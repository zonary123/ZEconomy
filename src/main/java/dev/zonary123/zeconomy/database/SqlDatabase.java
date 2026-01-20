package dev.zonary123.zeconomy.database;

import dev.zonary123.zeconomy.Models.Account;
import dev.zonary123.zeconomy.Models.Transaction;
import dev.zonary123.zeconomy.ZEconomy;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Implementación SQLite de DatabaseClient optimizada y segura
 */
public class SqlDatabase extends DatabaseClient {

  private Connection connection;

  @Override
  public void connect() {
    try {
      Class.forName("org.sqlite.JDBC");
      var dbConfig = ZEconomy.get().getConfig().get().getDatabase();
      connection = DriverManager.getConnection(dbConfig.getUrl());
      createTablesIfNotExists();
      getLogger().atInfo().log("[SqlDatabase] Connected to SQLite!");
    } catch (Exception e) {
      getLogger().atSevere().log("Failed to connect to SQLite: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  public void disconnect() {
    try {
      if (connection != null) {
        connection.close();
        getLogger().atInfo().log("[SqlDatabase] SQLite connection closed!");
      }
    } catch (Exception e) {
      getLogger().atSevere().log("Failed to close SQLite connection: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void createTablesIfNotExists() {
    String accountsTable = """
      CREATE TABLE IF NOT EXISTS accounts (
          uuid TEXT PRIMARY KEY,
          username TEXT NOT NULL
      );
      """;

    String currenciesTable = """
      CREATE TABLE IF NOT EXISTS currencies (
          uuid TEXT NOT NULL,
          currencyid TEXT NOT NULL,
          amount NUMERIC DEFAULT 0,
          PRIMARY KEY (uuid, currencyid),
          FOREIGN KEY (uuid) REFERENCES accounts(uuid) ON DELETE CASCADE
      );
      """;

    try (Statement stmt = connection.createStatement()) {
      stmt.executeUpdate(accountsTable);
      stmt.executeUpdate(currenciesTable);
      getLogger().atInfo().log("[SqlDatabase] Tables Accounts and Currencies created or verified!");
    } catch (Exception e) {
      getLogger().atSevere().log("Error creating tables: " + e.getMessage());
      e.printStackTrace();
    }
  }

  // -------------------------------------------------
  // Helpers para lambdas con SQLException centralizado
  // -------------------------------------------------

  @FunctionalInterface
  interface SQLConsumer<T> {
    void accept(T t) throws SQLException;
  }

  @FunctionalInterface
  interface SQLFunction<T, R> {
    R apply(T t) throws SQLException;
  }

  private static <T> void safeAccept(SQLConsumer<T> consumer, T t) {
    try {
      consumer.accept(t);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private static <T, R> R safeApply(SQLFunction<T, R> function, T t) {
    try {
      return function.apply(t);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Método genérico para ejecutar consultas con ResultSet opcional
   */
  private void query(String sql, SQLConsumer<PreparedStatement> paramSetter, Consumer<ResultSet> resultHandler) {
    try (PreparedStatement ps = connection.prepareStatement(sql)) {
      if (paramSetter != null) safeAccept(paramSetter, ps);

      if (resultHandler != null) {
        try (ResultSet rs = ps.executeQuery()) {
          resultHandler.accept(rs);
        }
      } else {
        ps.executeUpdate();
      }
    } catch (Exception e) {
      getLogger().atSevere().log("DB query error: " + e.getMessage());
      e.printStackTrace();
    }
  }

  // -------------------------------------------------
  // Helpers para PreparedStatement
  // -------------------------------------------------

  private void setString(PreparedStatement ps, int index, String value) {
    safeAccept(p -> p.setString(index, value), ps);
  }

  private void setBigDecimal(PreparedStatement ps, int index, BigDecimal value) {
    safeAccept(p -> p.setBigDecimal(index, value), ps);
  }

  private void setLong(PreparedStatement ps, int index, long value) {
    safeAccept(p -> p.setLong(index, value), ps);
  }

  // -------------------------------------------------
  // Account queries
  // -------------------------------------------------

  @Override
  public @Nullable Account findAccountByUuid(UUID uuid) {
    return findAccount("SELECT uuid, username FROM accounts WHERE uuid = ?", uuid.toString());
  }

  @Override
  public @Nullable Account findAccountByUsername(String username) {
    return findAccount("SELECT uuid, username FROM accounts WHERE username = ?", username);
  }

  private @Nullable Account findAccount(String sql, String param) {
    Account account = new Account();

    query(sql, ps -> setString(ps, 1, param), rs -> {
      try {
        if (rs.next()) {
          account.setUuid(UUID.fromString(rs.getString("uuid")));
          account.setUsername(rs.getString("username"));
        } else {
          account.setUuid(null);
        }
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    });

    if (account.getUuid() == null) return null;

    query("SELECT currencyid, amount FROM currencies WHERE uuid = ?", ps -> setString(ps, 1, account.getUuid().toString()), rs -> {
      while (true) {
        try {
          if (!rs.next()) break;
          account.setBalance(rs.getString("currencyid"), rs.getBigDecimal("amount"));
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      }
    });

    return account;
  }

  // -------------------------------------------------
  // Account updates
  // -------------------------------------------------

  @Override
  public boolean saveOrUpdateAccount(Account account) {
    return executeTransaction(() -> {
      query("""
        INSERT INTO accounts (uuid, username) VALUES (?, ?)
        ON CONFLICT(uuid) DO UPDATE SET username = excluded.username
        """, ps -> {
        setString(ps, 1, account.getUuid().toString());
        setString(ps, 2, account.getUsername());
      }, null);

      String sqlBalance = """
        INSERT INTO currencies (uuid, currencyid, amount)
        VALUES (?, ?, ?)
        ON CONFLICT(uuid, currencyid) DO UPDATE SET amount = excluded.amount
        """;

      try (PreparedStatement ps = connection.prepareStatement(sqlBalance)) {
        for (Map.Entry<String, BigDecimal> entry : account.getBalances().entrySet()) {
          setString(ps, 1, account.getUuid().toString());
          setString(ps, 2, entry.getKey());
          setBigDecimal(ps, 3, entry.getValue());
          ps.addBatch();
        }
        ps.executeBatch();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Override public boolean setBalance(UUID uuid, String currencyId, BigDecimal amount) {
    return executeTransaction(() -> {
      String sql = """
        INSERT INTO currencies (uuid, currencyid, amount)
        VALUES (?, ?, ?)
        ON CONFLICT(uuid, currencyid) DO UPDATE SET amount = excluded.amount
        """;

      query(sql, ps -> {
        setString(ps, 1, uuid.toString());
        setString(ps, 2, currencyId);
        setBigDecimal(ps, 3, amount);
      }, null);
    });
  }

  @Override
  public boolean deposit(UUID uuid, String currency, BigDecimal amount) {
    return executeTransaction(() -> {
      String sql = """
        INSERT INTO currencies (uuid, currencyid, amount)
        VALUES (?, ?, ?)
        ON CONFLICT(uuid, currencyid) DO UPDATE SET amount = amount + excluded.amount
        """;

      query(sql, ps -> {
        setString(ps, 1, uuid.toString());
        setString(ps, 2, currency);
        setBigDecimal(ps, 3, amount);
      }, null);
    });
  }

  @Override
  public boolean withdraw(UUID uuid, String currency, BigDecimal amount) {
    return executeTransaction(() -> {
      String sql = "UPDATE currencies SET amount = amount - ? WHERE uuid = ? AND currencyid = ? AND amount >= ?";

      query(sql, ps -> {
        setBigDecimal(ps, 1, amount);
        setString(ps, 2, uuid.toString());
        setString(ps, 3, currency);
        setBigDecimal(ps, 4, amount);
      }, null);
    });
  }

  @Override
  public List<Account> getTopBalance(int page, int pageSize, String currency) {
    List<Account> topAccounts = new java.util.ArrayList<>();
    String sql = """
          SELECT a.uuid, a.username, c.amount
          FROM accounts a
          JOIN currencies c ON a.uuid = c.uuid
          WHERE c.currencyid = ?
          ORDER BY c.amount DESC
          LIMIT ? OFFSET ?
      """;

    int offset = (page - 1) * pageSize;

    query(sql, ps -> {
      setString(ps, 1, currency);
      ps.setInt(2, pageSize);
      ps.setInt(3, offset);
    }, rs -> {
      while (true) {
        try {
          if (!rs.next()) break;
          UUID uuid = UUID.fromString(rs.getString("uuid"));
          String username = rs.getString("username");
          BigDecimal amount = rs.getBigDecimal("amount");

          Account account = new Account(uuid, username);
          account.setBalance(currency, amount); // Solo la moneda solicitada
          topAccounts.add(account);
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      }
    });

    return topAccounts;
  }


  @Override
  public void addTransaction(Transaction transaction) {
    query("""
      INSERT INTO transactions (uuid, currencyid, amount, type, timestamp)
      VALUES (?, ?, ?, ?, ?)
      """, ps -> {
      setString(ps, 1, transaction.getAccountId().toString());
      setString(ps, 2, transaction.getCurrencyId());
      setBigDecimal(ps, 3, transaction.getAmount());
      setString(ps, 4, transaction.getType().name());
      setLong(ps, 5, transaction.getTimestamp());
    }, null);
  }

  @Override public List<Transaction> getTransactions() {
    return List.of();
  }

  // -------------------------------------------------
  // Transaction wrapper
  // -------------------------------------------------

  /**
   * Ejecuta un bloque en transacción con rollback automático
   */
  private boolean executeTransaction(Runnable block) {
    try {
      connection.setAutoCommit(false);
      block.run();
      connection.commit();
      connection.setAutoCommit(true);
      return true;
    } catch (Exception e) {
      try {
        connection.rollback();
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
      getLogger().atSevere().log("Transaction error: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }
}

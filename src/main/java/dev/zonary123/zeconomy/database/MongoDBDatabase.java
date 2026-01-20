package dev.zonary123.zeconomy.database;

import dev.zonary123.libs.bson.Document;
import dev.zonary123.libs.mongodb.ConnectionString;
import dev.zonary123.libs.mongodb.MongoClientSettings;
import dev.zonary123.libs.mongodb.client.MongoClient;
import dev.zonary123.libs.mongodb.client.MongoClients;
import dev.zonary123.libs.mongodb.client.MongoCollection;
import dev.zonary123.libs.mongodb.client.MongoDatabase;
import dev.zonary123.libs.mongodb.client.model.Filters;
import dev.zonary123.libs.mongodb.client.model.UpdateOptions;
import dev.zonary123.zeconomy.Models.Account;
import dev.zonary123.zeconomy.Models.Transaction;
import dev.zonary123.zeconomy.ZEconomy;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB con balances dentro del documento de accounts
 */
public class MongoDBDatabase extends DatabaseClient {

  private MongoClient mongoClient;
  private MongoDatabase db;
  private MongoCollection<Document> accountsCollection;
  private MongoCollection<Document> transactionsCollection;

  @Override
  public void connect() {
    try {
      var config = ZEconomy.get().getConfig().get().getDatabase();

      MongoClientSettings settings = MongoClientSettings.builder()
        .applyConnectionString(new ConnectionString(config.getUrl()))
        .applicationName("ZEconomy")
        .build();
      mongoClient = MongoClients.create(settings);
      db = mongoClient.getDatabase(config.getDatabaseName());

      accountsCollection = db.getCollection("accounts");
      transactionsCollection = db.getCollection("transactions");

      // Índices para optimizar búsquedas
      accountsCollection.createIndex(new Document("uuid", 1));
      transactionsCollection.createIndex(new Document("uuid", 1));

      getLogger().atInfo().log("[MongoDBDatabase] Connected with accounts + transactions!");
    } catch (Exception e) {
      getLogger().atSevere().log("Failed to connect to MongoDB: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  public void disconnect() {
    if (mongoClient != null) {
      mongoClient.close();
      getLogger().atInfo().log("[MongoDBDatabase] MongoDB connection closed!");
    }
  }

  @Override
  public @Nullable Account findAccountByUuid(UUID uuid) {
    try {
      Document doc = accountsCollection.find(Filters.eq("uuid", uuid.toString())).first();
      if (doc != null) {
        Account account = new Account(uuid, doc.getString("username"));

        // Cargar todos los balances
        Document currencies = doc.get("currencies", Document.class);
        if (currencies != null) {
          for (Map.Entry<String, Object> entry : currencies.entrySet()) {
            account.setBalance(entry.getKey(), new BigDecimal(entry.getValue().toString()));
          }
        }
        return account;
      }
    } catch (Exception e) {
      getLogger().atSevere().log("Error finding account: " + e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

  @Override public @Nullable Account findAccountByUsername(String username) {
    try {
      Document doc = accountsCollection.find(Filters.eq("username", username)).first();
      if (doc != null) {
        UUID uuid = UUID.fromString(doc.getString("uuid"));
        Account account = new Account(uuid, username);

        // Cargar todos los balances
        Document currencies = doc.get("currencies", Document.class);
        if (currencies != null) {
          for (Map.Entry<String, Object> entry : currencies.entrySet()) {
            account.setBalance(entry.getKey(), new BigDecimal(entry.getValue().toString()));
          }
        }
        return account;
      }
    } catch (Exception e) {
      getLogger().atSevere().log("Error finding account by username: " + e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public boolean saveOrUpdateAccount(Account account) {
    try {
      Document update = new Document("$set", new Document("username", account.getUsername())
        .append("currencies", account.getBalances()));
      accountsCollection.updateOne(Filters.eq("uuid", account.getUuid().toString()), update,
        new UpdateOptions().upsert(true));
      return true;
    } catch (Exception e) {
      getLogger().atSevere().log("Error saving/updating account: " + e.getMessage());
      e.printStackTrace();
    }
    return false;
  }

  @Override public boolean setBalance(UUID uuid, String id, BigDecimal amount) {
    try {
      accountsCollection.updateOne(
        Filters.eq("uuid", uuid.toString()),
        new Document("$set", new Document("currencies." + id, amount)),
        new UpdateOptions().upsert(true)
      );
      return true;
    } catch (Exception e) {
      getLogger().atSevere().log("Error setting balance: " + e.getMessage());
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean deposit(UUID uuid, String currency, BigDecimal amount) {
    try {
      accountsCollection.updateOne(
        Filters.eq("uuid", uuid.toString()),
        new Document("$inc", new Document("currencies." + currency, amount)),
        new UpdateOptions().upsert(true)
      );
      return true;
    } catch (Exception e) {
      getLogger().atSevere().log("Error depositing: " + e.getMessage());
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean withdraw(UUID uuid, String currency, BigDecimal amount) {
    try {
      Document doc = accountsCollection.find(Filters.eq("uuid", uuid.toString())).first();
      if (doc != null) {
        Document currencies = doc.get("currencies", Document.class);
        BigDecimal current = currencies != null && currencies.containsKey(currency)
          ? new BigDecimal(currencies.get(currency).toString())
          : BigDecimal.ZERO;

        if (current.compareTo(amount) >= 0) {
          accountsCollection.updateOne(
            Filters.eq("uuid", uuid.toString()),
            new Document("$inc", new Document("currencies." + currency, amount.negate()))
          );
          return true;
        }
      }
    } catch (Exception e) {
      getLogger().atSevere().log("Error withdrawing: " + e.getMessage());
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public List<Account> getTopBalance(int page, int pageSize, String currency) {
    try {
      int skip = (page - 1) * pageSize;

      // Ordenar por la moneda en cuestión de mayor a menor
      var iterable = accountsCollection.find()
        .sort(new Document("currencies." + currency, -1)) // -1 = descendente
        .skip(skip)
        .limit(pageSize);

      List<Account> topAccounts = new java.util.ArrayList<>();

      for (Document doc : iterable) {
        UUID uuid = UUID.fromString(doc.getString("uuid"));
        String username = doc.getString("username");
        Account account = new Account(uuid, username);

        Document currencies = doc.get("currencies", Document.class);
        if (currencies != null) {
          for (Map.Entry<String, Object> entry : currencies.entrySet()) {
            account.setBalance(entry.getKey(), new BigDecimal(entry.getValue().toString()));
          }
        }

        topAccounts.add(account);
      }

      return topAccounts;
    } catch (Exception e) {
      getLogger().atSevere().log("Error getting top balances: " + e.getMessage());
      e.printStackTrace();
    }

    return List.of();
  }


  @Override
  public void addTransaction(Transaction transaction) {
    try {
      transactionsCollection.insertOne(transaction.toDocument());
    } catch (Exception e) {
      getLogger().atSevere().log("Error adding transaction: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @Override public List<Transaction> getTransactions() {
    return List.of();
  }
}

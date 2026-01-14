package dev.zonary123.api;

import dev.zonary123.Models.Account;
import dev.zonary123.ZEconomy;
import dev.zonary123.database.DatabaseClient;

import java.math.BigDecimal;
import java.util.UUID;

public class ZEconomyApi {
  /**
   * Get an account from cache by UUID.
   *
   * @param uuid The UUID of the account.
   * @return The account.
   */
  public static Account getAccount(UUID uuid) {
    return DatabaseClient.ACCOUNTS.get(uuid);
  }

  /**
   * Find an account by UUID.
   *
   * @param uuid The UUID of the account.
   * @return The account.
   */
  public static Account findAccount(UUID uuid) {
    DatabaseClient database = ZEconomy.getDatabase();
    return database.findAccount(uuid);
  }

  /**
   * Get the balance of an account.
   *
   * @param uuid     The UUID of the account.
   * @param currency The currency to check the balance for.
   * @return The balance of the account.
   */
  public static BigDecimal getBalance(UUID uuid, String currency) {
    DatabaseClient database = ZEconomy.getDatabase();
    Account account = database.getAccount(uuid);
    if (account == null) {
      account = database.findAccount(uuid);
      if (account == null) return BigDecimal.ZERO;
    }
    return account.getBalance(currency);
  }

  /**
   * Deposit an amount to an account.
   *
   * @param uuid     The UUID of the account.
   * @param currency The currency to deposit.
   * @param amount   The amount to deposit.
   * @return True if the deposit was successful, false otherwise.
   */
  public static boolean deposit(UUID uuid, String currency, BigDecimal amount) {
    DatabaseClient database = ZEconomy.getDatabase();
    Account account = database.getAccount(uuid);
    if (account == null) {
      return database.deposit(uuid, currency, amount);
    } else {
      return account.deposit(currency, amount);
    }
  }

  /**
   * Withdraw an amount from an account.
   *
   * @param uuid     The UUID of the account.
   * @param currency The currency to withdraw.
   * @param amount   The amount to withdraw.
   * @return True if the withdrawal was successful, false otherwise.
   */
  public static boolean withdraw(UUID uuid, String currency, BigDecimal amount) {
    DatabaseClient database = ZEconomy.getDatabase();
    Account account = database.getAccount(uuid);
    if (account == null) {
      return database.withdraw(uuid, currency, amount);
    } else {
      return account.withdraw(currency, amount);
    }
  }
}

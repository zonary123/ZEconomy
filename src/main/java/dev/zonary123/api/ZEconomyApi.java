package dev.zonary123.api;

import dev.zonary123.Config.CCurrency;
import dev.zonary123.Models.Account;
import dev.zonary123.Models.Currency;
import dev.zonary123.ZEconomy;
import dev.zonary123.database.DatabaseClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ZEconomyApi {
  /**
   * Get an account from cache by UUID.
   *
   * @param uuid The UUID of the account.
   *
   * @return The account.
   */
  public static Account getAccount(UUID uuid) {
    return DatabaseClient.ACCOUNTS.get(uuid);
  }

  /**
   * Find an account by UUID.
   *
   * @param uuid The UUID of the account.
   *
   * @return The account.
   */
  public static Account findAccount(UUID uuid) {
    DatabaseClient database = ZEconomy.getDatabase();
    return database.findAccount(uuid);
  }

  /**
   * Get the balance of an account.
   *
   * @param uuid       The UUID of the account.
   * @param currencyId The currency to check the balance for.
   *
   * @return The balance of the account.
   */
  public static BigDecimal getBalance(UUID uuid, String currencyId) {
    DatabaseClient database = ZEconomy.getDatabase();
    Account account = database.getAccount(uuid);
    Currency currency = getCurrency(currencyId);
    if (account == null) {
      account = database.findAccount(uuid);
      if (account == null) return BigDecimal.ZERO;
    }
    return account.getBalance(currency.getId());
  }

  /**
   * Deposit an amount to an account.
   *
   * @param uuid       The UUID of the account.
   * @param currencyId The currency to deposit.
   * @param amount     The amount to deposit.
   *
   * @return True if the deposit was successful, false otherwise.
   */
  public static boolean deposit(UUID uuid, String currencyId, BigDecimal amount) {
    DatabaseClient database = ZEconomy.getDatabase();
    Account account = database.getAccount(uuid);
    Currency currency = getCurrency(currencyId);
    if (account == null) {
      return database.deposit(uuid, currency.getId(), amount);
    } else {
      return account.deposit(currency.getId(), amount);
    }
  }

  /**
   * Withdraw an amount from an account.
   *
   * @param uuid       The UUID of the account.
   * @param currencyId The currency to withdraw.
   * @param amount     The amount to withdraw.
   *
   * @return True if the withdrawal was successful, false otherwise.
   */
  public static boolean withdraw(UUID uuid, String currencyId, BigDecimal amount) {
    DatabaseClient database = ZEconomy.getDatabase();
    Account account = database.getAccount(uuid);
    Currency currency = getCurrency(currencyId);
    if (account == null) {
      return database.withdraw(uuid, currency.getId(), amount);
    } else {
      return account.withdraw(currency.getId(), amount);
    }
  }

  /**
   * Get a currency by its code.
   *
   * @param currencyId The code of the currency.
   *
   * @return The currency. If not exist returns the primary currency.
   */
  public static Currency getCurrency(String currencyId) {
    return Objects.isNull(currencyId)
      ? CCurrency.PRIMARY_CURRENCY
      : CCurrency.CURRENCIES.getOrDefault(currencyId, CCurrency.PRIMARY_CURRENCY);
  }

  /**
   * Check if a currency exists.
   *
   * @param currencyId The code of the currency.
   *
   * @return True if the currency exists, false otherwise.
   */
  public static boolean existsCurrency(String currencyId) {
    return CCurrency.CURRENCIES.containsKey(currencyId);
  }

  /**
   * Get all currencies.
   *
   * @return A map of all currencies.
   */
  public static Map<String, Currency> getCurrencies() {
    return CCurrency.CURRENCIES;
  }
}

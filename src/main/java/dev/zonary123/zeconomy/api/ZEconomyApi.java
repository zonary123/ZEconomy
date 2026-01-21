package dev.zonary123.zeconomy.api;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.zonary123.zeconomy.Config.CCurrency;
import dev.zonary123.zeconomy.Models.Account;
import dev.zonary123.zeconomy.Models.Currency;
import dev.zonary123.zeconomy.Models.Transaction;
import dev.zonary123.zeconomy.Models.TransactionTypes;
import dev.zonary123.zeconomy.ZEconomy;
import dev.zonary123.zeconomy.database.DatabaseClient;
import dev.zonary123.zeconomy.utils.Utils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
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
  public static Account findAccountByUuid(UUID uuid) {
    DatabaseClient database = ZEconomy.getDatabase();
    Account cachedAccount = getAccount(uuid);
    return cachedAccount != null ? cachedAccount : database.findAccountByUuid(uuid);
  }

  /**
   * Find an account by username.
   *
   * @param username The username of the account.
   * @return The account.
   */
  public static Account findAccountByUsername(String username) {
    DatabaseClient database = ZEconomy.getDatabase();
    return database.findAccountByUsername(username);
  }

  /**
   * Get the balance of an account.
   *
   * @param uuid       The UUID of the account.
   * @param currencyId The currency to check the balance for.
   * @return The balance of the account.
   */
  public static BigDecimal getBalance(UUID uuid, String currencyId) {
    DatabaseClient database = ZEconomy.getDatabase();
    Account account = database.getAccount(uuid);
    Currency currency = getCurrency(currencyId);
    if (account != null) return account.getBalance(currency.getId());
    account = database.findAccountByUuid(uuid);
    if (account == null) return BigDecimal.valueOf(currency.getDefaultBalance());
    return account.getBalance(currency.getId());
  }

  /**
   * Set the balance of an account.
   *
   * @param uuid       The UUID of the account.
   * @param currencyId The currency to set the balance for.
   * @param amount     The amount to set the balance to.
   * @return True if the balance was set successfully, false otherwise.
   * @apiNote This method is valid, but it is recommended to use
   * {@link #setBalance(UUID, String, BigDecimal, String)}
   * to provide a reason for the transaction.
   */
  public static boolean setBalance(UUID uuid, String currencyId, BigDecimal amount) {
    return setBalance(uuid, currencyId, amount, "API Set Balance");
  }

  /**
   * Set the balance of an account with a reason.
   */
  public static boolean setBalance(UUID uuid, String currencyId, BigDecimal amount, String reason) {
    DatabaseClient database = ZEconomy.getDatabase();
    Account account = database.getAccount(uuid);
    Currency curr = getCurrency(currencyId);
    Transaction transaction = Utils.createTransaction(uuid, curr.getId(), TransactionTypes.SET, amount, reason);
    boolean result;
    if (account == null) {
      result = database.existAccount(uuid);
      if (!result) return false;
      result = database.setBalance(uuid, curr.getId(), amount);
      database.addTransaction(transaction);
    } else {
      database.addTransaction(transaction);
      account.setBalance(curr.getId(), amount);
      result = true;
    }
    return result;
  }

  /**
   * Deposit an amount to an account.
   *
   * @apiNote This method is valid, but it is recommended to use
   * {@link #deposit(UUID, String, BigDecimal, String)}
   * to specify a transaction reason.
   */
  public static boolean deposit(UUID uuid, String currencyId, BigDecimal amount) {
    return deposit(uuid, currencyId, amount, "API Deposit");
  }

  /**
   * Deposit an amount to an account with a reason.
   */
  public static boolean deposit(UUID uuid, String currencyId, BigDecimal amount, String reason) {
    DatabaseClient database = ZEconomy.getDatabase();
    Account account = database.getAccount(uuid);
    Currency currency = getCurrency(currencyId);
    Transaction transaction = Utils.createTransaction(uuid, currency.getId(), TransactionTypes.DEPOSIT, amount, reason);
    boolean result;
    if (account == null) {
      result = database.existAccount(uuid);
      if (!result) return false;
      database.deposit(uuid, currency.getId(), amount);
      database.addTransaction(transaction);
    } else {
      database.addTransaction(transaction);
      result = account.deposit(currency.getId(), amount);
    }
    return result;
  }

  /**
   * Withdraw an amount from an account.
   *
   * @apiNote This method is valid, but it is recommended to use
   * {@link #withdraw(UUID, String, BigDecimal, String)}
   * to specify a transaction reason.
   */
  public static boolean withdraw(UUID uuid, String currencyId, BigDecimal amount) {
    return withdraw(uuid, currencyId, amount, "API Withdraw");
  }

  /**
   * Withdraw an amount from an account with a reason.
   */
  public static boolean withdraw(UUID uuid, String currencyId, BigDecimal amount, String reason) {
    DatabaseClient database = ZEconomy.getDatabase();
    Account account = database.getAccount(uuid);
    Currency currency = getCurrency(currencyId);
    boolean result;
    Transaction transaction = Utils.createTransaction(uuid, currency.getId(), TransactionTypes.WITHDRAW, amount, reason);
    if (account == null) {
      result = database.existAccount(uuid);
      if (!result) return false;
      database.withdraw(uuid, currency.getId(), amount);
      database.addTransaction(transaction);
    } else {
      result = account.withdraw(currency.getId(), amount);
      if (result) database.addTransaction(transaction);
    }
    return result;
  }

  /**
   * Get a currency by its code.
   */
  public static Currency getCurrency(String currencyId) {
    return Objects.isNull(currencyId)
      ? CCurrency.PRIMARY_CURRENCY
      : CCurrency.CURRENCIES.getOrDefault(currencyId, CCurrency.PRIMARY_CURRENCY);
  }

  private static final Cache<BigDecimal, String> FORMAT_CACHE = Caffeine.newBuilder()
    .maximumSize(1000)
    .build();

  /**
   * Get the formatted string of a currency amount.
   */
  public static String getFormat(String currencyId, BigDecimal amount) {
    Currency currency = getCurrency(currencyId);
    return FORMAT_CACHE.get(amount, currency::getFormat);
  }

  /**
   * Check if a currency exists.
   */
  public static boolean existsCurrency(String currencyId) {
    return CCurrency.CURRENCIES.containsKey(currencyId);
  }

  /**
   * Get all currencies.
   */
  public static Map<String, Currency> getCurrencies() {
    return CCurrency.CURRENCIES;
  }

  /**
   * Get the default currency.
   */
  public static Currency getDefaultCurrency() {
    return CCurrency.PRIMARY_CURRENCY;
  }
}

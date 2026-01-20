package dev.zonary123.zeconomy.database;

import com.hypixel.hytale.logger.HytaleLogger;
import dev.zonary123.zeconomy.Models.Account;
import dev.zonary123.zeconomy.Models.Transaction;
import dev.zonary123.zeconomy.ZEconomy;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Carlos Varas Alonso - 14/01/2026 10:47
 */
public abstract class DatabaseClient {
  /**
   * Get the logger.
   *
   * @return The logger.
   */
  protected static HytaleLogger getLogger() {
    return ZEconomy.get().getLogger();
  }

  /**
   * Cache of accounts.
   * <p>
   * Only for online players accounts.
   * <p>
   * If the player disconnects, the account must be removed from the cache.
   * <p>
   * Key: UUID of the account.
   * Value: Account.
   */
  public static final Map<UUID, Account> ACCOUNTS = new HashMap<>();

  /**
   * Save all accounts in cache to the database.
   */
  public static void saveAll() {
    int savedAccounts = 0;

    var accounts = ACCOUNTS.values();
    for (Account account : accounts) {
      if (account.save())
        savedAccounts++;
    }
    if (savedAccounts > 0)
      getLogger().atInfo().log(
        "Saving %d accounts to the database...",
        savedAccounts
      );
  }

  /**
   * Connect to the database.
   */
  public abstract void connect();

  /**
   * Disconnect from the database.
   */
  public abstract void disconnect();

  /**
   * Get an account from cache by UUID.
   *
   * @param uuid The UUID of the account.
   *
   * @return The account.
   */
  @Nullable
  public Account getAccount(UUID uuid) {
    return ACCOUNTS.get(uuid);
  }

  /**
   * Find an account by UUID.
   *
   * @param uuid The UUID of the account.
   *
   * @return The account.
   */
  @Nullable
  public abstract Account findAccountByUuid(UUID uuid);

  /**
   * Find an account by username.
   *
   * @param username The username of the account.
   *
   * @return The account.
   */
  @Nullable
  public abstract Account findAccountByUsername(String username);

  /**
   * Save or update an account.
   *
   * @param account The account to save or update.
   *
   * @return True if the account was saved or updated, false otherwise.
   */
  public abstract boolean saveOrUpdateAccount(Account account);

  /**
   * Set the balance of an account.
   *
   * @param uuid   The UUID of the account.
   * @param id     The currency to set.
   * @param amount The amount to set.
   *
   * @return True if the balance was set, false otherwise.
   */
  public abstract boolean setBalance(UUID uuid, String id, BigDecimal amount);

  /**
   * Deposit an amount to an account.
   *
   * @param uuid     The UUID of the account.
   * @param currency The currency to deposit.
   * @param amount   The amount to deposit.
   *
   * @return True if the deposit was successful, false otherwise.
   */
  public abstract boolean deposit(UUID uuid, String currency, BigDecimal amount);

  /**
   * Withdraw an amount from an account.
   *
   * @param uuid     The UUID of the account.
   * @param currency The currency to withdraw.
   * @param amount   The amount to withdraw.
   *
   * @return True if the withdrawal was successful, false otherwise.
   */
  public abstract boolean withdraw(UUID uuid, String currency, BigDecimal amount);

  /**
   * Get a paginated list of accounts.
   *
   * @param page     The page number.
   * @param pageSize The number of accounts per page.
   *
   * @return A list of accounts.
   */
  public abstract List<Account> getTopBalance(int page, int pageSize, String currency);

  /**
   * Add a transaction to the database.
   *
   * @param transaction The transaction to add.
   */
  public abstract void addTransaction(Transaction transaction);

  /**
   * Get all transactions from the database.
   *
   * @return A list of transactions.
   */
  public abstract List<Transaction> getTransactions();

  /**
   * Check if an account exists.
   *
   * @param uuid The UUID of the account.
   *
   * @return True if the account exists, false otherwise.
   */
  public boolean existAccount(UUID uuid) {
    return findAccountByUuid(uuid) != null;
  }


}

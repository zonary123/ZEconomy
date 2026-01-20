package dev.zonary123.database;

import com.hypixel.hytale.logger.HytaleLogger;
import dev.zonary123.Models.Account;
import dev.zonary123.Models.Transaction;
import dev.zonary123.ZEconomy;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.HashMap;
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
  public abstract Account findAccount(UUID uuid);

  /**
   * Save or update an account.
   *
   * @param account The account to save or update.
   *
   * @return True if the account was saved or updated, false otherwise.
   */
  public abstract boolean saveOrUpdateAccount(Account account);

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
   * Add a transaction to the database.
   *
   * @param transaction The transaction to add.
   */
  public abstract void addTransaction(Transaction transaction);

  /**
   * Check if an account exists.
   *
   * @param uuid The UUID of the account.
   *
   * @return True if the account exists, false otherwise.
   */
  public boolean existAccount(UUID uuid) {
    return findAccount(uuid) != null;
  }

}

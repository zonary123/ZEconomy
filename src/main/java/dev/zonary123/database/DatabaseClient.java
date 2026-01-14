package dev.zonary123.database;

import dev.zonary123.Models.Account;

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
  public static final Map<UUID, Account> ACCOUNTS = new HashMap<>();

  /**
   * Get an account from cache by UUID.
   *
   * @param uuid The UUID of the account.
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
   * @return The account.
   */
  @Nullable
  public abstract Account findAccount(UUID uuid);

  /**
   * Save or update an account.
   *
   * @param account The account to save or update.
   * @return True if the account was saved or updated, false otherwise.
   */
  public abstract boolean saveOrUpdateAccount(Account account);

  public abstract boolean deposit(UUID uuid, String currency, BigDecimal amount);
}

package dev.zonary123.database;

import dev.zonary123.Models.Account;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Carlos Varas Alonso - 14/01/2026 10:47
 */
public abstract class DatabaseClient {
  public static final Map<UUID, Account> ACCOUNTS = new HashMap<>();

  public Account getCacheAccount(UUID uuid) {
    return ACCOUNTS.get(uuid);
  }
}

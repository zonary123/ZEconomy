package dev.zonary123.database;

import dev.zonary123.Models.Account;
import dev.zonary123.Models.Transaction;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 * @author Carlos Varas Alonso - 16/01/2026 11:43
 */
public class SqlDatabase extends DatabaseClient {
  @Override public void connect() {
    getLogger().atInfo().log(
      "[SqlDatabase] Connected to SQL database!"
    );
  }

  @Override public void disconnect() {
    getLogger().atInfo().log(
      "[SqlDatabase] Disconnected from SQL database!"
    );
  }

  @Override public @Nullable Account findAccount(UUID uuid) {
    return null;
  }

  @Override public boolean saveOrUpdateAccount(Account account) {
    return false;
  }

  @Override public boolean deposit(UUID uuid, String currency, BigDecimal amount) {
    return false;
  }

  @Override public boolean withdraw(UUID uuid, String currency, BigDecimal amount) {
    return false;
  }

  @Override public void addTransaction(Transaction transaction) {

  }
}

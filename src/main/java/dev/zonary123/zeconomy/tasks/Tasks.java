package dev.zonary123.zeconomy.tasks;

import dev.zonary123.zeconomy.ZEconomy;
import dev.zonary123.zeconomy.database.DatabaseClient;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author Carlos Varas Alonso - 17/01/2026 11:14
 */
public class Tasks {
  public static void register() {
    ZEconomy.SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(DatabaseClient::saveAll, 0L, 30L, TimeUnit.SECONDS);
  }
}

package dev.zonary123.zeconomy.tasks;

import dev.zonary123.zeconomy.database.DatabaseClient;
import dev.zonary123.zutils.utils.async.UtilsAsync;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author Carlos Varas Alonso - 17/01/2026 11:14
 */
public class Tasks {
  public static void register() {
    UtilsAsync.getContext("ZEconomy").scheduleAtFixedRate(() -> {
      try {
        DatabaseClient.saveAll();
      } catch (Exception e) {
        e.printStackTrace();

      }
      return null;
    }, 0L, 30L, TimeUnit.SECONDS);
  }
}

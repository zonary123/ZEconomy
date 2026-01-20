package dev.zonary123.zeconomy.database;

import dev.zonary123.zeconomy.ZEconomy;

public class DatabaseFactory {
  public static DatabaseClient createDatabaseClient() {
    DatabaseClient client = ZEconomy.getDatabase();
    if (client != null) client.disconnect();
    client = switch (ZEconomy.get().getConfig().get().getDatabase().getDatabaseType()) {
      case MONGODB -> new MongoDBDatabase();
      case SQL -> new SqlDatabase();
      default -> new SqlDatabase();
    };
    client.connect();
    return client;
  }
}

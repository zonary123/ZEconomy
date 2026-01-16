package dev.zonary123.database;

import dev.zonary123.ZEconomy;

public class DatabaseFactory {
  public static DatabaseClient createDatabaseClient() {
    DatabaseClient client = ZEconomy.getDatabase();
    if (client != null) client.disconnect();
    client = switch (ZEconomy.get().getConfig().get().getDatabase().getDatabaseType()) {
      case MONGODB -> new MongoDatabase();
      case SQL -> new SqlDatabase();
      default -> new SqlDatabase();
    };
    client.connect();
    return client;
  }
}

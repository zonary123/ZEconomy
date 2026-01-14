package dev.zonary123.database;

public class DatabaseFactory {
  public static DatabaseClient createDatabaseClient() {
    return new JsonDatabase();
  }
}

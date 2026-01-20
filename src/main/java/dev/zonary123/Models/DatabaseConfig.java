package dev.zonary123.Models;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import lombok.Data;

/**
 *
 * @author Carlos Varas Alonso - 16/01/2026 11:42
 */
@Data
public class DatabaseConfig {
  public static final BuilderCodec<DatabaseConfig> CODEC;
  private DatabaseType databaseType = DatabaseType.SQL;
  private String url = "jdbc:sqlite:database.db";
  private String databaseName = "ZEconomy";
  private String username = "";
  private String password = "";


  static {
    CODEC = BuilderCodec.builder(DatabaseConfig.class, DatabaseConfig::new)
      .append(
        new KeyedCodec<>("Url", Codec.STRING),
        DatabaseConfig::setUrl, DatabaseConfig::getUrl
      )
      .add()
      .append(
        new KeyedCodec<>("DatabaseName", Codec.STRING),
        DatabaseConfig::setDatabaseName, DatabaseConfig::getDatabaseName
      )
      .add()
      .append(
        new KeyedCodec<>("Username", Codec.STRING),
        DatabaseConfig::setUsername, DatabaseConfig::getUsername
      )
      .add()
      .append(
        new KeyedCodec<>("Password", Codec.STRING),
        DatabaseConfig::setPassword, DatabaseConfig::getPassword
      )
      .add()
      .append(
        new KeyedCodec<>("DatabaseType", Codec.STRING),
        (config, type) -> config.setDatabaseType(DatabaseType.valueOf(type.toUpperCase())),
        config -> config.getDatabaseType().name()
      )
      .add()
      .build();
  }
}

package dev.zonary123.zeconomy.Config;

import dev.zonary123.zeconomy.ZEconomy;
import dev.zonary123.zutils.models.DatabaseConfig;
import dev.zonary123.zutils.utils.UtilsFile;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.nio.file.Path;

/**
 *
 * @author Carlos Varas Alonso - 14/01/2026 10:02
 */
@Getter
@Setter
public class Config {

  private boolean debug;
  private String lang = "en_us";
  private DatabaseConfig database = DatabaseConfig.builder()
    .type(DatabaseConfig.DatabaseType.SQL)
    .url("jdbc:sqlite:mods/Zonary123_ZEconomy/database.db")
    .database("ZEconomy")
    .build();

  public Config init() {
    Path path = ZEconomy.getPath();
    File file = path.resolve("config.json").toFile();
    if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
    Config config = new Config();
    try {
      config = UtilsFile.read(file.toPath(), Config.class);
      if (config == null) config = new Config();
      UtilsFile.write(file.toPath(), config);
    } catch (Exception _) {
    }
    return config;
  }
}

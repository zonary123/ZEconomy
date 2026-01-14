package dev.zonary123.Config;

import com.google.gson.Gson;
import dev.zonary123.Models.Currency;
import dev.zonary123.ZEconomy;
import dev.zonary123.utils.Utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class CCurrency {
  public static final Map<String, Currency> CURRENCIES = Map.of(
    "ZEM", new Currency("ZEM", "Zonary Economy Money", "$%.2f", 100.0, true),
    "GEM", new Currency("GEM", "Gonary Economy Money", "G$%.2f", 50.0, false)
  );
  public static Currency PRIMARY_CURRENCY;

  public static void init() {
    CURRENCIES.clear();
    Path currencyFile = ZEconomy.getInstance().getFile().resolve("currencies");
    if (!currencyFile.toFile().exists()) {
      createDefault();
    } else {
      var files = currencyFile.toFile().listFiles();
      if (files != null) {
        for (File file : files) {
          Gson gson = Utils.GSON;
          try {
            Currency currency = gson.fromJson(Utils.readFileToString(file), Currency.class);
            if (currency.isPrimary()) PRIMARY_CURRENCY = currency;
            CURRENCIES.put(currency.getId(), currency);
          } catch (Exception e) {
            e.printStackTrace();

          }
        }
      }
    }
  }

  private static void createDefault() {
    var map = Map.of(
      "ZEM", new Currency("coins", "Coins", "Coins %amount%", 100.0, true),
      "GEM", new Currency("gems", "Gems", "Gems %amount%", 50.0, false)
    );
    Path currencyFile = ZEconomy.getInstance().getFile().resolve("currencies");
    if (!currencyFile.toFile().exists()) {
      currencyFile.toFile().mkdirs();
    }

    for (var entry : map.entrySet()) {
      File file = currencyFile.resolve(entry.getKey() + ".json").toFile();
      try {
        if (!file.exists()) file.createNewFile();
        Gson gson = Utils.GSON;
        String json = gson.toJson(entry.getValue());
        Files.writeString(file.toPath(), json);
        if (entry.getValue().isPrimary()) PRIMARY_CURRENCY = entry.getValue();
        CURRENCIES.put(entry.getKey(), entry.getValue());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}

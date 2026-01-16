package dev.zonary123.Config;

import com.google.gson.Gson;
import dev.zonary123.Models.Currency;
import dev.zonary123.ZEconomy;
import dev.zonary123.utils.Utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CCurrency {

  public static final Map<String, Currency> CURRENCIES = new LinkedHashMap<>();
  public static Currency PRIMARY_CURRENCY;

  public static void init() {
    CURRENCIES.clear();
    PRIMARY_CURRENCY = null;

    Path currencyDir = ZEconomy.get().getDataDirectory().resolve("currencies");

    ZEconomy.get().getLogger().atInfo().log(
      "[CCurrency] Loading currencies from: " + currencyDir.toAbsolutePath()
    );

    if (!Files.exists(currencyDir)) {
      createDefault(currencyDir);
      return;
    }

    File[] files = currencyDir.toFile().listFiles((dir, name) -> name.endsWith(".json"));
    if (files == null || files.length == 0) {
      createDefault(currencyDir);
      return;
    }

    Gson gson = Utils.GSON;

    for (File file : files) {
      try {
        Currency currency = gson.fromJson(
          Files.readString(file.toPath()),
          Currency.class
        );
        String id = file.getName().replace(".json", "");
        if (currency == null) continue;
        if (currency.getId() == null) currency.setId(id);

        Files.writeString(file.toPath(), gson.toJson(currency));
        CURRENCIES.put(currency.getId(), currency);

        if (currency.isPrimary()) {
          PRIMARY_CURRENCY = currency;
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    // Fallback si ninguna es primaria
    if (PRIMARY_CURRENCY == null && !CURRENCIES.isEmpty()) PRIMARY_CURRENCY = CURRENCIES.values().iterator().next();
    Map<String, Currency> sorted = CURRENCIES.entrySet()
      .stream()
      .sorted(Map.Entry.comparingByValue(
        Comparator.comparingInt(Currency::getSort)
      ))
      .collect(Collectors.toMap(
        Map.Entry::getKey,
        Map.Entry::getValue,
        (a, b) -> a,
        LinkedHashMap::new
      ));

    CURRENCIES.clear();
    CURRENCIES.putAll(sorted);

  }

  private static void createDefault(Path currencyDir) {
    try {
      Files.createDirectories(currencyDir);
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    Map<String, Currency> defaults = Map.of(
      "coins", new Currency("coins", "Coins", "Coins %amount%", 100.0, true),
      "gems", new Currency("gems", "Gems", "Gems %amount%", 50.0, false)
    );

    Gson gson = Utils.GSON;

    for (Currency currency : defaults.values()) {
      File file = currencyDir.resolve(currency.getId() + ".json").toFile();

      try {
        Files.writeString(file.toPath(), gson.toJson(currency));
        CURRENCIES.put(currency.getId(), currency);

        if (currency.isPrimary()) {
          PRIMARY_CURRENCY = currency;
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}

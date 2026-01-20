package dev.zonary123.utils;

import com.google.gson.Gson;
import dev.zonary123.Models.Transaction;
import dev.zonary123.Models.TransactionTypes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.UUID;

public abstract class Utils {
  public static final Gson GSON = new Gson()
    .newBuilder()
    .setPrettyPrinting()
    .create();

  public static String readFileToString(File file) {
    StringBuilder contentBuilder = new StringBuilder();
    try (var br = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = br.readLine()) != null) {
        contentBuilder.append(line).append("\n");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return contentBuilder.toString();
  }

  public static Transaction createTransaction(UUID accountId, String currencyId, TransactionTypes type,
                                              BigDecimal amount, String reason) {
    return Transaction.builder()
      .id(UUID.randomUUID())
      .type(type)
      .reason(reason)
      .accountId(accountId)
      .processed(false)
      .currencyId(currencyId)
      .amount(amount)
      .build();
  }
}

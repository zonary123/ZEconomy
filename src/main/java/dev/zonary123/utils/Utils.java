package dev.zonary123.utils;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

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
}

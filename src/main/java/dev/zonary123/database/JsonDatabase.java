package dev.zonary123.database;

import com.hypixel.hytale.codec.ExtraInfo;
import dev.zonary123.Models.Account;
import dev.zonary123.ZEconomy;
import org.bson.BsonDocument;
import org.bson.json.JsonWriterSettings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 *
 * @author Carlos Varas Alonso - 14/01/2026 10:49
 */
public class JsonDatabase extends DatabaseClient {
  @Override
  public Account findAccount(UUID uuid) {
    try {
      var plugin = ZEconomy.getInstance();
      Path folder = plugin.getDataDirectory().resolve("players");

      Path file = folder.resolve(uuid.toString() + ".json");

      if (!Files.exists(file)) return null;

      String json = Files.readString(file);

      BsonDocument bson = BsonDocument.parse(json);

      return Account.CODEC.decode(bson, ExtraInfo.THREAD_LOCAL.get());
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public boolean saveOrUpdateAccount(Account account) {
    try {
      UUID uuid = account.getUuid();
      // Obtén la instancia del plugin y la carpeta de datos
      var plugin = ZEconomy.getInstance();
      Path folder = plugin.getDataDirectory().resolve("players");

      // Asegúrate de que la carpeta exista
      if (!Files.exists(folder)) {
        Files.createDirectories(folder);
      }

      // Crea el path para guardar la cuenta, usando el UUID como nombre
      Path file = folder.resolve(uuid.toString() + ".json");

      // Serializa la account a BSON y luego a JSON
      BsonDocument bson = Account.CODEC.encode(account, ExtraInfo.THREAD_LOCAL.get());
      String json = bson.toJson(JsonWriterSettings.builder().indent(true).build());

      // Guarda el JSON en el archivo
      Files.writeString(file, json);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }
}

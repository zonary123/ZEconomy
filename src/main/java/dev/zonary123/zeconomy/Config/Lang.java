package dev.zonary123.zeconomy.Config;

import dev.zonary123.zeconomy.ZEconomy;
import dev.zonary123.zutils.utils.UtilsFile;
import lombok.Data;

import java.io.File;
import java.nio.file.Path;

/**
 *
 * @author Carlos Varas Alonso - 18/01/2026 0:02
 */
@Data
public class Lang {
  private String prefix;
  private String titleUI = "&Balances";

  public Lang init() {
    Path path = ZEconomy.getPath();
    File file = path.resolve("lang").resolve(ZEconomy.getConfig().getLang()).toFile();
    if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
    Lang l = new Lang();
    try {
      l = UtilsFile.read(file.toPath(), Lang.class);
      if (l == null) l = new Lang();
      UtilsFile.write(file.toPath(), l);
    } catch (Exception _) {
    }
    return l;
  }
}

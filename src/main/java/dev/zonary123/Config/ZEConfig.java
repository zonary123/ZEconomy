package dev.zonary123.Config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import dev.zonary123.Models.DatabaseConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 *
 * @author Carlos Varas Alonso - 14/01/2026 10:02
 */
@Getter
@Setter
public class ZEConfig {
  public static final BuilderCodec<ZEConfig> CODEC = BuilderCodec.builder(ZEConfig.class, ZEConfig::new)
    .append(
      new KeyedCodec<>("Debug", Codec.BOOLEAN),
      ZEConfig::setDebug, ZEConfig::isDebug
    )
    .add()
    .append(
      new KeyedCodec<>("Database", DatabaseConfig.CODEC),
      (cfg, db) -> cfg.setDatabase(Objects.requireNonNullElseGet(db, DatabaseConfig::new)),
      cfg -> Objects.requireNonNullElseGet(cfg.getDatabase(), DatabaseConfig::new)
    )
    .add()
    .build();

  private boolean debug;
  private DatabaseConfig database;
}

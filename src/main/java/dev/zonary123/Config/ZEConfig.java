package dev.zonary123.Config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import lombok.Getter;
import lombok.Setter;

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
    .build();

  private boolean debug;
}

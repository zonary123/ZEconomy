package dev.zonary123.Models;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Currency {
  public static final BuilderCodec<Currency> CODEC;
  private transient String id;
  private boolean primary = false;
  private String name;
  private String format;
  private double defaultBalance;


  public Currency(boolean primary) {
    this();
    this.primary = primary;
  }

  public Currency(String id, String name, String format, double defaultBalance, boolean primary) {
    this.id = id;
    this.name = name;
    this.format = format;
    this.defaultBalance = defaultBalance;
    this.primary = primary;
  }


  static {
    CODEC = BuilderCodec.builder(Currency.class, Currency::new)
      .append(
        new KeyedCodec<>("Primary", Codec.BOOLEAN),
        Currency::setPrimary, Currency::isPrimary
      )
      .add()
      .append(
        new KeyedCodec<>("Name", Codec.STRING),
        Currency::setName, Currency::getName
      )
      .add()
      .append(
        new KeyedCodec<>("Format", Codec.STRING),
        Currency::setFormat, Currency::getFormat
      )
      .add()
      .append(
        new KeyedCodec<>("DefaultBalance", Codec.DOUBLE),
        Currency::setDefaultBalance, Currency::getDefaultBalance
      )
      .add()
      .build();
  }
}

package dev.zonary123.Models;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Currency {
  public static final BuilderCodec<Currency> CODEC;
  private transient String id;
  private boolean primary = false;
  private short sort = 0;
  private String name;
  private String format;
  private String icon = "";
  private String colorFont = "#DEAC43";
  private String colorNumbers = "#EBD065";
  private double defaultBalance;


  public Currency(boolean primary) {
    this();
    this.primary = primary;
    this.icon = "";
    this.colorFont = "#EBC78F";
    this.colorNumbers = "#8FC9EB";
  }

  public Currency(String id, String name, String format, double defaultBalance, boolean primary) {
    this.id = id;
    this.name = name;
    this.format = format;
    this.defaultBalance = defaultBalance;
    this.primary = primary;
    this.icon = "";
    this.colorFont = "#EBC78F";
    this.colorNumbers = "#8FC9EB";
  }

  private static final NumberFormat COMPACT =
    NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);

  static {
    COMPACT.setMaximumFractionDigits(3);
  }


/*  private static final Cache<Long, String> CACHE = Caffeine.newBuilder()
    .maximumSize(10_000)
    .expireAfterAccess(5, TimeUnit.SECONDS)
    .build();*/

  public String getFormat(BigDecimal value) {
    if (value == null) return apply("0");

    long key = value.longValue();

    return apply(
      //CACHE.get(key, COMPACT::format)
      apply(COMPACT.format(key))
    );
  }

  private String apply(String v) {
    String f = format;
    if (f == null) return v;
    int i = f.indexOf("%s");
    return i < 0 ? v : f.substring(0, i) + v + f.substring(i + 3);
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
      .append(
        new KeyedCodec<>("Icon", Codec.STRING),
        Currency::setIcon, c -> {
          String icon = c.getIcon();
          if (icon == null || icon.isEmpty()) return "";
          return icon;
        }
      )
      .add()
      .append(
        new KeyedCodec<>("ColorFont", Codec.STRING),
        Currency::setColorFont, c -> {
          String colorFont = c.getColorFont();
          if (colorFont == null || colorFont.isEmpty()) {
            return "#DEAC43";
          }
          return colorFont;
        }
      )
      .add()
      .append(
        new KeyedCodec<>("ColorNumbers", Codec.STRING),
        Currency::setColorNumbers, c -> {
          String colorNumbers = c.getColorNumbers();
          if (colorNumbers == null || colorNumbers.isEmpty()) {
            return "#EBD065";
          }
          return colorNumbers;
        }
      )
      .add()
      .build();
  }
}

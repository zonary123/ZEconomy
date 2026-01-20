package dev.zonary123.zeconomy.Models;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.config.StringSchema;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.zonary123.zeconomy.ZEconomy;
import dev.zonary123.zeconomy.api.ZEconomyApi;
import dev.zonary123.zeconomy.ui.BalUI;
import lombok.Data;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
public class Account {

  public static final BuilderCodec<Account> CODEC;

  private UUID uuid;
  private String username;
  private final Map<String, BigDecimal> balances = new ConcurrentHashMap<>();
  private transient BalUI balUI;
  private transient AtomicBoolean dirty = new AtomicBoolean();

  public Account() {

  }

  public Account(PlayerRef playerRef) {
    this.uuid = playerRef.getUuid();
    this.username = playerRef.getUsername();
    this.dirty = new AtomicBoolean(true);
  }

  public Account(UUID uuid, String username) {
    this.uuid = uuid;
    this.username = username;
  }

  public synchronized BigDecimal getBalance(String currency) {
    return balances.getOrDefault(currency, BigDecimal.valueOf(ZEconomyApi.getCurrency(currency).getDefaultBalance()));
  }

  public synchronized BigDecimal getBalance(Currency currency) {
    return balances.getOrDefault(currency.getId(), BigDecimal.valueOf(currency.getDefaultBalance()));
  }

  public synchronized boolean deposit(String currency, BigDecimal amount) {
    balances.merge(currency, amount, BigDecimal::add);
    dirty.set(true);
    return true;
  }

  public synchronized boolean withdraw(String currency, BigDecimal amount) {
    BigDecimal current = balances.getOrDefault(currency, BigDecimal.ZERO);
    if (current.compareTo(amount) < 0) return false;
    balances.put(currency, current.subtract(amount));
    dirty.set(true);
    return true;
  }

  public void fix() {
    var currencies = ZEconomyApi.getCurrencies();
    boolean changed = false;

    for (Map.Entry<String, Currency> entry : currencies.entrySet()) {
      String currencyId = entry.getKey();
      Currency currency = entry.getValue();

      BigDecimal current = balances.get(currencyId);
      if (current == null) {
        balances.put(currencyId, BigDecimal.valueOf(currency.getDefaultBalance()));
        changed = true;
      }
    }


    for (String key : balances.keySet().toArray(new String[0])) {
      if (!currencies.containsKey(key)) {
        balances.remove(key);
        changed = true;
      }
    }

    if (changed) dirty.set(true);
  }


  public boolean save() {
    if (!dirty.getAndSet(false)) return false;
    ZEconomy.getDatabase().saveOrUpdateAccount(this);
    return true;
  }


  public void setBalance(String currencyId, BigDecimal amount) {
    balances.put(currencyId, amount);
    dirty.set(true);
  }

  static {
    CODEC = BuilderCodec.builder(Account.class, Account::new)
      .append(new KeyedCodec<>("Uuid", Codec.UUID_STRING),
        Account::setUuid, Account::getUuid)
      .add()
      .append(new KeyedCodec<>("Username", Codec.STRING),
        Account::setUsername, Account::getUsername)
      .add()
      .append(
        new KeyedCodec<>("Balances", new Codec<>() {
          @NonNullDecl
          @Override
          public Schema toSchema(@NonNullDecl SchemaContext schemaContext) {
            return new StringSchema();
          }

          @Override
          public Map<String, BigDecimal> decode(org.bson.BsonValue bsonValue, ExtraInfo extraInfo) {
            Map<String, BigDecimal> map = new ConcurrentHashMap<>();
            bsonValue.asDocument().forEach((key, value) ->
              map.put(key, new BigDecimal(value.asString().getValue()))
            );
            return map;
          }

          @Override
          public org.bson.BsonValue encode(Map<String, BigDecimal> map, ExtraInfo extraInfo) {
            org.bson.BsonDocument doc = new org.bson.BsonDocument();
            map.forEach((key, value) ->
              doc.put(key, new org.bson.BsonString(value.toPlainString()))
            );
            return doc;
          }
        }),
        (account, map) -> account.getBalances().putAll(map),
        Account::getBalances
      )
      .add()
      .build();
  }
}

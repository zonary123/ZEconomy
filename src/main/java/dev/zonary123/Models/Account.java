package dev.zonary123.Models;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.config.StringSchema;
import com.hypixel.hytale.server.core.entity.entities.Player;
import lombok.Data;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Account {

  public static final BuilderCodec<Account> CODEC = BuilderCodec.builder(Account.class, Account::new)
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

  private UUID uuid;
  private String username;
  private final Map<String, BigDecimal> balances = new ConcurrentHashMap<>();
  private transient boolean dirty = false;

  public Account() {
  }

  public Account(Player player) {
    this.uuid = player.getUuid();
    this.username = player.getDisplayName();
    this.dirty = true;
  }

  @Nullable
  public synchronized BigDecimal getBalance(String currency) {
    return balances.getOrDefault(currency, null);
  }

  public synchronized void deposit(String currency, BigDecimal amount) {
    balances.merge(currency, amount, BigDecimal::add);
  }

  public synchronized boolean withdraw(String currency, BigDecimal amount) {
    BigDecimal current = balances.getOrDefault(currency, BigDecimal.ZERO);
    if (current.compareTo(amount) < 0) return false;
    balances.put(currency, current.subtract(amount));
    return true;
  }

  public void fix() {
    balances.put("USD", BigDecimal.valueOf(1000));
  }

  public void save() {

  }
}

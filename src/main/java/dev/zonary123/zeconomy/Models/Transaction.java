package dev.zonary123.zeconomy.Models;

import dev.zonary123.libs.bson.Document;
import dev.zonary123.zeconomy.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 * @author Carlos Varas Alonso - 16/01/2026 11:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

  @Builder.Default
  private boolean processed = true;

  @Builder.Default
  private UUID id = UUID.randomUUID();

  private UUID accountId;
  private String currencyId;
  private TransactionTypes type;

  @Builder.Default
  private BigDecimal amount = BigDecimal.ZERO;

  @Builder.Default
  private String reason = "No reason provided";

  @Builder.Default
  private Long timestamp = System.currentTimeMillis();


  // MONGODB Document conversion
  public Document toDocument() {
    return Document.parse(Utils.GSON.toJson(this));
  }

  public static Transaction fromDocument(Document doc) {
    return Utils.GSON.fromJson(doc.toJson(), Transaction.class);
  }
}


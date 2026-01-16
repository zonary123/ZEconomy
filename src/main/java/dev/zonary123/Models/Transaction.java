package dev.zonary123.Models;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
  private boolean processed;
  private UUID id;
  private UUID accountId;
  private String currencyId;
  private TransactionTypes type;
  private BigDecimal amount;
  private String reason;

}

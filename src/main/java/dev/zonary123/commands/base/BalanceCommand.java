package dev.zonary123.commands.base;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.zonary123.Models.Account;
import dev.zonary123.Models.Currency;
import dev.zonary123.ZEconomy;
import dev.zonary123.api.ZEconomyApi;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 * @author Carlos Varas Alonso - 14/01/2026 9:55
 */

public class BalanceCommand extends CommandBase {
  @Nonnull
  private OptionalArg<String> currencyArg;
  @Nonnull
  private OptionalArg<PlayerRef> playerArg;

  public BalanceCommand(@NonNullDecl String name, @NonNullDecl String description) {
    super(name, description, true);
    this.currencyArg = this.withOptionalArg(
      "currency",
      "The currency to check the balance for.",
      ArgTypes.STRING
    );
    this.playerArg = this.withOptionalArg(
      "player",
      "The player to check the balance for.",
      ArgTypes.PLAYER_REF
    );
  }


  @Override
  protected void executeSync(@NonNullDecl CommandContext context) {
    String currency = this.currencyArg.get(context);
    PlayerRef playerRef = this.playerArg.get(context);
    if (currency == null) currency = "USD";
    showBalance(context, playerRef, currency);
  }

  private void showBalance(CommandContext context, PlayerRef playerRef, String currency) {
    UUID playerUuid = playerRef == null
      ? context.sender().getUuid()
      : playerRef.getUuid();
    showBalance(context, playerUuid, currency);
  }

  private void showBalance(CommandContext context, UUID playerUuid, String currency) {
    var database = ZEconomy.getDatabase();
    Account account = database.getAccount(playerUuid);
    if (account == null) {
      context.sendMessage(
        Message.raw(
          "The player with UUID " + playerUuid + " does not have an account yet."
        )
      );
      return;
    }
    Currency curr = ZEconomyApi.getCurrency(currency);
    BigDecimal bal = ZEconomyApi.getBalance(playerUuid, curr.getId());
    if (bal == null) {
      context.sendMessage(
        Message.raw(
          "The player with UUID " + playerUuid + " does not have a balance for " + curr.getName() + "."
        )
      );
      return;
    }

    context.sendMessage(
      Message.raw(
        "The balance for player with UUID " + playerUuid + " in " + curr.getName() + " is: " + bal.toPlainString()
      )
    );
  }

}

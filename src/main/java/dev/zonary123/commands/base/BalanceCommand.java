package dev.zonary123.commands.base;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.zonary123.Models.Account;
import dev.zonary123.ZEconomy;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
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


  @Override protected void executeSync(@NonNullDecl CommandContext context) {
    String currency = this.currencyArg.get(context);

    PlayerRef playerRef = this.playerArg.get(context);
    if (currency == null) currency = "USD";
    if (playerRef == null) {
      showBalance(context, currency);
    } else {
      showBalance(context, playerRef, currency);
    }
  }

  private void showBalance(CommandContext context, String currency) {
    UUID playerUuid = context.sender().getUuid();
    var plugin = ZEconomy.getInstance();
    var database = plugin.getDatabase();
    Account account = database.getCacheAccount(playerUuid);
    if (account == null) {
      context.sendMessage(
        Message.raw(
          "You do not have an account yet."
        )
      );
      return;
    }
    context.sendMessage(
      Message.raw(
        "Your balance for " + currency + " is: " + account.getBalance(currency).toPlainString()
      )
    );
  }

  private void showBalance(CommandContext context, PlayerRef playerRef, String currency) {
    var plugin = ZEconomy.getInstance();
    var database = plugin.getDatabase();
    Account account = database.getCacheAccount(playerRef.getUuid());
    if (account == null) {
      context.sendMessage(
        Message.raw(
          "The player " + playerRef.getUsername() + " does not have an account yet."
        )
      );
      return;
    }
    context.sendMessage(
      Message.raw(
        "The balance for " + playerRef.getUsername() + " in " + currency + " is: " + account.getBalance(currency).toPlainString()
      )
    );
  }


}

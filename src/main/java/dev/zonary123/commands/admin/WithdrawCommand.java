package dev.zonary123.commands.admin;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.zonary123.Models.Account;
import dev.zonary123.ZEconomy;
import dev.zonary123.database.DatabaseClient;

import javax.annotation.Nonnull;
import java.math.BigDecimal;

public class WithdrawCommand extends CommandBase {
  @Nonnull
  private RequiredArg<String> currencyArg;
  @Nonnull
  private RequiredArg<PlayerRef> playerArg;
  @Nonnull
  private RequiredArg<String> amountArg;

  public WithdrawCommand(@Nonnull String name, @Nonnull String description) {
    super(name, description, true);
    this.requirePermission("zeconomy.command.withdraw");
    this.currencyArg = this.withRequiredArg(
      "currency",
      "The currency to withdraw.",
      ArgTypes.STRING
    );
    this.playerArg = this.withRequiredArg(
      "player",
      "The player to withdraw to.",
      ArgTypes.PLAYER_REF
    );
    this.amountArg = this.withRequiredArg(
      "amount",
      "The amount to withdraw.",
      ArgTypes.STRING
    );
  }

  @Override
  protected void executeSync(@Nonnull CommandContext context) {
    String currency = this.currencyArg.get(context);
    PlayerRef playerRef = this.playerArg.get(context);
    String amountStr = this.amountArg.get(context);
    DatabaseClient database = ZEconomy.getDatabase();
    Account account = database.getAccount(playerRef.getUuid());
    BigDecimal amount = new BigDecimal(amountStr);
    boolean result = amount.compareTo(BigDecimal.ZERO) > 0;
    if (!result) {
      context.sendMessage(
        Message.raw(
          "Amount must be greater than zero."
        )
      );
    }
    if (account == null) {
      // Player disconnected or does not exist
      result = database.withdraw(playerRef.getUuid(), currency, amount);
    } else {
      result = account.withdraw(currency, amount);
    }
    // Respond to sender
    if (result) {
      // Success
      context.sendMessage(
        Message.raw(
          "Successfully deposited " + amount.toPlainString() + " " + currency + " to " +
            playerRef.getUsername() + "."
        )
      );
    } else {
      // Failure
      context.sendMessage(
        Message.raw(
          "Failed to deposit " + amount.toPlainString() + " " + currency + " to " +
            playerRef.getUsername() + "."
        )
      );
    }
  }
}

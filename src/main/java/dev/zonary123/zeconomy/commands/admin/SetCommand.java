package dev.zonary123.zeconomy.commands.admin;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import dev.zonary123.zeconomy.Config.CCurrency;
import dev.zonary123.zeconomy.Models.Account;
import dev.zonary123.zeconomy.api.ZEconomyApi;

import javax.annotation.Nonnull;
import java.math.BigDecimal;

public class SetCommand extends CommandBase {
  @Nonnull
  private RequiredArg<String> currencyArg;
  @Nonnull
  private RequiredArg<String> playerArg;
  @Nonnull
  private RequiredArg<String> amountArg;
  @Nonnull
  private OptionalArg<String> reasonArg;

  public SetCommand(@Nonnull String name, @Nonnull String description) {
    super(name, description, true);
    this.requirePermission("zeconomy.command.set");
    this.currencyArg = this.withRequiredArg(
      "currency",
      "The currency to set. Examples: " + CCurrency.getCurrencyList(),
      ArgTypes.STRING
    );
    this.playerArg = this.withRequiredArg(
      "player",
      "The player to set to.",
      ArgTypes.STRING
    );
    this.amountArg = this.withRequiredArg(
      "amount",
      "The amount to set.",
      ArgTypes.STRING
    );
    this.reasonArg = this.withOptionalArg(
      "reason",
      "The reason for the set.",
      ArgTypes.STRING
    );
  }

  @Override
  protected void executeSync(@Nonnull CommandContext context) {
    String currency = this.currencyArg.get(context);
    String playerName = this.playerArg.get(context);
    String amountStr = this.amountArg.get(context);
    String reason = context.provided(reasonArg) ? this.reasonArg.get(context) : "Command set executed by admin.";

    BigDecimal amount = new BigDecimal(amountStr);
    boolean result = amount.compareTo(BigDecimal.ZERO) > 0;
    if (!result) {
      context.sendMessage(
        Message.raw(
          "Amount must be greater than zero."
        )
      );
    }
    Account account = ZEconomyApi.findAccountByUsername(playerName);
    if (account == null) {
      context.sendMessage(
        Message.raw(
          "Player not found."
        )
      );
      return;
    }
    context.sendMessage(
      Message.raw(
        "Setting balance of " + playerName + " to " + CCurrency.getCurrencyFormat(currency, amount) + "."
      )
    );
    ZEconomyApi.setBalance(
      account.getUuid(),
      currency,
      amount,
      reason
    );
  }
}

package dev.zonary123.commands.admin;

import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import dev.zonary123.Models.Currency;
import dev.zonary123.api.ZEconomyApi;

import javax.annotation.Nonnull;
import java.math.BigDecimal;

public class DepositCommand extends CommandBase {
  @Nonnull
  private RequiredArg<String> currencyArg;
  @Nonnull
  private RequiredArg<PlayerRef> playerArg;
  @Nonnull
  private RequiredArg<String> amountArg;

  public DepositCommand(@Nonnull String name, @Nonnull String description) {
    super(name, description, true);
    this.requirePermission("zeconomy.command.deposit");
    this.currencyArg = this.withRequiredArg(
      "currency",
      "The currency to deposit.",
      ArgTypes.STRING
    );
    this.playerArg = this.withRequiredArg(
      "player",
      "The player to deposit to.",
      ArgTypes.PLAYER_REF
    );
    this.amountArg = this.withRequiredArg(
      "amount",
      "The amount to deposit.",
      ArgTypes.STRING
    );
  }

  @Override
  protected void executeSync(@Nonnull CommandContext context) {
    String currency = this.currencyArg.get(context);
    PlayerRef playerRef = this.playerArg.get(context);
    String amountStr = this.amountArg.get(context);
    BigDecimal amount = new BigDecimal(amountStr);
    boolean result = amount.compareTo(BigDecimal.ZERO) > 0;
    if (!result) {
      context.sendMessage(
        Message.raw(
          "Amount must be greater than zero."
        )
      );
    }
    Currency curr = ZEconomyApi.getCurrency(currency);
    result = ZEconomyApi.deposit(playerRef.getUuid(), curr.getId(), amount);

    if (result) {
      // Success
      var packetHandler = playerRef.getPacketHandler();
      context.sendMessage(
        Message.raw(
          "Successfully deposited " + amount.toPlainString() + " " + curr.getName() + " to " +
            playerRef.getUsername() + "."
        )
      );
      NotificationUtil.sendNotification(
        packetHandler,
        Message.raw("Deposit").color("#FFD700"),
        Message.raw(
          "You have received " + amount.toPlainString() + " " + curr.getName() + "."
        ),
        NotificationStyle.Success
      );
    } else {
      // Failure
      context.sendMessage(
        Message.raw(
          "Failed to deposit " + amount.toPlainString() + " " + curr.getName() + " to " +
            playerRef.getUsername() + "."
        )
      );
      playerRef = Universe.get().getPlayer(context.sender().getUuid());
      if (playerRef != null) {
        var packetHandler = playerRef.getPacketHandler();
        NotificationUtil.sendNotification(
          packetHandler,
          Message.raw("Deposit Failed").color("#FF0000"),
          Message.raw(
            "A deposit of " + amount.toPlainString() + " " + curr.getName() + " to your account has failed."
          ),
          NotificationStyle.Danger
        );
      }
    }
  }
}

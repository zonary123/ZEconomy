package dev.zonary123.zeconomy.commands.base;

import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import dev.zonary123.zeconomy.Config.CCurrency;
import dev.zonary123.zeconomy.Models.Account;
import dev.zonary123.zeconomy.Models.Currency;
import dev.zonary123.zeconomy.api.ZEconomyApi;
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
  private final OptionalArg<String> currencyArg;
  @Nonnull
  private final OptionalArg<String> playerArg;

  public BalanceCommand(@NonNullDecl String name, @NonNullDecl String description) {
    super(name, description);
    this.currencyArg = this.withOptionalArg(
      "currency",
      "The currency to check the balance for. Examples: " + CCurrency.getCurrencyList(),
      ArgTypes.STRING
    );
    this.playerArg = this.withOptionalArg(
      "player",
      "The player to check the balance for.",
      ArgTypes.STRING
    );
  }


  @Override
  protected void executeSync(@NonNullDecl CommandContext context) {
    String currency = this.currencyArg.get(context);
    String playerName = this.playerArg.get(context);
    showBalance(context, playerName, currency);
  }

  private void showBalance(CommandContext context, String playerName, String currency) {
    PlayerRef playerRef;
    UUID playerUuid;
    if (playerName != null) {
      Account account = ZEconomyApi.findAccountByUsername(playerName);
      if (account == null) {
        context.sendMessage(
          Message.raw("Could not find an account for player " + playerName + ".")
        );
        return;
      }
      playerUuid = account.getUuid();
      playerRef = Universe.get().getPlayerByUsername(playerName, NameMatching.EXACT);
    } else {
      if (!context.isPlayer()) {
        context.sendMessage(
          Message.raw(
            "You must specify a player when using this command from the console."
          )
        );
        return;
      }
      playerRef = context.senderAs(Player.class).getPlayerRef();
      playerUuid = playerRef.getUuid();
    }
    Currency curr = ZEconomyApi.getCurrency(currency);
    BigDecimal bal = ZEconomyApi.getBalance(playerUuid, curr.getId());
    if (bal == null) {
      context.sendMessage(
        Message.raw(
          "Could not retrieve the balance for " + curr.getName() + "."
        )
      );
      return;
    }
    if (playerRef != null) {
      SoundUtil.playSoundEvent2dToPlayer(
        playerRef,
        SoundEvent.getAssetMap().getIndex("SFX_Coins_Walk"),
        SoundCategory.SFX
      );
    }
    context.sendMessage(
      Message.raw(
        "The balance for " + curr.getName() + " is " + bal.toPlainString() + "."
      )
    );
  }

}

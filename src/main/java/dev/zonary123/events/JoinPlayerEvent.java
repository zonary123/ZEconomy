package dev.zonary123.events;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import dev.zonary123.Models.Account;
import dev.zonary123.ZEconomy;
import dev.zonary123.database.DatabaseClient;

public class JoinPlayerEvent {

  public static void onPlayerReady(PlayerReadyEvent event) {
    Player player = event.getPlayer();
    DatabaseClient database = ZEconomy.getDatabase();
    Account account = database.findAccount(player.getUuid());
    if (account == null) account = new Account(player);
    account.fix();
    account.save();
    DatabaseClient.ACCOUNTS.put(account.getUuid(), account);
  }

}


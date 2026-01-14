package dev.zonary123.events;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import dev.zonary123.Models.Account;
import dev.zonary123.database.DatabaseClient;

public class ExampleEvent {

  public static void onPlayerReady(PlayerReadyEvent event) {
    Player player = event.getPlayer();
    Account account = new Account(player);
    account.fix();
    account.save();
    DatabaseClient.ACCOUNTS.put(player.getUuid(), account);
  }

}

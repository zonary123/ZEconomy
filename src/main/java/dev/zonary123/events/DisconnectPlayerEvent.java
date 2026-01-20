package dev.zonary123.events;

import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import dev.zonary123.Models.Account;
import dev.zonary123.ZEconomy;
import dev.zonary123.database.DatabaseClient;

import java.util.UUID;

public class DisconnectPlayerEvent {

  public static void onPlayerDisconnect(PlayerDisconnectEvent event) {
    UUID uuid = event.getPlayerRef().getUuid();
    DatabaseClient database = ZEconomy.getDatabase();
    Account account = database.getAccount(uuid);
    if (account != null) account.save();
  }

}


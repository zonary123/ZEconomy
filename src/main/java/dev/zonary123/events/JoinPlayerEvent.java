package dev.zonary123.events;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.zonary123.Models.Account;
import dev.zonary123.ZEconomy;
import dev.zonary123.database.DatabaseClient;

import java.util.UUID;

public class JoinPlayerEvent {

  public static void onPlayerReady(PlayerReadyEvent evt) {
    var ref = evt.getPlayerRef();
    var store = ref.getStore();
    PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
    if (playerRef == null) {
      ZEconomy.get().getLogger().atInfo().log(
        "PlayerRef is null for player %s on PlayerReadyEvent", evt.getPlayer().getDisplayName()
      );
      return;
    }
    UUID uuid = playerRef.getUuid();
    DatabaseClient database = ZEconomy.getDatabase();
    Account account = database.findAccount(uuid);
    if (account == null) account = new Account(playerRef);
    account.fix();
    account.save();
    DatabaseClient.ACCOUNTS.put(uuid, account);
  }

}


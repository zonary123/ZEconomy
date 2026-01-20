package dev.zonary123.zeconomy.systems;

import com.buuz135.mhud.MultipleHUD;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.zonary123.zeconomy.Models.Account;
import dev.zonary123.zeconomy.api.ZEconomyApi;
import dev.zonary123.zeconomy.ui.BalUI;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Carlos Varas Alonso - 15/01/2026 21:45
 */
public class BalanceHudTickingSystem extends EntityTickingSystem<EntityStore> {
  private transient final Map<UUID, Short> playerTickCounter = new ConcurrentHashMap<>();

  @Override
  public void tick(float v, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
                   @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
    Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
    Player player = store.getComponent(ref, Player.getComponentType());
    PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
    if (playerRef == null) return;
    UUID playerUuid = playerRef.getUuid();
    short counter = playerTickCounter.getOrDefault(playerUuid, (short) 0);
    counter++;
    if (counter >= 20) {
      Account account = ZEconomyApi.getAccount(playerUuid);
      playerTickCounter.put(playerUuid, (short) 0);
      if (account == null) return;
      MultipleHUD.getInstance().setCustomHud(
        player,
        playerRef,
        "balance_hud",
        new BalUI(
          playerRef,
          account,
          "BalanceHud.ui"
        )
      );
      return;
    }
    playerTickCounter.put(playerUuid, counter);
  }

  @Nonnull
  @Override
  public Query<EntityStore> getQuery() {
    return Query.any();
  }
}

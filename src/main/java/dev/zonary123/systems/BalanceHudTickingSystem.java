package dev.zonary123.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.zonary123.Models.Account;
import dev.zonary123.api.ZEconomyApi;
import dev.zonary123.ui.BalUI;
import org.jspecify.annotations.NonNull;

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
  public void tick(float v, int index, @NonNull ArchetypeChunk<EntityStore> archetypeChunk, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {
    Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
    PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
    if (playerRef == null) return;
    UUID playerUuid = playerRef.getUuid();
    short counter = playerTickCounter.getOrDefault(playerUuid, (short) 0);
    counter++;
    if (counter >= 20) {
      Account account = ZEconomyApi.getAccount(playerUuid);
      if (account == null) {
        playerTickCounter.put(playerUuid, (short) 0);
        return;
      }
      if (account.getBalUI() == null) {
        BalUI balUI = new BalUI(playerRef, account);
        account.setBalUI(balUI);
        balUI.show();
      }
      account.getBalUI().updateStatsOnly();
      counter = 0;
    }
    playerTickCounter.put(playerUuid, counter);
  }

  @Nonnull
  @Override
  public Query<EntityStore> getQuery() {
    return Query.any();
  }
}

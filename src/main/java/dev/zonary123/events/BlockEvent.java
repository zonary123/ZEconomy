package dev.zonary123.events;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.event.events.entity.EntityEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.awt.*;

/**
 *
 * @author Carlos Varas Alonso - 13/01/2026 22:21
 */
public class BlockEvent extends EntityEventSystem<EntityStore, BreakBlockEvent> {


  public BlockEvent() {
    super(BreakBlockEvent.class);
  }

  @Override public void handle(int i,
                               @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                               @NonNullDecl Store<EntityStore> store,
                               @NonNullDecl CommandBuffer<EntityStore> command,
                               @NonNullDecl BreakBlockEvent evt) {
    Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
    Player player = store.getComponent(ref, Player.getComponentType());
    var blockType = evt.getBlockType();
    var block = evt.getBlockType().getId();

    if (block.equalsIgnoreCase("Empty")) return;
    if (player != null){
      player.sendMessage(
        Message.raw(
          "You broke a block." + " (ID: " + evt.getBlockType().getId() + ")"
        )
      );
    }


  }

  @NullableDecl @Override public Query<EntityStore> getQuery() {
    return PlayerRef.getComponentType();
  }
}

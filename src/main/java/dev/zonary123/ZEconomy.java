package dev.zonary123;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.zonary123.commands.Commands;
import dev.zonary123.events.BlockEvent;
import dev.zonary123.events.ExampleEvent;

import javax.annotation.Nonnull;

public class ZEconomy extends JavaPlugin {

    public ZEconomy(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        Commands.register(this);
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, ExampleEvent::onPlayerReady);
        this.getEntityStoreRegistry().registerSystem(new BlockEvent());
    }
}

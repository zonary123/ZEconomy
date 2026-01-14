package dev.zonary123;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import dev.zonary123.Config.ZEConfig;
import dev.zonary123.commands.Commands;
import dev.zonary123.database.DatabaseClient;
import dev.zonary123.database.JsonDatabase;
import dev.zonary123.events.BlockEvent;
import dev.zonary123.events.ExampleEvent;
import lombok.Getter;

import javax.annotation.Nonnull;

@Getter
public class ZEconomy extends JavaPlugin {
  private static ZEconomy INSTANCE;

  private final DatabaseClient database = new JsonDatabase();
  private final Config<ZEConfig> config;

  public ZEconomy(@Nonnull JavaPluginInit init) {
    super(init);
    INSTANCE = this; // ✅ inicializamos la instancia aquí
    this.config = this.withConfig("config", ZEConfig.CODEC);
  }

  @Override
  protected void setup() {
    super.setup();
    this.config.save();
    Commands.register(this);
    this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, ExampleEvent::onPlayerReady);
    this.getEntityStoreRegistry().registerSystem(new BlockEvent());
  }

  public static ZEconomy getInstance() {
    return INSTANCE;
  }

  public static DatabaseClient getDatabase() {
    return getInstance().database;
  }
}


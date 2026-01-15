package dev.zonary123;

import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import dev.zonary123.Config.CCurrency;
import dev.zonary123.Config.ZEConfig;
import dev.zonary123.commands.Commands;
import dev.zonary123.database.DatabaseClient;
import dev.zonary123.database.DatabaseFactory;
import dev.zonary123.database.JsonDatabase;
import dev.zonary123.events.DisconnectPlayerEvent;
import dev.zonary123.events.JoinPlayerEvent;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;

@Getter
@Setter
public class ZEconomy extends JavaPlugin {
  private static ZEconomy INSTANCE;

  private Commands commands = new Commands();
  private DatabaseClient database = new JsonDatabase();
  private final Config<ZEConfig> config;

  public ZEconomy(@Nonnull JavaPluginInit init) {
    super(init);
    INSTANCE = this;
    this.config = this.withConfig("config", ZEConfig.CODEC);
  }

  @Override
  protected void setup() {
    super.setup();
    files();
    commands.register(this);
    events();
    this.database = DatabaseFactory.createDatabaseClient();
  }


  private void files() {
    this.config.load();
    this.config.save();
    CCurrency.init();
  }

  private void events() {
    this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, JoinPlayerEvent::onPlayerReady);
    this.getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, DisconnectPlayerEvent::onPlayerDisconnect);
  }

  @Override
  protected void shutdown() {
    super.shutdown();
  }

  public static ZEconomy getInstance() {
    return INSTANCE;
  }

  public static DatabaseClient getDatabase() {
    return getInstance().database;
  }
}


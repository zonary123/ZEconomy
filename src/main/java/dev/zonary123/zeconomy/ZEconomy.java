package dev.zonary123.zeconomy;


import com.dunystudios.hytale.plugins.IEcoAPI;
import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.zonary123.zeconomy.Config.CCurrency;
import dev.zonary123.zeconomy.Config.Config;
import dev.zonary123.zeconomy.Config.Lang;
import dev.zonary123.zeconomy.commands.Commands;
import dev.zonary123.zeconomy.database.DatabaseClient;
import dev.zonary123.zeconomy.database.DatabaseFactory;
import dev.zonary123.zeconomy.events.DisconnectPlayerEvent;
import dev.zonary123.zeconomy.events.JoinPlayerEvent;
import dev.zonary123.zeconomy.systems.BalanceHudTickingSystem;
import dev.zonary123.zeconomy.tasks.Tasks;
import dev.zonary123.zeconomy.utils.IEcoOverwrite;
import dev.zonary123.zutils.utils.async.AsyncContext;
import dev.zonary123.zutils.utils.async.UtilsAsync;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.nio.file.Path;

@Getter
@Setter
public class ZEconomy extends JavaPlugin {
  private static ZEconomy INSTANCE;

  private Commands commands = new Commands();
  private DatabaseClient database;
  private Config config = new Config();
  private Lang lang = new Lang();
  public static final AsyncContext ASYNC_CONTEXT = UtilsAsync.createContext("ZEconomy", "-Worker");

  public ZEconomy(@Nonnull JavaPluginInit init) {
    super(init);
    INSTANCE = this;

  }

  @Override
  protected void setup() {
    super.setup();
    files();
    commands.register(this);
    events();
    this.database = DatabaseFactory.createDatabaseClient();

    var plugin = HytaleServer.get().getPluginManager().getPlugin(new PluginIdentifier("com.dunystudios.hytale.plugins", "EcoAPI"));
    if (plugin != null && plugin.isEnabled()) {
      IEcoAPI.Service.setInstance(new IEcoOverwrite());
      getLogger().atInfo().log(
        "EcoAPI plugin detected, overwriting with ZEconomy implementation."
      );
    }
    Tasks.register();
  }


  private void files() {
    this.config = config.init();
    this.lang = lang.init();
    CCurrency.init();
  }

  private void events() {
    this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, JoinPlayerEvent::onPlayerReady);
    this.getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, DisconnectPlayerEvent::onPlayerDisconnect);
    this.getEntityStoreRegistry().registerSystem(new BalanceHudTickingSystem());
  }


  public static ZEconomy get() {
    return INSTANCE;
  }

  public static Config getConfig() {
    return get().config;
  }

  public static Lang getLang() {
    return get().lang;
  }

  public static Path getPath() {
    return get().getDataDirectory();
  }

  public static DatabaseClient getDatabase() {
    return get().database;
  }

  public void reload() {
    files();
    this.database = DatabaseFactory.createDatabaseClient();
  }
}


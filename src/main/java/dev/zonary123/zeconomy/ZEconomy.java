package dev.zonary123.zeconomy;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import dev.zonary123.zeconomy.Config.CCurrency;
import dev.zonary123.zeconomy.Config.ZEConfig;
import dev.zonary123.zeconomy.commands.Commands;
import dev.zonary123.zeconomy.database.DatabaseClient;
import dev.zonary123.zeconomy.database.DatabaseFactory;
import dev.zonary123.zeconomy.events.DisconnectPlayerEvent;
import dev.zonary123.zeconomy.events.JoinPlayerEvent;
import dev.zonary123.zeconomy.systems.BalanceHudTickingSystem;
import dev.zonary123.zeconomy.tasks.Tasks;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter
@Setter
public class ZEconomy extends JavaPlugin {
  private static ZEconomy INSTANCE;
  private Commands commands = new Commands();
  private DatabaseClient database;
  private final Config<ZEConfig> config;


  public static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE =
    Executors.newSingleThreadScheduledExecutor(
      r -> new Thread(r, "ZEconomy-Scheduler")
    );

  public ZEconomy(@Nonnull JavaPluginInit init) {
    super(init);
    INSTANCE = this;
    this.config = this.withConfig("config", ZEConfig.CODEC);
  }

  @Override
  protected void setup() {
    super.setup();
    getLogger().atInfo().log(
      "Starting ZEconomy v%s", getPath().toAbsolutePath().toString()
    );
    reload();
    commands.register(this);
    events();
    Tasks.register();

  }


  public void reload() {
    files();
    DatabaseFactory.createDatabaseClient();
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
    this.getEntityStoreRegistry().registerSystem(new BalanceHudTickingSystem());
  }

  @Override protected void shutdown() {
    super.shutdown();
    DatabaseClient.saveAll();
  }

  public static ZEconomy get() {
    return INSTANCE;
  }

  public static Path getPath() {
    return get().getDataDirectory();
  }

  public static HytaleLogger getLog() {
    return get().getLogger();
  }

  public static DatabaseClient getDatabase() {
    return get().database;
  }

}


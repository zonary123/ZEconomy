package dev.zonary123.commands;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import dev.zonary123.commands.base.BalanceCommand;

public class Commands {
  public static void register(JavaPlugin plugin) {
    plugin.getCommandRegistry().registerCommand(new BalanceCommand("bal", "Check your balance"));
  }
}

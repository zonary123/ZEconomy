package dev.zonary123.commands;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import dev.zonary123.commands.base.homes.HomeCommand;

public class Commands  {
    public static void register(JavaPlugin plugin) {
        plugin.getCommandRegistry().registerCommand(new HomeCommand("home", "Teleport to your home"));
    }
}

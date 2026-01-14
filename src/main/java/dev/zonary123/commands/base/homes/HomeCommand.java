package dev.zonary123.commands.base.homes;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

/**
 *
 * @author Carlos Varas Alonso - 14/01/2026 6:28
 */
public class HomeCommand extends CommandBase{
  public HomeCommand(@NonNullDecl String name, @NonNullDecl String description) {
    super(name, description);
    this.requirePermission("command.home");
    this.addSubCommand(new HomeSetCommand("set", "Set your home location"));
  }

  @Override protected void executeSync(@NonNullDecl CommandContext context) {

  }
}

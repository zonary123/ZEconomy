package dev.zonary123.zeconomy.commands.admin;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import dev.zonary123.zeconomy.ZEconomy;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Carlos Varas Alonso - 17/01/2026 11:40
 */
public class ReloadCommand extends CommandBase {
  public ReloadCommand(@NonNull String name, @NonNull String description) {
    super(name, description);
    this.requirePermission("zeconomy.command.reload");
  }

  @Override protected void executeSync(@NonNull CommandContext context) {
    ZEconomy.get().reload();
    context.sendMessage(
      Message.raw(
        "§aZEconomy configuration reloaded successfully."
      )
    );
  }
}

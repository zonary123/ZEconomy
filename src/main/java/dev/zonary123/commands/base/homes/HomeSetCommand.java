package dev.zonary123.commands.base.homes;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.args.OptionArg;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.modules.i18n.I18nModule;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;

/**
 *
 * @author Carlos Varas Alonso - 14/01/2026 6:28
 */
public class HomeSetCommand extends CommandBase{
  @Nonnull
  private final RequiredArg<String> name;
  @Nonnull
  private final OptionalArg<String> password;

  public HomeSetCommand(@NonNullDecl String name, @NonNullDecl String description) {
    super(name, description);
    this.requirePermission("command.home.create");
    this.name = this.withRequiredArg(
      "home",
      "The name of the home to set",
      ArgTypes.STRING
    );
    this.password = this.withOptionalArg(
      "password",
      "The password to protect the home",
      ArgTypes.STRING
    );
  }

  @Override protected void executeSync(@NonNullDecl CommandContext context) {
    String homeName = this.name.get(context);
    String password = this.password.get(context);
    if (!context.isPlayer()){
      context.sendMessage(
        Message.raw(
          "Only players can set homes."
        )
      );
      return;
    }
    if (password != null){
      runWithPassword(context, password);
    } else {
      runWithOutPassword(context);
    }
  }

  private void runWithPassword(CommandContext context, String password) {
    String homeName = this.name.get(context);
    context.sendMessage(
      Message.raw(
        "Home '" + homeName + "' set with password: " + password
      )
    );
  }

  private void  runWithOutPassword(CommandContext context){
    String homeName = this.name.get(context);
    context.sendMessage(
      Message.raw(
        "Home '" + homeName + "' set!"
      )
    );
  }
}

package dev.zonary123.commands.base;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import dev.zonary123.commands.Commands;

import javax.annotation.Nonnull;

public class EcoCommand extends CommandBase {

  public EcoCommand(@Nonnull String name, @Nonnull String description) {
    super(name, description);
    this.requirePermission("zeconomy.command.eco");
    this.addSubCommand(Commands.createBalanceCommand());
    this.addSubCommand(Commands.createDepositCommand());
    this.addSubCommand(Commands.createWithdrawCommand());
  }

  @Override
  protected void executeSync(@Nonnull CommandContext commandContext) {
    throw new UnsupportedOperationException("This command is not meant to be executed directly.");
  }
}

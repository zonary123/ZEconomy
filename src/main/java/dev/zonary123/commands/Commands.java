package dev.zonary123.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import dev.zonary123.commands.admin.DepositCommand;
import dev.zonary123.commands.admin.WithdrawCommand;
import dev.zonary123.commands.base.BalanceCommand;
import dev.zonary123.commands.base.EcoCommand;
import lombok.Data;

@Data
public class Commands {
  public Commands() {

  }

  public void register(JavaPlugin plugin) {
    plugin.getCommandRegistry().registerCommand(createEcoCommand());
    plugin.getCommandRegistry().registerCommand(createBalanceCommand());
  }

  public static CommandBase createEcoCommand() {
    return new EcoCommand("eco", "Base economy command");
  }

  public static CommandBase createBalanceCommand() {
    return new BalanceCommand("bal", "Check your balance");
  }

  public static CommandBase createDepositCommand() {
    return new DepositCommand("deposit", "Deposit money to a player");
  }

  public static CommandBase createWithdrawCommand() {
    return new WithdrawCommand("withdraw", "Withdraw money from a player");
  }
}

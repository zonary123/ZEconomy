package dev.zonary123.zeconomy.commands;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import dev.zonary123.zeconomy.commands.admin.DepositCommand;
import dev.zonary123.zeconomy.commands.admin.ReloadCommand;
import dev.zonary123.zeconomy.commands.admin.SetCommand;
import dev.zonary123.zeconomy.commands.admin.WithdrawCommand;
import dev.zonary123.zeconomy.commands.base.BalanceCommand;
import dev.zonary123.zeconomy.commands.base.BalanceTopCommand;
import dev.zonary123.zeconomy.commands.base.EcoCommand;
import lombok.Data;

@Data
public class Commands {
  public Commands() {

  }


  public void register(JavaPlugin plugin) {
    plugin.getCommandRegistry().registerCommand(new ReloadCommand("zeconomyreload", "Reload Zeconomy configuration"));
    plugin.getCommandRegistry().registerCommand(createBalanceTopCommand());
    plugin.getCommandRegistry().registerCommand(createEcoCommand());
    plugin.getCommandRegistry().registerCommand(createBalanceCommand());
  }

  public static CommandBase createEcoCommand() {
    return new EcoCommand("eco", "Base economy command");
  }

  public static CommandBase createBalanceCommand() {
    return new BalanceCommand("bal", "Check your balance");
  }

  public static CommandBase createBalanceTopCommand() {
    return new BalanceTopCommand("baltop", "Check the top balances");
  }

  public static CommandBase createDepositCommand() {
    return new DepositCommand("deposit", "Deposit money to a player");
  }

  public static CommandBase createWithdrawCommand() {
    return new WithdrawCommand("withdraw", "Withdraw money from a player");
  }

  public static AbstractCommand createSetCommand() {
    return new SetCommand("set", "Set a player's balance");
  }
}

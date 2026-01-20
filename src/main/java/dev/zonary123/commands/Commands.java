package dev.zonary123.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import dev.zonary123.commands.admin.DepositCommand;
import dev.zonary123.commands.admin.WithdrawCommand;
import dev.zonary123.commands.base.BalanceCommand;
import dev.zonary123.commands.base.EcoCommand;

public class Commands {
  public static CommandBase ECO_COMMAND;
  public static CommandBase BALANCE_COMMAND;
  public static CommandBase DEPOSIT_COMMAND;
  public static CommandBase WITHDRAW_COMMAND;


  public static void register(JavaPlugin plugin) {
    ECO_COMMAND = new EcoCommand("eco", "Base economy command");
    BALANCE_COMMAND = new BalanceCommand("bal", "Check your balance");
    DEPOSIT_COMMAND = new DepositCommand("deposit", "Deposit money to your account");
    WITHDRAW_COMMAND = new WithdrawCommand("withdraw", "Withdraw money from your account");
    plugin.getCommandRegistry().registerCommand(ECO_COMMAND);
    plugin.getCommandRegistry().registerCommand(BALANCE_COMMAND);
  }
}

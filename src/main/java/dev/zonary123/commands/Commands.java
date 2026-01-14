package dev.zonary123.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import dev.zonary123.commands.admin.DepositCommand;
import dev.zonary123.commands.admin.WithdrawCommand;
import dev.zonary123.commands.base.BalanceCommand;
import dev.zonary123.commands.base.EcoCommand;

public class Commands {
  public final static CommandBase ECO_COMMAND = new EcoCommand("eco", "Base economy command");
  public final static CommandBase BALANCE_COMMAND = new BalanceCommand("bal", "Check your balance");
  public final static CommandBase DEPOSIT_COMMAND = new DepositCommand("deposit", "Deposit money to your account");
  public final static CommandBase WITHDRAW_COMMAND = new WithdrawCommand("withdraw", "Withdraw money from your account");


  public static void register(JavaPlugin plugin) {
    plugin.getCommandRegistry().registerCommand(ECO_COMMAND);
    plugin.getCommandRegistry().registerCommand(BALANCE_COMMAND);
  }
}

package dev.zonary123.zeconomy.commands.base;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import dev.zonary123.zeconomy.Config.CCurrency;
import dev.zonary123.zeconomy.Models.Account;
import dev.zonary123.zeconomy.Models.Currency;
import dev.zonary123.zeconomy.ZEconomy;
import dev.zonary123.zeconomy.api.ZEconomyApi;
import dev.zonary123.zutils.utils.FormatMessage;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.math.BigDecimal;

/**
 *
 * @author Carlos Varas Alonso - 14/01/2026 9:55
 */

public class BalanceTopCommand extends CommandBase {
  @Nonnull
  private final OptionalArg<String> currencyArg;
  @Nonnull
  private final OptionalArg<Integer> pageArg;

  public BalanceTopCommand(@NonNullDecl String name, @NonNullDecl String description) {
    super(name, description);
    this.requirePermission("zeconomy.command.eco.balancetop");
    this.currencyArg = this.withOptionalArg(
      "currency",
      "The currency to check the balance for. Examples: " + CCurrency.getCurrencyList(),
      ArgTypes.STRING
    );
    this.pageArg = this.withOptionalArg(
      "page",
      "The page number to display.",
      ArgTypes.INTEGER
    );
  }

  @Override
  protected void executeSync(@Nonnull CommandContext context) {
    int page = context.provided(pageArg) ? this.pageArg.get(context) : 1;
    if (page <= 0) page = 1;
    Currency currency = context.provided(currencyArg)
      ? ZEconomyApi.getCurrency(this.currencyArg.get(context))
      : ZEconomyApi.getDefaultCurrency();
    String currencyId = currency.getId();

    var baltop = ZEconomy.getDatabase().getTopBalance(page, 20, currencyId);
    StringBuilder builder = new StringBuilder();
    // Encabezado con gradiente
    builder.append("<gradient:#FFE5BA:#FFAD29>=== Balance Top ===</gradient>");

    for (Account account : baltop) {
      String username = account.getUsername();
      BigDecimal balance = account.getBalance(currencyId);

      // Cada línea con colores
      builder.append("\n<#AAAAAA>- <#55FF55>")
        .append(username)
        .append("<#FFFF55>: <#FFFFAA>")
        .append(ZEconomyApi.getFormat(currencyId, balance));
    }

    // Pie con color fijo
    builder.append("\n<#FFAA00><b>====================</b>");
    // Enviar mensaje con formateo
    context.sendMessage(FormatMessage.formatMessage(builder.toString()));
  }


}

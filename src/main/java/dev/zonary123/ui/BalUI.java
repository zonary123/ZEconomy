package dev.zonary123.ui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.zonary123.Models.Account;
import dev.zonary123.Models.Currency;
import dev.zonary123.api.ZEconomyApi;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.util.Map;

/**
 *
 * @author Carlos Varas Alonso - 15/01/2026 21:44
 */
@EqualsAndHashCode(callSuper = true) @Data
public class BalUI extends CustomUIHud {
  public static final String HUD_IDENTIFIER = "balance_hud";
  private transient final Account account;
  private boolean built = false;

  public BalUI(PlayerRef playerRef, Account account) {
    super(playerRef);
    this.account = account;
  }

  @Override protected void build(@NonNull UICommandBuilder commands) {
    commands.append("Pages/BalanceHud.ui");
    this.built = true;
  }

  private void ensureBuilt() {
    if (!this.built) {
      UICommandBuilder commands = new UICommandBuilder();
      this.build(commands);
      this.update(false, commands);
    }
  }

  public void refresh() {
    this.ensureBuilt();
    UICommandBuilder commands = new UICommandBuilder();
    this.update(false, commands);
  }

  public void updateStatsOnly() {
    this.ensureBuilt();
    UICommandBuilder commands = new UICommandBuilder();
    commands.clear("#CurrenciesList");
    var currencies = ZEconomyApi.getCurrencies();

    var entries = currencies.entrySet();
    int index = 0;
    for (Map.Entry<String, Currency> entry : entries) {
      Currency currency = entry.getValue();
      BigDecimal balance = account.getBalance(currency.getId());
      commands.append("#CurrenciesList", "Pages/BalanceEntry.ui");
      String selector = "#CurrenciesList[" + index + "]";
      commands.set(selector + " #CurrencyName.Text", currency.getName() + ": ");
      commands.set(selector + " #CurrencyName.Style.TextColor", currency.getColorFont());
      commands.set(selector + " #CurrencyAmount.Text", currency.getFormat(balance));
      commands.set(selector + " #CurrencyAmount.Style.TextColor", currency.getColorNumbers());
      ++index;
    }
    this.update(false, commands);
  }

}

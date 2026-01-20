package dev.zonary123.zeconomy.ui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.zonary123.zeconomy.Models.Account;
import dev.zonary123.zeconomy.Models.Currency;
import dev.zonary123.zeconomy.api.ZEconomyApi;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.Map;

/**
 *
 * @author Carlos Varas Alonso - 15/01/2026 21:44
 */
@EqualsAndHashCode(callSuper = true) @Data
public class BalUI extends CustomUIHud {
  public static final String HUD_IDENTIFIER = "balance_hud";
  private final String file;
  private transient final Account account;
  private boolean built = false;

  public BalUI(PlayerRef playerRef, Account account, String file) {
    super(playerRef);
    this.account = account;
    this.file = file;
  }

  @Override protected void build(@Nonnull UICommandBuilder commands) {
    commands.append("Pages/" + file);
    updateStatsOnly(commands);
    this.built = true;
  }
  
  public void updateStatsOnly(UICommandBuilder commands) {
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

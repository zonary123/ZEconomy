package dev.zonary123.utils;

import com.dunystudios.hytale.plugins.IEcoAPI;
import dev.zonary123.api.ZEconomyApi;

import java.math.BigDecimal;
import java.util.UUID;

public class IEcoOverwrite implements IEcoAPI {
  @Override
  public float getBalance(UUID uuid) {
    return ZEconomyApi.getBalance(uuid, "").floatValue();
  }

  @Override
  public void addBalance(UUID uuid, float v) {
    BigDecimal amount = BigDecimal.valueOf(v);
    ZEconomyApi.deposit(uuid, "", amount);
  }


  @Override
  public void removeBalance(UUID uuid, float v) {
    BigDecimal amount = BigDecimal.valueOf(v);
    ZEconomyApi.withdraw(uuid, "", amount);
  }
}

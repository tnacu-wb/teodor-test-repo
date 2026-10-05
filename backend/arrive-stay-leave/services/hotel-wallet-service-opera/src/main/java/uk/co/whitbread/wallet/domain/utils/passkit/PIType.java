package uk.co.whitbread.wallet.domain.utils.passkit;

import lombok.AllArgsConstructor;
import uk.co.whitbread.wallet.domain.properties.WalletProperties;

/**
 * Type keeping the UI details for any brands except HUB and ZIP brands when the channel is PI.
 */
@AllArgsConstructor
public class PIType implements Type {

  private final WalletProperties walletProperties;

  @Override
  public String getName() {
    return "PI";
  }

  @Override
  public String getBackgroundColor() {
    return walletProperties.getPi().getBackgroundColor();
  }

  @Override
  public String getForegroundColor() {
    return walletProperties.getPi().getForegroundColor();
  }

  @Override
  public String getLabelColor() {
    return walletProperties.getPi().getLabelColor();
  }
}
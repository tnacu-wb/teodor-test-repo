package uk.co.whitbread.wallet.domain.utils.passkit;

import lombok.AllArgsConstructor;
import uk.co.whitbread.wallet.domain.properties.WalletProperties;

/**
 * Type keeping the UI details for HUB brand when the channel is PI.
 */
@AllArgsConstructor
public class HUBType implements Type {

  private final WalletProperties walletProperties;

  @Override
  public String getName() {
    return "HUB";
  }

  @Override
  public String getBackgroundColor() {
    return walletProperties.getHub().getBackgroundColor();
  }

  @Override
  public String getForegroundColor() {
    return walletProperties.getHub().getForegroundColor();
  }

  @Override
  public String getLabelColor() {
    return walletProperties.getHub().getLabelColor();
  }
}

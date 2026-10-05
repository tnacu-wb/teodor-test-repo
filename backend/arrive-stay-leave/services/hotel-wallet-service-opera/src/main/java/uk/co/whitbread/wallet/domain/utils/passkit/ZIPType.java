package uk.co.whitbread.wallet.domain.utils.passkit;

import lombok.AllArgsConstructor;
import uk.co.whitbread.wallet.domain.properties.WalletProperties;

/**
 * Type keeping the UI details for ZIP brand when the channel is PI.
 */
@AllArgsConstructor
public class ZIPType implements Type {

  private final WalletProperties walletProperties;

  @Override
  public String getName() {
    return "ZIP";
  }

  @Override
  public String getBackgroundColor() {
    return walletProperties.getZip().getBackgroundColor();
  }

  @Override
  public String getForegroundColor() {
    return walletProperties.getZip().getForegroundColor();
  }

  @Override
  public String getLabelColor() {
    return walletProperties.getZip().getLabelColor();
  }
}

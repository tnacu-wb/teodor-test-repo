package uk.co.whitbread.wallet.domain.utils.passkit;

import lombok.AllArgsConstructor;
import uk.co.whitbread.wallet.domain.properties.WalletProperties;

/**
 * Type keeping the UI details for any brand when the channel is BB.
 */
@AllArgsConstructor
public class BBType implements Type {

  private final WalletProperties walletProperties;

  @Override
  public String getName() {
    return "BB";
  }

  @Override
  public String getBackgroundColor() {
    return walletProperties.getBb().getBackgroundColor();
  }

  @Override
  public String getForegroundColor() {
    return walletProperties.getBb().getForegroundColor();
  }

  @Override
  public String getLabelColor() {
    return walletProperties.getBb().getLabelColor();
  }
}

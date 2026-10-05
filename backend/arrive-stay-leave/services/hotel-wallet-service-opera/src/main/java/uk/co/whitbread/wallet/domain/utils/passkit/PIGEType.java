package uk.co.whitbread.wallet.domain.utils.passkit;

import uk.co.whitbread.wallet.domain.properties.WalletProperties;

/**
 * Type keeping the UI details for the german hotels when the channel is PI.
 */
public class PIGEType extends PIType {

  public PIGEType(WalletProperties walletProperties) {
    super(walletProperties);
  }

  @Override
  public String getName() {
    return "PIGE";
  }
}
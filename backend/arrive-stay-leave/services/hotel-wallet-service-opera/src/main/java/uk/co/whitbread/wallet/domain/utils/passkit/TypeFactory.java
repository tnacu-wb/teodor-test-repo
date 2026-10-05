package uk.co.whitbread.wallet.domain.utils.passkit;

import uk.co.whitbread.wallet.domain.properties.WalletProperties;

public class TypeFactory {

  private TypeFactory() {
  }

  /**
   * Create the specific Type with the UI details that are dynamically changed when the template is
   * filled in.
   *
   * @param walletProperties the wallet properties
   * @param channel          PI or BB
   * @param brand            PI, Hub or ZIP
   * @param isGEHotel        true if it is a german hotel
   * @return the Type keeping the UI details
   */
  public static Type create(WalletProperties walletProperties, String channel, String brand,
      boolean isGEHotel) {

    if ("BB".equals(channel)) {
      return new BBType(walletProperties);
    }
    if ("HUB".equals(brand)) {
      return new HUBType(walletProperties);
    }
    if ("ZIP".equals(brand)) {
      return new ZIPType(walletProperties);
    }
    if (!isGEHotel) {
      return new PIType(walletProperties);
    } else {
      return new PIGEType(walletProperties);
    }
  }
}
package uk.co.whitbread.basket.domain.ports.primary;


public interface BackgroundChargeInPort {

  void processBackgroundCharge(final String basketReference, final String token);
}

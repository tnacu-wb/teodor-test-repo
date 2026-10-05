package uk.co.whitbread.basket.processor.domain.ports.secondary;

public interface BasketAcknowledgeOutPort {
  void sendAcknowledgeMessage(final String basketReference, final String itemId, final String reqAction,
      final String reservationStatus);
}

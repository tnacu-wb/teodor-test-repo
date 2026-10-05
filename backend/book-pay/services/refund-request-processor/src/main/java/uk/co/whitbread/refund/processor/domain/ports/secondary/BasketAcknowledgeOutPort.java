package uk.co.whitbread.refund.processor.domain.ports.secondary;

public interface BasketAcknowledgeOutPort {

  void sendAcknowledgeMessage(final String basketReference, final String itemId, final String reqAction,
      final boolean isRefunded);
}

package uk.co.whitbread.payment.orchestrator.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payment.orchestrator.domain.model.WebhookPayload;
import uk.co.whitbread.payment.orchestrator.domain.ports.primary.WebhookInPort;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.PaymentWorkflowPort;

/**
 * Use-case implementation for inbound Datatrans webhook handling.
 *
 * <p>Correlates a Datatrans webhook to its payment workflow via the {@code basketId}
 * carried on the callback URL and hands the gateway-scoped payload to the workflow as a
 * signal through {@link PaymentWorkflowPort}. A webhook that carries no correlation key
 * is logged and ignored so the endpoint can still acknowledge it with {@code 200 OK} —
 * Datatrans does not retry on a non-2xx response.
 *
 * <p>Only non-sensitive identifiers are logged; card data is never logged.
 *
 * <p>This class has no Spring annotations; it is wired via
 * {@link uk.co.whitbread.payment.orchestrator.infrastructure.config.InfrastructureBeanConfig}.
 */
@Slf4j
@RequiredArgsConstructor
public class WebhookInPortImpl implements WebhookInPort {

  private final PaymentWorkflowPort paymentWorkflowPort;

  @Override
  public void handleWebhook(String basketId, WebhookPayload payload) {
    String transactionId = payload == null ? null : payload.transactionId();

    if (basketId == null || basketId.isBlank()) {
      log.warn("Datatrans webhook without basketId correlation key [transactionId={}] "
          + "— ignoring", transactionId);
      return;
    }

    log.info("Signalling payment workflow with Datatrans webhook [basketId={}, transactionId={}]",
        basketId, transactionId);
    paymentWorkflowPort.signalWebhookReceived(basketId, payload);
  }
}

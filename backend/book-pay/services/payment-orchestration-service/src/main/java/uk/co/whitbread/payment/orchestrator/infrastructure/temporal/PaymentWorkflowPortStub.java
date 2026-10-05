package uk.co.whitbread.payment.orchestrator.infrastructure.temporal;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uk.co.whitbread.payment.orchestrator.domain.model.AuthorizeResult;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitResult;
import uk.co.whitbread.payment.orchestrator.domain.model.WebhookPayload;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentStatus;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentStatusResponse;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.PaymentWorkflowPort;

/**
 * Stub implementation of {@link PaymentWorkflowPort} used when Temporal is unavailable.
 *
 * <p>Active only under the {@code integration} profile (used for OpenAPI generation)
 * where the real Temporal adapter is excluded.
 */
@Component
@Profile("integration")
public class PaymentWorkflowPortStub implements PaymentWorkflowPort {

  @Override
  public PaymentInitResult initPayment(PaymentInitCommand command) {
    return new PaymentInitResult(true, "stub-transaction-id", null, null);
  }

  @Override
  public AuthorizeResult authorizePayment(String basketId) {
    return new AuthorizeResult(true, null, null);
  }

  @Override
  public PaymentStatusResponse getPaymentStatus(String basketId) {
    return new PaymentStatusResponse(PaymentStatus.INITIALIZED, null);
  }

  @Override
  public void signalWebhookReceived(String basketId, WebhookPayload payload) {
    // No-op under the integration profile — no Temporal server available.
  }
}

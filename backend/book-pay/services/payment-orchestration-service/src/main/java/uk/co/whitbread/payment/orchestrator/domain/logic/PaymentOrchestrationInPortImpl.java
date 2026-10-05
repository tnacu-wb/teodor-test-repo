package uk.co.whitbread.payment.orchestrator.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payment.orchestrator.domain.model.AuthorizeResult;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentErrorCode;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitResult;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentStatusResponse;
import uk.co.whitbread.payment.orchestrator.domain.ports.primary.PaymentOrchestrationInPort;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.PaymentWorkflowPort;

/**
 * Use-case implementation for unified payment orchestration.
 *
 * <p>Coordinates payment initialization and authorization through the unified
 * {@link PaymentWorkflowPort}, delegating to the Temporal workflow adapter.
 *
 * <p>This class has no Spring annotations; it is wired via
 * {@link uk.co.whitbread.payment.orchestrator.infrastructure.config.InfrastructureBeanConfig}.
 */
@Slf4j
@RequiredArgsConstructor
public class PaymentOrchestrationInPortImpl implements PaymentOrchestrationInPort {

  private final PaymentWorkflowPort paymentWorkflowPort;

  @Override
  public PaymentInitResult initPayment(PaymentInitCommand command) {
    log.info("initPayment called for basketId={}, method={}",
        command.basketId(), command.paymentMethod());

    PaymentInitResult result = paymentWorkflowPort.initPayment(command);

    if (result.success()) {
      log.info("Payment init successful for basketId={}, transactionId={}",
          command.basketId(), result.transactionId());
    } else {
      log.warn("Payment init failed for basketId={}, errorCode={}, message={}",
          command.basketId(), result.errorCode(), result.errorMessage());
    }

    return result;
  }

  @Override
  public AuthorizeResult authorizePayment(String basketId) {
    log.info("authorizePayment called for basketId={}", basketId);

    AuthorizeResult result = paymentWorkflowPort.authorizePayment(basketId);

    if (result.success()) {
      log.info("Payment authorized successfully for basketId={}", basketId);
    } else if (PaymentErrorCode.AUTHORIZATION_PENDING.equals(result.errorCode())) {
      // Not a failure: the update is durably accepted and still running in the workflow.
      log.info("Payment authorization still processing for basketId={}; caller will poll",
          basketId);
    } else {
      log.warn("Payment authorization failed for basketId={}, errorCode={}, message={}",
          basketId, result.errorCode(), result.errorMessage());
    }

    return result;
  }

  @Override
  public PaymentStatusResponse getPaymentStatus(String basketId) {
    log.info("getPaymentStatus called for basketId={}", basketId);
    return paymentWorkflowPort.getPaymentStatus(basketId);
  }
}

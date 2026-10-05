package uk.co.whitbread.payment.orchestrator.domain.model.payment.out;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Pins the three lifecycle predicates against every enum constant.
 *
 * <p>Each test asserts the whole set rather than a handful of examples: the predicates cut the
 * lifecycle at three different points, and the bug this test guards against is a new status —
 * or a moved one — quietly landing on the wrong side of one of those cuts.
 */
class PaymentStatusTest {

  @Nested
  @DisplayName("authorizationPhaseComplete()")
  class AuthorizationPhaseComplete {

    @Test
    @DisplayName("is true exactly once the gateway has answered on the authorization")
    void trueForStatusesThatEndTheAuthorizationPhase() {
      Set<PaymentStatus> expected = EnumSet.of(
          PaymentStatus.AUTHORIZED,
          PaymentStatus.SETTLED,
          PaymentStatus.SETTLEMENT_FAILED,
          PaymentStatus.BOOKING_PENDING_TIMEOUT,
          PaymentStatus.FAILED,
          PaymentStatus.CANCELLED,
          PaymentStatus.EXPIRED);

      assertThat(matching(PaymentStatus::authorizationPhaseComplete))
          .isEqualTo(expected);
    }

    @Test
    @DisplayName("is false while the authorization outcome is still outstanding")
    void falseWhileAwaitingTheGateway() {
      assertThat(PaymentStatus.INITIALIZED.authorizationPhaseComplete()).isFalse();
      assertThat(PaymentStatus.SETTLEMENT_PENDING.authorizationPhaseComplete()).isFalse();
    }
  }

  @Nested
  @DisplayName("isFinal()")
  class IsFinal {

    @Test
    @DisplayName("is true exactly for the statuses the workflow can close on")
    void trueForStatusesTheWorkflowClosesOn() {
      Set<PaymentStatus> expected = EnumSet.of(
          PaymentStatus.SETTLED,
          PaymentStatus.CANCELLED,
          PaymentStatus.EXPIRED,
          PaymentStatus.SETTLEMENT_FAILED,
          PaymentStatus.BOOKING_PENDING_TIMEOUT);

      assertThat(matching(PaymentStatus::isFinal)).isEqualTo(expected);
    }

    @Test
    @DisplayName("FAILED is not final — it permits re-initialization, so the workflow stays open")
    void failedIsNotFinal() {
      assertThat(PaymentStatus.FAILED.isFinal()).isFalse();
      assertThat(PaymentStatus.FAILED.authorizationPhaseComplete()).isTrue();
    }

    @Test
    @DisplayName("AUTHORIZED is not final — the booking and settlement phases are still ahead")
    void authorizedIsNotFinal() {
      assertThat(PaymentStatus.AUTHORIZED.isFinal()).isFalse();
      assertThat(PaymentStatus.AUTHORIZED.authorizationPhaseComplete()).isTrue();
    }

    @Test
    @DisplayName("SETTLEMENT_PENDING is not final — the settle retry horizon has not resolved")
    void settlementPendingIsNotFinal() {
      assertThat(PaymentStatus.SETTLEMENT_PENDING.isFinal()).isFalse();
    }
  }

  @Nested
  @DisplayName("holdsFunds()")
  class HoldsFunds {

    @Test
    @DisplayName("is true exactly for the statuses sitting on a live authorization")
    void trueForStatusesHoldingAnAuthorization() {
      Set<PaymentStatus> expected = EnumSet.of(
          PaymentStatus.SETTLEMENT_PENDING,
          PaymentStatus.SETTLEMENT_FAILED,
          PaymentStatus.BOOKING_PENDING_TIMEOUT);

      assertThat(matching(PaymentStatus::holdsFunds)).isEqualTo(expected);
    }

    @Test
    @DisplayName("the two final-but-unreconciled statuses still hold funds")
    void finalStatusesThatStillNeedAnOperator() {
      assertThat(PaymentStatus.SETTLEMENT_FAILED.isFinal()).isTrue();
      assertThat(PaymentStatus.SETTLEMENT_FAILED.holdsFunds()).isTrue();
      assertThat(PaymentStatus.BOOKING_PENDING_TIMEOUT.isFinal()).isTrue();
      assertThat(PaymentStatus.BOOKING_PENDING_TIMEOUT.holdsFunds()).isTrue();
    }
  }

  private static Set<PaymentStatus> matching(Predicate<PaymentStatus> p) {
    Set<PaymentStatus> matches = EnumSet.noneOf(PaymentStatus.class);
    for (PaymentStatus status : PaymentStatus.values()) {
      if (p.test(status)) {
        matches.add(status);
      }
    }
    return matches;
  }
}

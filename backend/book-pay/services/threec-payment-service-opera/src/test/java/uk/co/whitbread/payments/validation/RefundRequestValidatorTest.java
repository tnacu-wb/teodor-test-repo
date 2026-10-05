package uk.co.whitbread.payments.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static uk.co.whitbread.payments.util.PaymentRequestFixtures.getAmount;
import static uk.co.whitbread.payments.util.PaymentRequestFixtures.getBooking;
import static uk.co.whitbread.payments.util.PaymentRequestFixtures.getCard;

import jakarta.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import uk.co.whitbread.payments.model.Mit;
import uk.co.whitbread.payments.model.MitType;
import uk.co.whitbread.payments.model.Refund;
import uk.co.whitbread.payments.model.RefundRequest;

public class RefundRequestValidatorTest {

  private static final String REASON = "Cancelled holiday";

  LocalValidatorFactoryBean localValidatorFactory;
  Mit mit;

  @BeforeEach
  public void setup() {
    localValidatorFactory = new LocalValidatorFactoryBean();
    localValidatorFactory.setProviderClass(HibernateValidator.class);
    localValidatorFactory.afterPropertiesSet();
    mit = new Mit("43686363637", MitType.L);
  }

  @Test
  public void verifyCardTokenMissing() {
    var requestId = UUID.randomUUID().toString();
    var refund = getRefund();
    refund.getCard().setToken(null);

    var paymentRequest = RefundRequest.builder()
        .refund(refund)
        .booking(getBooking("PAY_NOW"))
        .requestId(requestId)
        .build();
    var result = localValidatorFactory.validate(paymentRequest);
    List<String> messages = getErrorMessages(result);
    assertThat("", result, hasSize(1));
    assertThat("", messages, contains("Please provide either the card number or token."));
  }

  @Test
  public void verifyCardExpiryYearMissing() {
    var requestId = UUID.randomUUID().toString();
    var refund = getRefund();
    refund.getCard().setExpiryYear(null);

    var paymentRequest = RefundRequest.builder()
        .refund(refund)
        .booking(getBooking("PAY_NOW"))
        .requestId(requestId)
        .build();
    var result = localValidatorFactory.validate(paymentRequest);
    List<String> messages = getErrorMessages(result);
    assertThat("", result, hasSize(1));
    assertThat("", messages, contains("Please provide the expiry date month and year for the card/token."));
  }

  @Test
  public void verifyCardExpiryMonthMissing() {
    var requestId = UUID.randomUUID().toString();
    var refund = getRefund();
    var booking = getBooking("PAY_NOW");
    refund.getCard().setExpiryMonth(null);

    var paymentRequest = RefundRequest.builder()
        .refund(refund)
        .booking(booking)
        .requestId(requestId)
        .build();
    var result = localValidatorFactory.validate(paymentRequest);
    List<String> messages = getErrorMessages(result);
    assertThat("", result, hasSize(1));
    assertThat("", messages, contains("Please provide the expiry date month and year for the card/token."));
  }

  @Test
  public void verifyValidRefundRequest() {
    var requestId = UUID.randomUUID().toString();
    var paymentRequest = RefundRequest.builder()
        .refund(getRefund())
        .booking(getBooking("PAY_NOW"))
        .requestId(requestId)
        .build();
    var result = localValidatorFactory.validate(paymentRequest);
    assertThat("", result, hasSize(0));
  }

  private List<String> getErrorMessages(Set<ConstraintViolation<RefundRequest>> result) {
    return result.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
  }

  private Refund getRefund() {
    return Refund.builder()
        .amount(getAmount())
        .reason(REASON)
        .card(getCard())
        .type("CARD")
        .build();
  }

}

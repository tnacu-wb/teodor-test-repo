package uk.co.whitbread.payments.model.dynamo;

import static java.util.Optional.of;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import uk.co.whitbread.payments.model.Amount;
import uk.co.whitbread.payments.model.Booking;
import uk.co.whitbread.payments.model.BusinessSite;
import uk.co.whitbread.payments.model.Payment;
import uk.co.whitbread.payments.model.ProviderResponse;
import uk.co.whitbread.payments.model.Refund;
import uk.co.whitbread.payments.model.SaveCardDetails;

@DynamoDbBean
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Slf4j
public class PaymentsSchema {

    private String paymentId;
    private String requestId;
    private String sessionId;
    private String bookingReference;
    private String type;
    private String account;
    private Payment payment;
    private Booking booking;
    private Refund refund;
    private ProviderResponse providerResponse;
    private LocalDateTime createdOn;
    private boolean refunded;
    private LocalDateTime refundedOn;
    private String template;
    private long expire;
    private SaveCardDetails saveCardDetails;


    @DynamoDbPartitionKey
    @DynamoDbAttribute(value = "payment-id")
    public String getPaymentId() {
        return paymentId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "request-id")
    @DynamoDbAttribute(value = "request-id")
    public String getRequestId() {
        return requestId;
    }

    public String getHotelCode() {
        return of(this)
                .map(PaymentsSchema::getBooking)
                .map(Booking::getBusinessSite)
                .map(BusinessSite::getIdentifier)
                .orElse(null);
    }

    public String getPaymentSubType() {
        return of(this)
                .map(PaymentsSchema::getPayment)
                .map(Payment::getSubType)
                .orElse(null);
    }

    public PaymentsSchema validate() {
        if (paymentId == null) {
            log.warn("paymentId is missing");
        }
        if (requestId == null) {
            log.warn("requestId is missing");
        }

        validateBooking();
        validatePayment();
        validateRefund();
        return this;
    }

    private void validateBooking() {
        if (booking == null) {
            return;
        }

        if (booking.getChannel() == null) {
            log.warn("booking.channel is missing for paymentId: {}", paymentId);
        }
        if (booking.getJourney() == null) {
            log.warn("booking.journey is missing for paymentId: {}", paymentId);
        }
        if (booking.getType() == null) {
            log.warn("booking.type is missing for paymentId: {}", paymentId);
        }

        validateBusinessSite();
    }

    private void validateBusinessSite() {
        if (booking.getBusinessSite() == null) {
            log.warn("booking.businessSite is missing for paymentId: {}", paymentId);
            return;
        }

        if (booking.getBusinessSite().getIdentifier() == null) {
            log.warn("booking.businessSite.identifier is missing for paymentId: {}", paymentId);
        }
        if (booking.getBusinessSite().getType() == null) {
            log.warn("booking.businessSite.type is missing for paymentId: {}", paymentId);
        }
    }

    private void validatePayment() {
        if (payment == null) {
            return;
        }

        validateAmount(payment.getAmount(), "payment");

        if ("ECOMM".equals(payment.getSubType()) && payment.getEnvironment() == null) {
            log.warn("payment.environment is missing when subType is ECOMM for paymentId: {}", paymentId);
        }
    }

    private void validateRefund() {
        if (refund == null) {
            return;
        }

        if (refund.getType() == null) {
            log.warn("refund.type is missing for paymentId: {}", paymentId);
        }
        if (refund.getCard() == null) {
            log.warn("refund.card is missing for paymentId: {}", paymentId);
        }
        if (refund.getReason() == null) {
            log.warn("refund.reason is missing for paymentId: {}", paymentId);
        }

        validateAmount(refund.getAmount(), "refund");
    }

    private void validateAmount(Amount amount, String context) {
        if (amount == null) {
            log.warn("{}.amount is missing for paymentId: {}", context, paymentId);
            return;
        }

        if (amount.getCurrency() == null) {
            log.warn("{}.amount.currency is missing for paymentId: {}", context, paymentId);
        }
    }
}

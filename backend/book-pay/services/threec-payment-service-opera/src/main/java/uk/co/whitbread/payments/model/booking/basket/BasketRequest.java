package uk.co.whitbread.payments.model.booking.basket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class BasketRequest {

    private String reference; //basketReference
    private String paymentId; //needed for refund
    private String paymentStatus; // SUCCESS("SUCCESS"),FAILURE("FAILURE"),PENDING("PENDING"),NO_PAYMENT_ATTEMPT("NO_PAYMENT_ATTEMPT")
    private String returnCode;
    private String bookingReference;
    private String countryCode;
    private String language;
    private String firstName;
    private String lastName;
    private String channel;
    private String last4Digits;
    private String cardSchemeId;
    private String token;
    private String expiry;
    private String fraudCheckDecision;
    private String threeDSIndicator;
}

package uk.co.whitbread.payments.validation;

import uk.co.whitbread.payments.model.Booking;
import uk.co.whitbread.payments.model.Card;
import uk.co.whitbread.payments.model.Refund;
import uk.co.whitbread.payments.model.RefundRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class RefundRequestValidator implements ConstraintValidator<ValidRefundRequest, RefundRequest> {

    private static final String REQUEST_ID_MISSING = "Please mention unique request id.";
    private static final String BUSINESS_SITE_MISSING = "Please specify Business Site.";
    private static final String EXPIRY_MISSING = "Please provide the expiry date month and year for the card/token.";
    private static final String CARD_MISSING = "At least one card must be provided.";
    private static final String BOTH_NULL_MESSAGE = "Please provide either the card number or token.";

    @Override
    public void initialize(ValidRefundRequest constraintAnnotation) {
        // empty
    }

    @Override
    public boolean isValid(RefundRequest value, ConstraintValidatorContext context) {
        if (value == null || value.getRefund() == null || value.getBooking() == null) {
            return true;
        }
        var refund = value.getRefund();
        var booking = value.getBooking();

        return validateRefund(context, refund) && validateBooking(context, booking) && validateRequestIdIsPresent(context, value);

    }

    private boolean validateRefund(ConstraintValidatorContext context, Refund refund) {
        return validateCardIsPresent(context, refund)
                && validateTokenIsPresent(context, refund)
                && validateExpiryDate(context, refund);
    }

    private boolean validateBooking(ConstraintValidatorContext context, Booking booking) {
        return validateBusinessSiteIsPresent(context, booking);
    }

    private boolean validateBusinessSiteIsPresent(ConstraintValidatorContext context, Booking booking) {
        if (ofNullable(booking.getBusinessSite()).isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(BUSINESS_SITE_MISSING)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean validateRequestIdIsPresent(ConstraintValidatorContext context, RefundRequest request) {
        if (ofNullable(request.getRequestId()).isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(REQUEST_ID_MISSING)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean validateCardIsPresent(ConstraintValidatorContext context, Refund refund) {
        if (getCard(refund).isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(CARD_MISSING)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean validateTokenIsPresent(ConstraintValidatorContext context, Refund refund) {
        Optional<Card> card = getCard(refund)
                .filter(cardFound -> ofNullable(cardFound.getToken()).isPresent());

        if (card.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(BOTH_NULL_MESSAGE).addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean validateExpiryDate(ConstraintValidatorContext context, Refund refund) {
        Optional<Card> card = getCard(refund)
                .filter(cardFound -> ofNullable(cardFound.getExpiryYear()).isEmpty() || ofNullable(cardFound.getExpiryMonth()).isEmpty());

        if (card.isPresent()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(EXPIRY_MISSING)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private Optional<Card> getCard(Refund refund) {
        return ofNullable(refund.getCard());
    }

}

package uk.co.whitbread.payments.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.payments.model.*;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static uk.co.whitbread.payments.model.PaymentSubType.ECKOH;
import static uk.co.whitbread.payments.model.PaymentSubType.MOTO;
import static uk.co.whitbread.payments.model.PaymentSubType.PAYPAL;

public class PaymentRequestValidator implements ConstraintValidator<ValidPaymentRequest, PaymentRequest> {

    private static final String BOOKING_DATE_ISSUE = "Departure date must be greater than Arrival date";
    private static final String LANGUAGE_MISSING = "Please specify a language.";
    private static final String TOKEN_MISSING = "Please provide a card token.";
    private static final String EXPIRY_MISSING = "Please provide the expiry date month and year for the card/token.";
    private static final String CARD_MISSING = "At least one card must be provided.";
    private static final String VALID_COUNTRY = "Please provide valid ISO country code.";
    private static final String LINE1_MISSING = "Please provide a line1 for the billing address.";
    private static final String PAYPAL_NONCE_MISSING = "Please specify a paypal nonce.";
    private static final String PAYPAL_DEVICE_DATA_MISSING = "Please specify a paypal device data.";

    @Override
    public boolean isValid(PaymentRequest value, ConstraintValidatorContext context) {

        if (value == null || value.getPayment() == null || value.getBooking() == null || value.getPayment().isEckohTransaction(List.of(ECKOH))) {
            return true;
        }

        var payment = value.getPayment();
        var booking = value.getBooking();

        boolean isBillingValid = validateBilling(value.getBooking().getChannel(), payment.getBilling(), context);

        if (isBillingValid) {
            if (isMoto(payment, booking)) {
                return validateMOTO(context, payment);
            }else if(isPayPal(payment, booking)){
                return validatePayPal(context, payment);
            }else {
                return validateECOMM(context, payment, booking);
            }
        }
        return false;
    }

    private boolean validateBilling(final String channel, final Billing billing, ConstraintValidatorContext context) {

        if (!ChannelType.FRONT_DESK.name().equals(channel) && !ChannelType.GDS.name().equals(channel)) {
            return validateAddress(billing.getAddress(), context); //ccc or web or apps
        }
        return true;
    }

    private boolean validateAddress(final Address address, ConstraintValidatorContext context) {
        boolean result = true;

        if (StringUtils.isEmpty(address.getLine1())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(LINE1_MISSING)
                    .addConstraintViolation();
            result = false;
        }

        if (!Arrays.asList(Locale.getISOCountries()).contains(address.getCountryCode())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(VALID_COUNTRY)
                    .addConstraintViolation();
            result = false;
        }

        return result;
    }

    private boolean validateMOTO(ConstraintValidatorContext context, Payment payment) {
        return validateCardIsPresent(context, payment)
                && validateTokenIsPresent(context, payment)
                && validateExpiryDate(context, payment);
    }

    private boolean validateECOMM(ConstraintValidatorContext context, Payment payment, Booking booking) {
        return validateLanguageIsPresent(context, booking)
                && validateTokenIsPresent(context, payment)
                && validateArrivalAndDepartureDates(context, booking);
    }

    private boolean validatePayPal(ConstraintValidatorContext context, Payment payment) {
        return validatePaypalNonceIsPresent(context, payment)
                && validatePaypalDeviceDataIsPresent(context, payment);
    }

    private boolean validateCardIsPresent(ConstraintValidatorContext context, Payment payment) {
        if (getCard(payment).isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(CARD_MISSING)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean validateExpiryDate(ConstraintValidatorContext context, Payment payment) {
        Optional<Card> card = getCard(payment)
                .filter(cardFound -> ofNullable(cardFound.getExpiryYear()).isEmpty() || ofNullable(cardFound.getExpiryMonth()).isEmpty());

        if (card.isPresent()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(EXPIRY_MISSING)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean validateLanguageIsPresent(ConstraintValidatorContext context, Booking booking) {
        if (ofNullable(booking.getLanguage()).isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(LANGUAGE_MISSING)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean validateTokenIsPresent(ConstraintValidatorContext context, Payment payment) {
        Optional<Card> card = getCard(payment)
                .filter(cardFound -> ofNullable(cardFound.getToken()).isEmpty());

        if (card.isPresent()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(TOKEN_MISSING)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean validateArrivalAndDepartureDates(ConstraintValidatorContext context, Booking booking) {
        if (booking.getDepartureDate() != null && booking.getArrivalDate() != null) {
            if (booking.getDepartureDate().isBefore(booking.getArrivalDate()) || booking.getDepartureDate().isEqual(booking.getArrivalDate())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(BOOKING_DATE_ISSUE).addConstraintViolation();
                return false;
            }
        }
        return true;
    }

    private Optional<Card> getCard(Payment payment) {
        return ofNullable(payment.getCard());
    }

    private boolean isMoto(Payment payment, Booking booking) {
        return payment.getSubType().equals(MOTO.name()) &&
                (booking.getChannel().equals(ChannelType.CCC.name())
                        || booking.getChannel().equals(ChannelType.GDS.name())
                        || booking.getChannel().equals(ChannelType.FRONT_DESK.name()));
    }

    private boolean isPayPal(Payment payment, Booking booking) {
        return payment.getType().equals(PAYPAL.name());
    }

    private boolean validatePaypalNonceIsPresent(ConstraintValidatorContext context, Payment payment) {
        if (ofNullable(payment.getPaypalNonce()).isEmpty() || payment.getPaypalNonce().trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(PAYPAL_NONCE_MISSING)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
    private boolean validatePaypalDeviceDataIsPresent(ConstraintValidatorContext context, Payment payment) {
        if (ofNullable(payment.getPaypalDeviceData()).isEmpty() || payment.getPaypalDeviceData().trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(PAYPAL_DEVICE_DATA_MISSING)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}

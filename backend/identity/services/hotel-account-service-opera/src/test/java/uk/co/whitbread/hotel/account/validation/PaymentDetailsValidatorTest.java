package uk.co.whitbread.hotel.account.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.account.model.BillingAddress;
import uk.co.whitbread.hotel.account.model.PaymentCard;

@ExtendWith(MockitoExtension.class)
class PaymentDetailsValidatorTest {

    private static final String VALID_CARD_NUMBER = "4242424242424242";

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;

    @InjectMocks
    private PaymentDetailsValidator paymentDetailsValidator;

    @Test
    void nullObjectShouldBeValid() {
        assertTrue(paymentDetailsValidator.isValid(null, null));
    }

    @Test
    void emptyObjectShouldBeValid() {
        assertTrue(paymentDetailsValidator.isValid(new PaymentCard(), null));
    }

    @Test
    void paymentCardMissingOneMandatoryFieldShouldBeInvalid() {
        when(constraintValidatorContext.buildConstraintViolationWithTemplate(
            anyString())).thenReturn(constraintViolationBuilder);
        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setStartDate("07/17");
        assertFalse(paymentDetailsValidator.isValid(paymentCard, constraintValidatorContext));

        paymentCard.setExpiryDate("07/20");
        assertFalse(paymentDetailsValidator.isValid(paymentCard, constraintValidatorContext));

        paymentCard.setCardHolderName("Name");
        assertFalse(paymentDetailsValidator.isValid(paymentCard, constraintValidatorContext));

        paymentCard.setCardNumber("******");
        assertFalse(paymentDetailsValidator.isValid(paymentCard, constraintValidatorContext));

    }

    @Test
    void paymentCardShouldBeValidWhenAllMandatoryFieldsArePresent() {
        PaymentCard paymentCard = buildValidPaymentcard();

        assertTrue(paymentDetailsValidator.isValid(paymentCard, null));
    }

    @Test
    void invalidLuhnCardNumber_fail() {
        when(constraintValidatorContext.buildConstraintViolationWithTemplate(
            anyString())).thenReturn(constraintViolationBuilder);
        PaymentCard paymentCard = buildValidPaymentcard();
        paymentCard.setCardNumber("1234567890123456");

        assertFalse(paymentDetailsValidator.isValid(paymentCard, constraintValidatorContext));
    }

    @Test
    void validLuhnCardNumber_success() {
        PaymentCard paymentCard = buildValidPaymentcard();
        paymentCard.setCardNumber(VALID_CARD_NUMBER);

        assertTrue(paymentDetailsValidator.isValid(paymentCard, constraintValidatorContext));
    }

    @Test
    void invalidCardType_fail() {
        when(constraintValidatorContext.buildConstraintViolationWithTemplate(
            anyString())).thenReturn(constraintViolationBuilder);
        PaymentCard paymentCard = buildValidPaymentcard();
        paymentCard.setCardType("abc");

        assertFalse(paymentDetailsValidator.isValid(paymentCard, constraintValidatorContext));
    }

    @Test
    void invalidExpiryDate_fail() {
        when(constraintValidatorContext.buildConstraintViolationWithTemplate(
            anyString())).thenReturn(constraintViolationBuilder);
        PaymentCard paymentCard = buildValidPaymentcard();
        paymentCard.setExpiryDate("20/30");

        assertFalse(paymentDetailsValidator.isValid(paymentCard, constraintValidatorContext));
    }

    @Test
    void expiryDateBeforeCurrentDate_fail() {
        when(constraintValidatorContext.buildConstraintViolationWithTemplate(
            anyString())).thenReturn(constraintViolationBuilder);
        PaymentCard paymentCard = buildValidPaymentcard();
        paymentCard.setExpiryDate("05/20");

        assertFalse(paymentDetailsValidator.isValid(paymentCard, constraintValidatorContext));
    }

    @Test
    void emptyCnpBusinessAccountPassword_fail() {
        when(constraintValidatorContext.buildConstraintViolationWithTemplate(
            anyString())).thenReturn(constraintViolationBuilder);
        PaymentCard paymentCard = buildValidPaymentcard();
        paymentCard.setCnpRequired(true);

        assertFalse(paymentDetailsValidator.isValid(paymentCard, constraintValidatorContext));
    }

    @Test
    void validCnpBusinessAccountPassword_success() {
        PaymentCard paymentCard = buildValidPaymentcard();
        paymentCard.setCnpRequired(true);
        paymentCard.setCnpBusinessAccountPassword("pass");

        assertTrue(paymentDetailsValidator.isValid(paymentCard, constraintValidatorContext));
    }

    private PaymentCard buildValidPaymentcard() {
        YearMonth expiryDate = YearMonth.now().plusMonths(5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyy");

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setExpiryDate(expiryDate.format(formatter));
        paymentCard.setCardHolderName("Name");
        paymentCard.setCardNumber("************1234");
        paymentCard.setCardType("VI");

        BillingAddress billingAddress = new BillingAddress();
        billingAddress.setLine1("Line 1");
        billingAddress.setPostCode("EC1A 1AA");
        billingAddress.setCountryCode("GB");
        paymentCard.setBillingAddress(billingAddress);

        return paymentCard;
    }
}

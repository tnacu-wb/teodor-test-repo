package uk.co.whitbread.hotel.payment.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

public class PaymentCardTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void shouldValidatePaymentCard() {
        //Given
        PaymentCard paymentCard = new PaymentCard();
        //When
        List<String> validationMessages = validator.validate(paymentCard).stream().
                map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage()).collect(toList());

        //Then
        assertThat(validationMessages, hasSize(4));
        assertThat(validationMessages, hasItem("cardType may not be null"));
        assertThat(validationMessages, hasItem("cardholderName may not be null"));
        assertThat(validationMessages, hasItem("expiryDate may not be null"));
        assertThat(validationMessages, hasItem("cardNumber may not be null"));
    }

}
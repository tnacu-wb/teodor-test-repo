package uk.co.whitbread.hotel.payment.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

public class PaymentTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void shouldValidateBookingCriteria() {
        //Given
        Payment payment = new Payment();

        //When
        List<String> validationMessages = validator.validate(payment).stream().
                map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage()).collect(toList());

        //Then
        assertThat(validationMessages, hasSize(4));
        assertThat(validationMessages, hasItem("booker must not be null"));
        assertThat(validationMessages, hasItem("guests must not be null"));
        assertThat(validationMessages, hasItem("donation must not be null"));
        assertThat(validationMessages, hasItem("paymentCard must not be null"));
    }
}
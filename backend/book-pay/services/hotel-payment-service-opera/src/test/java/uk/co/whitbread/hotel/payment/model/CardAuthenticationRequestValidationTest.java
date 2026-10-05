package uk.co.whitbread.hotel.payment.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

public class CardAuthenticationRequestValidationTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void shouldValidateMandatoryFields() {
        //Given
        CardAuthenticationRequest request = new CardAuthenticationRequest();

        //When
        List<String> validationMessages = validator.validate(request).stream().
                map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage()).collect(toList());

        //Then
        assertThat(validationMessages, hasSize(3));
        assertThat(validationMessages, hasItem("redirectUrl must not be null"));
        assertThat(validationMessages, hasItem("redirectUrl must not be empty"));
        assertThat(validationMessages, hasItem("paymentCard must not be null"));
    }
}

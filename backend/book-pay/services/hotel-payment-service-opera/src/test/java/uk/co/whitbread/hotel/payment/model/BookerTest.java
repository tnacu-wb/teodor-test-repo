package uk.co.whitbread.hotel.payment.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

public class BookerTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void shouldValidateBooker() {
        //Given
        Booker booker = new Booker();
        booker.setAddress(new Address());

        //When
        List<String> validationMessages = validator.validate(booker).stream().
                map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage()).collect(toList());

        //Then
        assertThat(validationMessages, hasSize(7));
        assertThat(validationMessages, hasItem("address.line1 must not be empty"));
        assertThat(validationMessages, hasItem("address.countryCode must not be empty"));
        assertThat(validationMessages, hasItem("title must not be empty"));
        assertThat(validationMessages, hasItem("firstName must not be empty"));
        assertThat(validationMessages, hasItem(" telephone or mobile number must be present"));
        assertThat(validationMessages, hasItem("emailAddress must not be empty"));
        assertThat(validationMessages, hasItem("lastName must not be empty"));
    }
}
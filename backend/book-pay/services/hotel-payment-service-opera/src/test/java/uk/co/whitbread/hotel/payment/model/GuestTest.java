package uk.co.whitbread.hotel.payment.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

public class GuestTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void shouldValidateGuest() {
        //Given
        Guest guest = new Guest();

        //When
        List<String> validationMessages = validator.validate(guest).stream().
                map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage()).collect(toList());

        //Then
        assertThat(validationMessages, hasSize(4));
        assertThat(validationMessages, hasItem("roomNumber must not be null"));
        assertThat(validationMessages, hasItem("title must not be empty"));
        assertThat(validationMessages, hasItem("firstName must not be empty"));
        assertThat(validationMessages, hasItem("lastName must not be empty"));
    }
}
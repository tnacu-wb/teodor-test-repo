package uk.co.whitbread.hotel.account.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;



import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

public class LoginRequestTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void shouldValidateBookingCriteria() {
        //Given
        LoginRequest loginRequest = new LoginRequest();

        //When
        List<String> validationMessages = validator.validate(loginRequest).stream().
                map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage()).collect(toList());

        //Then
        assertThat(validationMessages, hasSize(2));
        assertThat(validationMessages, hasItem("password must not be empty"));
        assertThat(validationMessages, hasItem("username must not be empty"));
    }
}
package uk.co.whitbread.hotel.payment.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

public class AddressValidationTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void shouldValidateAddress() {
        //Given
        Address address = new Address();

        //When
        List<String> validationMessages = validator.validate(address).stream().
                map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage()).collect(toList());

        //Then
        assertThat(validationMessages, hasSize(2));
        assertThat(validationMessages, hasItem("countryCode must not be empty"));
        assertThat(validationMessages, hasItem("line1 must not be empty"));
    }

    @Test
    public void shouldValidateAddressPostcode() {
        //Given
        Address address = new Address();
        address.setCountryCode("GB");
        address.setLine1("line1");

        //When
        List<String> validationMessages = validator.validate(address).stream().
                map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage()).collect(toList());

        //Then
        assertThat(validationMessages, hasSize(1));
        assertThat(validationMessages, hasItem(" postcode must be present when countryCode is 'GB'"));
    }
}
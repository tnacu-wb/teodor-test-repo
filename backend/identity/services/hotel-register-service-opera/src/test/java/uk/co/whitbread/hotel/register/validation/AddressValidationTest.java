package uk.co.whitbread.hotel.register.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.register.model.Address;

import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

public class AddressValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void shouldReturnAListOfErrorsWhenAnEmptyCompanyNameIsProvided() {
        //Given
        Address address = random(Address.class);
        address.setLine1(null);
        address.setCountryCode(null);

        //When
        List<String> validationMessages = validator.validate(address)
                .stream()
                .map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage())
                .collect(toList());

        //Then
        assertThat(validationMessages.size(), is(2));
        assertThat(validationMessages, hasItem("countryCode must not be empty"));
        assertThat(validationMessages, hasItem("line1 must not be empty"));
    }

    @Test
    public void shouldReturnAnEmptyListOfErrorsWhenAValidModelIsProvided() {
        //Given
        Address address = random(Address.class);

        //When
        List<String> validationMessages = validator.validate(address)
                .stream()
                .map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage())
                .collect(toList());

        //Then
        assertThat(validationMessages, is(empty()));
    }
}

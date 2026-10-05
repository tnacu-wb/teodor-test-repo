package uk.co.whitbread.hotel.register.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.register.model.CustomerResponse;

import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

public class CustomerResponseTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void shouldReturnAnEmptyListOfErrorsWhenAValidModelIsProvided() {
        //Given
        CustomerResponse address = random(CustomerResponse.class);

        //When
        List<String> validationMessages = validator.validate(address)
                .stream()
                .map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage())
                .collect(toList());

        //Then
        assertThat(validationMessages, is(empty()));
    }
}

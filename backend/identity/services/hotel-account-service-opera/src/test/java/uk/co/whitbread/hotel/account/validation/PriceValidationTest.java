package uk.co.whitbread.hotel.account.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.account.model.Price;

import java.math.BigDecimal;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by KrakenDevTeam on 28/12/2016.
 */
public class PriceValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void shouldReturnAListOfErrorsWhenANullCurrencyIsProvided() {
        //Given
        Price price = random(Price.class);
        price.setCurrency(null);

        //When
        List<String> validationMessages = validator.validate(price)
                .stream()
                .map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage())
                .collect(toList());

        //Then
        assertThat(validationMessages, is(not(empty())));
    }

    @Test
    public void shouldReturnAnEmptyListOfErrorsWhenAValidModelIsProvided() {
        //Given
        Price price = random(Price.class);
        price.setAmount(BigDecimal.valueOf(199.00));

        //When
        List<String> validationMessages = validator.validate(price)
                .stream()
                .map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage())
                .collect(toList());

        //Then
        assertThat(validationMessages, is(empty()));
    }
}

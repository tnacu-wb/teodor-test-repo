package uk.co.whitbread.hotel.register.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.register.model.RoomCriteria;

import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RoomCriteriaValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void shouldReturnAListOfErrorsWhenANullRoomTypeIsProvided() {
        //Given
        RoomCriteria roomCriteria = random(RoomCriteria.class);
        roomCriteria.setType(null);

        //When
        List<String> validationMessages = validator.validate(roomCriteria)
                .stream()
                .map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage())
                .collect(toList());

        //Then
        assertThat(validationMessages, is(not(empty())));
    }

    @Test
    public void shouldReturnAnEmptyListOfErrorsWhenAValidModelIsProvided() {
        //Given
        RoomCriteria roomCriteria = random(RoomCriteria.class);
        roomCriteria.setAdults(1);
        roomCriteria.setChildren(0);

        //When
        List<String> validationMessages = validator.validate(roomCriteria)
                .stream()
                .map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage())
                .collect(toList());

        //Then
        assertThat(validationMessages, is(empty()));
    }
}

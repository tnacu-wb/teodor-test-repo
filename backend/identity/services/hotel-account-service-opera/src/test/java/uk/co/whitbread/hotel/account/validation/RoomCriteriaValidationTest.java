package uk.co.whitbread.hotel.account.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.co.whitbread.hotel.account.model.RoomCriteria;

import java.util.List;
import java.util.Set;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class RoomCriteriaValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldReturnAListOfErrorsWhenANullRoomTypeIsProvided() {
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
    void shouldReturnAnEmptyListOfErrorsWhenAValidModelIsProvided() {
        //Given
        RoomCriteria roomCriteria = random(RoomCriteria.class);
        roomCriteria.setAdults(1L);
        roomCriteria.setChildren(0L);
        roomCriteria.setType("SB"); // Single room type for 1 adult and 0 children

        //When
        List<String> validationMessages = validator.validate(roomCriteria)
                .stream()
                .map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage())
                .collect(toList());

        //Then
        assertThat(validationMessages, is(empty()));
    }

    @ParameterizedTest
    @CsvSource({
        "0, 1",
        "3, 1",
        "2, 3"
    })
    void testRoomCriteria_ShouldReturnValidationErrorWhenAdultsAndChildrenSizeIsUnaccepted(Long adults, Long children) {
        RoomCriteria roomCriteria = new RoomCriteria();
        roomCriteria.setCotRequired(true);
        roomCriteria.setAdults(adults);
        roomCriteria.setChildren(children);
        roomCriteria.setType("FAM");

        Set<ConstraintViolation<RoomCriteria>> violations = validator.validate(roomCriteria);
        Assertions.assertThat(violations).hasSize(1);
    }

}

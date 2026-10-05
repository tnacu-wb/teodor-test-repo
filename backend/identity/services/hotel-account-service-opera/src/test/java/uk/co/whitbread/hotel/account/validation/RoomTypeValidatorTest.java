package uk.co.whitbread.hotel.account.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.co.whitbread.hotel.account.model.RoomCriteria;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RoomTypeValidatorTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator();
    }
  }

  @ParameterizedTest
  @CsvSource({
      "1, 0, SB, true",
      "1, 0, DB, true",
      "1, 0, TWIN, false",
      "1, 0, DIS, true",
      "1, 0, FAM, false",
      "1, 0, null, false",
      "1, 0, OTHER, false",
      "1, 1, SB, false",
      "1, 1, DB, false",
      "1, 1, TWIN, false",
      "1, 1, DIS, false",
      "1, 1, FAM, true",
      "1, 1, null, false",
      "1, 1, OTHER, false",
      "1, 2, SB, false",
      "1, 2, DB, false",
      "1, 2, TWIN, false",
      "1, 2, DIS, false",
      "1, 2, FAM, true",
      "1, 2, null, false",
      "1, 2, OTHER, false",
      "2, 0, SB, false",
      "2, 0, DB, true",
      "2, 0, TWIN, true",
      "2, 0, DIS, true",
      "2, 0, FAM, false",
      "2, 0, null, false",
      "2, 0, OTHER, false",
      "2, 1, SB, false",
      "2, 1, DB, false",
      "2, 1, TWIN, false",
      "2, 1, DIS, false",
      "2, 1, FAM, true",
      "2, 1, null, false",
      "2, 1, OTHER, false",
      "2, 2, SB, false",
      "2, 2, DB, false",
      "2, 2, TWIN, false",
      "2, 2, DIS, false",
      "2, 2, FAM, true",
      "2, 2, null, false",
      "2, 2, OTHER, false"
  })
  void testRoomTypeValidation_1adult0children(Long adults, Long children, String type, boolean expectedValidity) {
    RoomCriteria roomCriteria = new RoomCriteria();
    roomCriteria.setCotRequired(true);
    roomCriteria.setAdults(adults);
    roomCriteria.setChildren(children);
    roomCriteria.setType(type);

    Set<ConstraintViolation<RoomCriteria>> violations = validator.validate(roomCriteria);
    boolean isValid = violations.isEmpty();

    assertThat(isValid).isEqualTo(expectedValidity);
  }

  @Test
  void testInvalidRoomType_NullFields() {
    RoomCriteria roomCriteria = new RoomCriteria();
    roomCriteria.setCotRequired(true);
    roomCriteria.setAdults(null);
    roomCriteria.setChildren(null);
    roomCriteria.setType(null);

    Set<ConstraintViolation<RoomCriteria>> violations = validator.validate(roomCriteria);
    assertThat(violations).hasSize(4); // @NotNull violations for adults, children, and type & invalid type
  }

}

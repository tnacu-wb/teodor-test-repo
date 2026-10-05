package uk.co.whitbread.availabilitycacheservice.infrastructure.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.co.whitbread.availabilitycacheservice.infrastructure.properties.HotelPriceProperties;


public class RoomTypeValidatorTest {

  private final HotelPriceProperties mockHotelPriceProperties = Mockito.mock(HotelPriceProperties.class);

  private final List<ConstraintValidator<?, ?>> customConstraintValidators =
      Collections.singletonList(new RoomTypeValidator(mockHotelPriceProperties));

  private final ValidatorFactory customValidatorFactory =
      new CustomLocalValidatorFactoryBean(customConstraintValidators);

  private final Validator validator = customValidatorFactory.getValidator();

  private final String ERROR_MESSAGE = "Invalid roomType";


  @BeforeEach
  public void setup() {
    Mockito.when(mockHotelPriceProperties.getRoomTypes())
        .thenReturn(Arrays.asList("DB", "TWIN", "DIS", "FAM", "PRE", "SB"));
  }

  @Test
  public void isValidShouldReturnTrueForDBRoomType() {

    RoomTypeValidatorTest.DummyClass dc = new RoomTypeValidatorTest.DummyClass("DB");
    Set<ConstraintViolation<DummyClass>> violations = validator.validate(dc);

    assertThat("", violations, hasSize(0));

    //isValidShouldReturnTrueForTWINRoomType
    dc = new RoomTypeValidatorTest.DummyClass("TWIN");
    violations = validator.validate(dc);
    assertThat("", violations, hasSize(0));

  }

  @Test
  public void isValidShouldReturnTrueForSBRoomType() {

    RoomTypeValidatorTest.DummyClass dc = new RoomTypeValidatorTest.DummyClass("SB");
    Set<ConstraintViolation<DummyClass>> violations = validator.validate(dc);

    assertThat("", violations, hasSize(0));

  }

  @Test
  public void isValidShouldReturnFalseForNULLRoomType() {

    RoomTypeValidatorTest.DummyClass dc = new RoomTypeValidatorTest.DummyClass(null);
    Set<ConstraintViolation<DummyClass>> violations = validator.validate(dc);

    assertThat("", violations, hasSize(1));
    List<String> messages = getErrorMessages(violations);
    assertThat("", messages, containsInAnyOrder(ERROR_MESSAGE));

    //isValidShouldReturnFalseForInvalidRoomType1
    dc = new RoomTypeValidatorTest.DummyClass(" ");
    violations = validator.validate(dc);

    assertThat("", violations, hasSize(1));
    messages = getErrorMessages(violations);
    assertThat("", messages, containsInAnyOrder(ERROR_MESSAGE));

  }

  @Test
  public void isValidShouldReturnFalseForInvalidRoomType2() {

    RoomTypeValidatorTest.DummyClass dc = new RoomTypeValidatorTest.DummyClass("INVALID");
    Set<ConstraintViolation<DummyClass>> violations = validator.validate(dc);

    assertThat("", violations, hasSize(1));
    List<String> messages = getErrorMessages(violations);
    assertThat("", messages, containsInAnyOrder(ERROR_MESSAGE));

  }

  private List<String> getErrorMessages(Set<ConstraintViolation<DummyClass>> result) {
    return result.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
  }

  @RoomTypeConstraint(roomType = "roomType")
  @Data
  @AllArgsConstructor
  private static class DummyClass {

    private String roomType;
  }

}

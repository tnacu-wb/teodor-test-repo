package uk.co.whitbread.content.infrastructure.rest.controller.labels;

import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.exceptions.LabelsBadRequestException;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.model.in.CategoryEnumDto;

class StringToCategoryEnumMapperTest {

  @ParameterizedTest
  @MethodSource("provideParameters")
  void convert__ShouldReturnCategoryEnum(String source, String sourceFixed)
      throws IllegalArgumentException {
    //Arrange
    CategoryEnumDto categoryEnumDto;
    StringToCategoryEnumMapper stringToCategoryEnumMapper = new StringToCategoryEnumMapper();

    //Act
    categoryEnumDto = stringToCategoryEnumMapper.convert(source);

    //Assert
    Assertions.assertNotNull(categoryEnumDto);
    assertEquals(categoryEnumDto.toString(), sourceFixed, categoryEnumDto.toString());
  }

  @Test
  void convert__ShouldThrowException() throws IllegalArgumentException {
    //Arrange
    String source = "      -BOOKING";
    StringToCategoryEnumMapper stringToCategoryEnumMapper = new StringToCategoryEnumMapper();

    //Act_Assert
    Assertions.assertThrows(LabelsBadRequestException.class, () -> {
      stringToCategoryEnumMapper.convert(source);
    });
  }

  private static Stream<Arguments> provideParameters() {
    return Stream.of(
        Arguments.of("      MAIN", "MAIN"),
        Arguments.of("      PI-BOOKINGS", "PI_BOOKINGS"),
        Arguments.of("      BOOKING", "BOOKING")
    );
  }
}
package uk.co.whitbread.content.infrastructure.rest.controller.labels;

import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.exceptions.LabelsBadRequestException;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.model.in.CategoryEnumDto;

class StringToListCategoryEnumMapperTest {

  @Test
  void convert__ShouldReturnCategoryEnumMain() throws IllegalArgumentException {
    //Arrange
    String source = "      MAIN";
    String sourceFixed = "MAIN";
    List<CategoryEnumDto> categoryEnumsDto = new ArrayList<>();
    categoryEnumsDto.add(CategoryEnumDto.MAIN);
    StringToListCategoryEnumMapper stringToListCategoryEnumMapper = new StringToListCategoryEnumMapper();

    //Act
    categoryEnumsDto = stringToListCategoryEnumMapper.convert(source);

    //Assert
    Assertions.assertNotNull(categoryEnumsDto);
    assertEquals(categoryEnumsDto.toString(), sourceFixed, categoryEnumsDto.get(0).toString());
  }

  @Test
  void convert__ShouldReturnCategoryEnumPiBookings() throws IllegalArgumentException {
    //Arrange
    String source = "      PI-BOOKINGS";
    String sourceFixed = "PI_BOOKINGS";
    List<CategoryEnumDto> categoryEnumsDto = new ArrayList<>();
    categoryEnumsDto.add(CategoryEnumDto.PI_BOOKINGS);
    StringToListCategoryEnumMapper stringToListCategoryEnumMapper = new StringToListCategoryEnumMapper();

    //Act
    categoryEnumsDto = stringToListCategoryEnumMapper.convert(source);

    //Assert
    Assertions.assertNotNull(categoryEnumsDto);
    assertEquals(categoryEnumsDto.toString(), sourceFixed, categoryEnumsDto.get(0).toString());
  }

  @Test
  void convert__ShouldReturnCategoryEnumBooking() throws IllegalArgumentException {
    //Arrange
    String source = "      BOOKING";
    String sourceFixed = "BOOKING";
    List<CategoryEnumDto> categoryEnumsDto = new ArrayList<>();
    categoryEnumsDto.add(CategoryEnumDto.BOOKING);
    StringToListCategoryEnumMapper stringToListCategoryEnumMapper = new StringToListCategoryEnumMapper();

    //Act
    categoryEnumsDto = stringToListCategoryEnumMapper.convert(source);

    //Assert
    Assertions.assertNotNull(categoryEnumsDto);
    assertEquals(categoryEnumsDto.toString(), sourceFixed, categoryEnumsDto.get(0).toString());
  }

  @Test
  void convert__ShouldThrowException() throws IllegalArgumentException {
    //Arrange
    String source = "      -BOOKING";
    StringToListCategoryEnumMapper stringToListCategoryEnumMapper = new StringToListCategoryEnumMapper();

    //Act_Assert
    Assertions.assertThrows(LabelsBadRequestException.class, () -> stringToListCategoryEnumMapper.convert(source));
  }

  /**
   * Test method to verify the conversion of a source string to a list of CategoryEnumDto
   * It checks if the source string containing spaces is correctly converted to a fixed format and mapped to a CategoryEnumDto.
   * The expected behavior is that the source string "      PI-PRE-CHECKIN" is converted to "PI_PRE_CHECKIN" and mapped to a CategoryEnumDto.PI_PRE_CHECKIN.
   * @throws IllegalArgumentException if the conversion fails
   */
  @Test
  void convert__ShouldReturnCategoryEnumPiPreCheckIn() throws IllegalArgumentException {
    //Arrange
    String source = "      PI-PRE-CHECKIN";
    String sourceFixed = "PI_PRE_CHECKIN";
    List<CategoryEnumDto> categoryEnumsDto = new ArrayList<>();
    categoryEnumsDto.add(CategoryEnumDto.PI_PRE_CHECKIN);
    StringToListCategoryEnumMapper stringToListCategoryEnumMapper = new StringToListCategoryEnumMapper();

    //Act
    categoryEnumsDto = stringToListCategoryEnumMapper.convert(source);

    //Assert
    Assertions.assertNotNull(categoryEnumsDto);
    assertEquals(categoryEnumsDto.toString(), sourceFixed, categoryEnumsDto.get(0).toString());
  }

  @Test
  void convert__ShouldReturnCategoryEnumExtras() throws IllegalArgumentException {
    //Arrange
    String source = "      extras";
    String sourceFixed = "EXTRAS";
    List<CategoryEnumDto> categoryEnumsDto = new ArrayList<>();
    categoryEnumsDto.add(CategoryEnumDto.EXTRAS);
    StringToListCategoryEnumMapper stringToListCategoryEnumMapper = new StringToListCategoryEnumMapper();

    //Act
    categoryEnumsDto = stringToListCategoryEnumMapper.convert(source);

    //Assert
    Assertions.assertNotNull(categoryEnumsDto);
    assertEquals(categoryEnumsDto.toString(), sourceFixed, categoryEnumsDto.get(0).toString());
  }
}
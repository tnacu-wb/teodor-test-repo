package uk.co.whitbread.infrastructure.rest.controller.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.infrastructure.rest.controller.srp.model.in.OldWorldChannelEnumDto;
import uk.co.whitbread.infrastructure.rest.controller.srp.model.in.HotelAvailabilitiesRequestDto;
import uk.co.whitbread.infrastructure.rest.controller.srp.model.in.LocationFormatEnumDto;
import uk.co.whitbread.infrastructure.rest.controller.srp.model.in.RadiusUnitEnumDto;

@ExtendWith(MockitoExtension.class)
class RequestDtoFormatValidatorTest {

  private final RequestDtoFormatValidator requestValidator = new RequestDtoFormatValidator();
  private HotelAvailabilitiesRequestDto requestDto;

  @Mock
  ConstraintValidatorContext constraintValidatorContext;

  @BeforeEach
  public void setUp() {
    requestDto = new HotelAvailabilitiesRequestDto("ChIJdd4hrwug2EcRmSrV3Vo6llI",
        LocationFormatEnumDto.PLACEID, 50, RadiusUnitEnumDto.MILES,
        (LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))),
        (LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))),
        "en", "gb", List.of(1), List.of(0), List.of("DB"),
        OldWorldChannelEnumDto.WEB, "PI","WEB",null,
        1, 10, 5, "DISTANCE", 1f, 1f,
        Arrays.asList(""), Arrays.asList("EMP01"),
            0.85f
    );
  }

  @Test
  void idValid__ShouldReturnOK() {
    //Arrange
    //Act
    boolean valid = requestValidator.isValid(requestDto, null);

    //Assert
    assertThat(valid, is(true));
  }

  @Test
  void idValid_pastArrivalDate_BadRequest() {
    //Arrange
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(
        Mockito.mock(ConstraintViolationBuilder.class));
    requestDto.setArrivalDate(
        (LocalDate.now().minusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
    //Act
    boolean valid = requestValidator.isValid(requestDto, constraintValidatorContext);

    //Assert
    assertThat(valid, is(false));
  }

  @Test
  void idValid_arrivalDateAfterDepartureDate_BadRequest() {
    //Arrange
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(
        Mockito.mock(ConstraintViolationBuilder.class));
    requestDto.setArrivalDate(
        (LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
    requestDto.setDepartureDate(
        (LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
    //Act
    boolean valid = requestValidator.isValid(requestDto, constraintValidatorContext);

    //Assert
    assertThat(valid, is(false));
  }

  @Test
  void idValid_InvalidDateFormat() {
    //Arrange
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(
        Mockito.mock(ConstraintViolationBuilder.class));
    requestDto.setArrivalDate("x");

    //Act
    boolean valid = requestValidator.isValid(requestDto, constraintValidatorContext);

    //Assert
    assertThat(valid, is(false));
  }

  @Test
  void idValid_invalidValueForAdults_BadRequest() {
    //Arrange
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(
        Mockito.mock(ConstraintViolationBuilder.class));
    requestDto.setAdultsNumber(List.of(-1));
    //Act
    boolean valid = requestValidator.isValid(requestDto, constraintValidatorContext);

    //Assert
    assertThat(valid, is(false));
  }

  @Test
  void idValid_invalidValueForChildren_BadRequest() {
    //Arrange
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(
        Mockito.mock(ConstraintViolationBuilder.class));
    requestDto.setChildrenNumber(List.of(-1));
    //Act
    boolean valid = requestValidator.isValid(requestDto, constraintValidatorContext);

    //Assert
    assertThat(valid, is(false));
  }
  @Test
  void idValid_invalidValueForRoomType_BadRequest() {
    //Arrange
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(
            Mockito.mock(ConstraintViolationBuilder.class));
    requestDto.setRoomTypes(List.of(""));
    //Act
    boolean valid = requestValidator.isValid(requestDto, constraintValidatorContext);

    //Assert
    assertThat(valid, is(false));
  }

  @Test
  void idValid_missingValueForRoomType_Success() {
    requestDto.setRoomTypes(null);
    //Act
    boolean valid = requestValidator.isValid(requestDto, constraintValidatorContext);

    //Assert
    assertThat(valid, is(true));
  }

}
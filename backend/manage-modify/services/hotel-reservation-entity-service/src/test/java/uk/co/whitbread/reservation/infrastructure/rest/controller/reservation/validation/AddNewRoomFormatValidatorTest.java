package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.reservation.ErrorCode.DIGITAL_INVALID_CHANNEL_EXCEPTION;
import static uk.co.whitbread.reservation.ErrorCode.DIGITAL_INVALID_CHANNEL_SUBCHANNEL_EXCEPTION;
import static uk.co.whitbread.reservation.ErrorCode.DIGITAL_INVALID_EMAIL_EXCEPTION;
import static uk.co.whitbread.reservation.ErrorCode.DIGITAL_INVALID_INFO_LEAD_GUEST_EXCEPTION;
import static uk.co.whitbread.reservation.ErrorCode.DIGITAL_INVALID_LEAD_GUEST_EXCEPTION;
import static uk.co.whitbread.reservation.ErrorCode.DIGITAL_INVALID_ROOM_EXCEPTION;
import static uk.co.whitbread.reservation.ErrorCode.DIGITAL_INVALID_TEMPBOOKING_EXCEPTION;
import static uk.co.whitbread.reservation.ErrorCode.DIGITAL_INVALID_OCCUPANCY_EXCEPTION;
import static uk.co.whitbread.reservation.ErrorCode.DIGITAL_INVALID_ROOMTYPE_EXCEPTION;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.AddNewRoomRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.BookingChannelDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.LeadGuestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.RoomOccupancyDto;

@ExtendWith(MockitoExtension.class)
class AddNewRoomFormatValidatorTest {

  @Mock
  ConstraintValidatorContext constraintValidatorContext;

  private AddNewRoomFormatValidator validator = new AddNewRoomFormatValidator();
  private AddNewRoomRequestDto addNewRoomRequestDto;

  @BeforeEach
  public void setUp() {
    var roomOccupancy = new RoomOccupancyDto();
    roomOccupancy.setAdultsNumber(1);
    roomOccupancy.setChildrenNumber(0);
    roomOccupancy.setCotRequired(false);
    var leadGuest = new LeadGuestDto();
    leadGuest.setTitle("TestTitle");
    leadGuest.setFirstName("TestFirstName");
    leadGuest.setLastName("TestLastName");
    leadGuest.setEmailAddress("test@test.tst");
    var bookingChannel = new BookingChannelDto();
    bookingChannel.setChannel("TestChannel");
    bookingChannel.setSubchannel("TestSubChannel");
    bookingChannel.setLanguage("TestLanguage");
    addNewRoomRequestDto = new AddNewRoomRequestDto();
    addNewRoomRequestDto.setTempBookingRef("tempBookingBasket");
    addNewRoomRequestDto.setRoomType("DB");
    addNewRoomRequestDto.setRoomOccupancy(roomOccupancy);
    addNewRoomRequestDto.setBookingChannel(bookingChannel);
    addNewRoomRequestDto.setLeadGuest(leadGuest);
  }

  @Test
  void isValid__ShouldReturnOK() {
    //Act
    boolean valid = validator.isValid(addNewRoomRequestDto, null);
    //Assert
    assertThat(valid, is(true));
  }

  @Test
  void isValid_NullLeadGuest_BadRequest() {
    //Arrange
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(
        Mockito.mock(ConstraintViolationBuilder.class));
    addNewRoomRequestDto.setLeadGuest(null);
    //Assert
    var ex = assertThrows(BadRequestValidationException.class, () ->
        validator.isValid(addNewRoomRequestDto, constraintValidatorContext));
    assertEquals(DIGITAL_INVALID_LEAD_GUEST_EXCEPTION.getCode(), ex.getErrorCode());
  }

  @Test
  void isValid_NullGuestTitle_BadRequest() {
    //Arrange
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(
        Mockito.mock(ConstraintViolationBuilder.class));
    addNewRoomRequestDto.getLeadGuest().setTitle(null);
    //Assert
    var ex = assertThrows(BadRequestValidationException.class, () ->
        validator.isValid(addNewRoomRequestDto, constraintValidatorContext));
    assertEquals(DIGITAL_INVALID_INFO_LEAD_GUEST_EXCEPTION.getCode(), ex.getErrorCode());
  }

  @Test
  void isValid_NullFirstName_BadRequest() {
    //Arrange
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(
        Mockito.mock(ConstraintViolationBuilder.class));
    addNewRoomRequestDto.getLeadGuest().setFirstName(null);
    //Assert
    var ex = assertThrows(BadRequestValidationException.class, () ->
        validator.isValid(addNewRoomRequestDto, constraintValidatorContext));
    assertEquals(DIGITAL_INVALID_INFO_LEAD_GUEST_EXCEPTION.getCode(), ex.getErrorCode());
  }

  @Test
  void isValid_NullLastName_BadRequest() {
    //Arrange
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(
        Mockito.mock(ConstraintViolationBuilder.class));
    addNewRoomRequestDto.getLeadGuest().setLastName(null);
    //Assert
    var ex = assertThrows(BadRequestValidationException.class, () ->
        validator.isValid(addNewRoomRequestDto, constraintValidatorContext));
    assertEquals(DIGITAL_INVALID_INFO_LEAD_GUEST_EXCEPTION.getCode(), ex.getErrorCode());
  }

  @ParameterizedTest
  @ValueSource(strings = {"abd", "abd@", "abd@company.", "abd@company.a", "@company.abd", "abd@.scotland"})
  void isValid_BadEmail_BadRequest(String badEmail) {
    //Arrange
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(
        Mockito.mock(ConstraintViolationBuilder.class));
    addNewRoomRequestDto.getLeadGuest().setEmailAddress(badEmail);
    //Assert
    var ex = assertThrows(BadRequestValidationException.class, () ->
        validator.isValid(addNewRoomRequestDto, constraintValidatorContext));
    assertEquals(DIGITAL_INVALID_EMAIL_EXCEPTION.getCode(), ex.getErrorCode());
  }

  @ParameterizedTest
  @ValueSource(strings = {"abd@asd.asd", "abd@company.scotland", "name.lastname@company.wales"})
  void isValid_GoodEmail(String email) {
    //Arrange
    addNewRoomRequestDto.getLeadGuest().setEmailAddress(email);
    //Assert
    assertEquals(Boolean.TRUE, validator.isValid(addNewRoomRequestDto, constraintValidatorContext));
  }

  @Test
  void isValid_Room_BadRequest() {
    //Arrange
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(
        Mockito.mock(ConstraintViolationBuilder.class));
    addNewRoomRequestDto.setRoomOccupancy(null);
    //Assert
    var ex = assertThrows(BadRequestValidationException.class, () ->
        validator.isValid(addNewRoomRequestDto, constraintValidatorContext));
    assertEquals(DIGITAL_INVALID_ROOM_EXCEPTION.getCode(), ex.getErrorCode());
  }

  @Test
  void isValid_BookingChannel_BadRequest() {
    //Arrange
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(
        Mockito.mock(ConstraintViolationBuilder.class));
    addNewRoomRequestDto.setBookingChannel(null);
    //Assert
    var ex = assertThrows(BadRequestValidationException.class, () ->
        validator.isValid(addNewRoomRequestDto, constraintValidatorContext));
    assertEquals(DIGITAL_INVALID_CHANNEL_EXCEPTION.getCode(), ex.getErrorCode());
  }

  @Test
  void isValid_BookingSubChannel_BadRequest() {
    //Arrange
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(
        Mockito.mock(ConstraintViolationBuilder.class));
    addNewRoomRequestDto.setBookingChannel(new BookingChannelDto());
    //Assert
    var ex = assertThrows(BadRequestValidationException.class, () ->
        validator.isValid(addNewRoomRequestDto, constraintValidatorContext));
    assertEquals(DIGITAL_INVALID_CHANNEL_SUBCHANNEL_EXCEPTION.getCode(), ex.getErrorCode());
  }

  @Test
  void isValid_TempBookingRef_BadRequest() {
    //Arrange
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(
        Mockito.mock(ConstraintViolationBuilder.class));
    addNewRoomRequestDto.setTempBookingRef(null);
    //Assert
    var ex = assertThrows(BadRequestValidationException.class, () ->
        validator.isValid(addNewRoomRequestDto, constraintValidatorContext));
    assertEquals(DIGITAL_INVALID_TEMPBOOKING_EXCEPTION.getCode(), ex.getErrorCode());
  }

  @Test
  void isValid_RoomOccupancy_BadRequest() {
    //Arrange
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(
        Mockito.mock(ConstraintViolationBuilder.class));
    addNewRoomRequestDto.setRoomOccupancy(RoomOccupancyDto.builder().build());
    //Assert
    var ex = assertThrows(BadRequestValidationException.class, () ->
        validator.isValid(addNewRoomRequestDto, constraintValidatorContext));
    assertEquals(DIGITAL_INVALID_OCCUPANCY_EXCEPTION.getCode(), ex.getErrorCode());
  }

  @Test
  void isValid_RoomType_BadRequest() {
    //Arrange
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(
        Mockito.mock(ConstraintViolationBuilder.class));
    addNewRoomRequestDto.setRoomType(null);
    //Assert
    var ex = assertThrows(BadRequestValidationException.class, () ->
        validator.isValid(addNewRoomRequestDto, constraintValidatorContext));
    assertEquals(DIGITAL_INVALID_ROOMTYPE_EXCEPTION.getCode(), ex.getErrorCode());
  }
}
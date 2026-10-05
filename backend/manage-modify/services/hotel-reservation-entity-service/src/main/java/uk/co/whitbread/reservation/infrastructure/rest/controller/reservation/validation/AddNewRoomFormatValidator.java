package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.reservation.ErrorCode;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.AddNewRoomRequestDto;

@Slf4j
public class AddNewRoomFormatValidator implements
    ConstraintValidator<AddNewRoomFormat, AddNewRoomRequestDto> {

  @Override
  public boolean isValid(AddNewRoomRequestDto addNewRoomRequestDto,
      ConstraintValidatorContext context) {
    if (StringUtils.isBlank(addNewRoomRequestDto.getTempBookingRef())) {
      context.buildConstraintViolationWithTemplate("tempBookingRef is mandatory")
          .addConstraintViolation();
      var ex = new BadRequestValidationException(ErrorCode.DIGITAL_INVALID_TEMPBOOKING_EXCEPTION,
          "tempBookingRef is mandatory");
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    if (addNewRoomRequestDto.getRoomOccupancy() == null) {
      context.buildConstraintViolationWithTemplate("room occupancy is mandatory")
          .addConstraintViolation();
      var ex = new BadRequestValidationException(ErrorCode.DIGITAL_INVALID_ROOM_EXCEPTION,
          "room occupancy is mandatory");
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    if (addNewRoomRequestDto.getRoomOccupancy().getAdultsNumber() == null
        || addNewRoomRequestDto.getRoomOccupancy().getChildrenNumber() == null) {
      context.buildConstraintViolationWithTemplate(
              "Adults number and children number are mandatory")
          .addConstraintViolation();
      var ex = new BadRequestValidationException(ErrorCode.DIGITAL_INVALID_OCCUPANCY_EXCEPTION,
          "Adults number and children number are mandatory");
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    if (addNewRoomRequestDto.getLeadGuest() == null) {
      context.buildConstraintViolationWithTemplate("lead guest is mandatory")
          .addConstraintViolation();
      var ex = new BadRequestValidationException(ErrorCode.DIGITAL_INVALID_LEAD_GUEST_EXCEPTION,
          "lead guest is mandatory");
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    if (StringUtils.isBlank(addNewRoomRequestDto.getLeadGuest().getTitle())
        || StringUtils.isBlank(addNewRoomRequestDto.getLeadGuest().getFirstName())
        || StringUtils.isBlank(addNewRoomRequestDto.getLeadGuest().getLastName())) {
      context.buildConstraintViolationWithTemplate(
              "Lead guest Title, FirstName and LastName are mandatory")
          .addConstraintViolation();
      var ex = new BadRequestValidationException(
          ErrorCode.DIGITAL_INVALID_INFO_LEAD_GUEST_EXCEPTION,
          "Lead guest Title, FirstName and LastName are mandatory");
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    var regexEmailPattern = "^[\\w-\\.]+@{1}[\\w-+\\.]{1,100}\\.+[\\w-]{2,100}$";
    if (!StringUtils.isBlank(addNewRoomRequestDto.getLeadGuest().getEmailAddress())
        && !Pattern.compile(regexEmailPattern).matcher(addNewRoomRequestDto
        .getLeadGuest().getEmailAddress()).matches()) {
      context.buildConstraintViolationWithTemplate("invalid email format")
          .addConstraintViolation();
      var ex = new BadRequestValidationException(ErrorCode.DIGITAL_INVALID_EMAIL_EXCEPTION,
          "invalid email format");
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    if (StringUtils.isBlank(addNewRoomRequestDto.getRoomType())) {
      context.buildConstraintViolationWithTemplate("roomType is mandatory")
          .addConstraintViolation();
      var ex = new BadRequestValidationException(ErrorCode.DIGITAL_INVALID_ROOMTYPE_EXCEPTION,
          "roomType is mandatory");
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    if (addNewRoomRequestDto.getBookingChannel() == null) {
      context.buildConstraintViolationWithTemplate("booking channel is mandatory")
          .addConstraintViolation();
      var ex = new BadRequestValidationException(ErrorCode.DIGITAL_INVALID_CHANNEL_EXCEPTION,
          "booking channel is mandatory");
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    if (StringUtils.isBlank(addNewRoomRequestDto.getBookingChannel().getChannel())
        || StringUtils.isBlank(addNewRoomRequestDto.getBookingChannel().getSubchannel())) {
      context.buildConstraintViolationWithTemplate("channel and subchannel are mandatory")
          .addConstraintViolation();
      var ex = new BadRequestValidationException(
          ErrorCode.DIGITAL_INVALID_CHANNEL_SUBCHANNEL_EXCEPTION,
          "channel and subchannel are mandatory");
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    return true;
  }

}

package uk.co.whitbread.infrastructure.rest.controller.validation;

import static java.util.Objects.nonNull;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.infrastructure.rest.controller.srp.model.in.HotelAvailabilitiesRequestDto;

@Slf4j
public class RequestDtoFormatValidator implements
    ConstraintValidator<RequestDtoFormat, HotelAvailabilitiesRequestDto> {

  @Override
  public boolean isValid(HotelAvailabilitiesRequestDto requestDto,
      ConstraintValidatorContext context) {

    String datePattern = "yyyy-MM-dd";
    try {
      if (nonNull(requestDto.getArrivalDate())) {
        if (!LocalDate.parse(requestDto.getArrivalDate(), DateTimeFormatter.ofPattern(datePattern))
                .isAfter(LocalDate.now().minusDays(1))) {
          context.buildConstraintViolationWithTemplate("arrival date may not be earlier than today")
                  .addConstraintViolation();
          return false;
        }
        if (!LocalDate.parse(requestDto.getArrivalDate(), DateTimeFormatter.ofPattern(datePattern))
                .isBefore(LocalDate.parse(requestDto.getDepartureDate(),
                        DateTimeFormatter.ofPattern(datePattern)))) {
          context.buildConstraintViolationWithTemplate(
                          "arrival date may not be on or after departure date and earlier than today")
                  .addConstraintViolation();
          return false;
        }
      }
    } catch (DateTimeParseException ex) {
      log.error("Error while trying to validate date.", ex);
      context.buildConstraintViolationWithTemplate("invalid date format").addConstraintViolation();
      return false;
    }

    if (!validateAdults(requestDto, context)) {
      return false;
    }
    if (!validateChildren(requestDto, context)) {
      return false;
    }
    return validateRoomTypes(requestDto, context);
  }

  private boolean validateAdults(HotelAvailabilitiesRequestDto requestDto,
      ConstraintValidatorContext context) {
    for (Integer adult : requestDto.getAdultsNumber()) {
      if (adult <= 0 || adult == null) {
        context.buildConstraintViolationWithTemplate(
                "a positive number of adults must be specified for each room")
            .addConstraintViolation();
        return false;
      }
    }
    return true;
  }

  private boolean validateChildren(HotelAvailabilitiesRequestDto requestDto,
      ConstraintValidatorContext context) {
    for (Integer child : requestDto.getChildrenNumber()) {
      if (child < 0 || child == null) {
        context.buildConstraintViolationWithTemplate(
                "a positive or zero number of children must be specified for each room")
            .addConstraintViolation();
        return false;
      }
    }
    return true;
  }

  private boolean validateRoomTypes(HotelAvailabilitiesRequestDto requestDto,
      ConstraintValidatorContext context) {
    if (requestDto.getRoomTypes() != null && !requestDto.getRoomTypes().isEmpty()) {
      for (String roomType : requestDto.getRoomTypes()) {
        if (StringUtils.isBlank(roomType)) {
          context.buildConstraintViolationWithTemplate("invalid room type")
                  .addConstraintViolation();
          return false;
        }
      }
    }
    return true;
  }
}

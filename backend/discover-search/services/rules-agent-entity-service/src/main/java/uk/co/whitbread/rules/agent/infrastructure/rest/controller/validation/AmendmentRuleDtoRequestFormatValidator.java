package uk.co.whitbread.rules.agent.infrastructure.rest.controller.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.AmendmentRuleRequestDto;

@Slf4j
public class AmendmentRuleDtoRequestFormatValidator implements
    ConstraintValidator<AmendmentRuleDtoRequestFormat, AmendmentRuleRequestDto> {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
      "yyyyMMdd'T'HHmmss");

  @Override
  public boolean isValid(AmendmentRuleRequestDto requestDto, ConstraintValidatorContext context) {

    try {
      LocalDate arrivalDate = LocalDate.parse(requestDto.getArrivalDate(), DATE_FORMATTER);
      LocalDateTime hotelLocalDateTime = LocalDateTime.parse(requestDto.getHotelLocalDateTime(),
          DATE_TIME_FORMATTER);
      var hotelLocalDate = hotelLocalDateTime.toLocalDate();

      if (hotelLocalDate.isAfter(arrivalDate)) {
        context.buildConstraintViolationWithTemplate("Arrival date can not be in the past")
            .addConstraintViolation();
        return false;
      }
    } catch (DateTimeParseException ex) {
      log.error("Error while trying to validate date format.", ex);
      context.buildConstraintViolationWithTemplate("Invalid date time format")
          .addConstraintViolation();
      return false;
    }

    return true;
  }
}

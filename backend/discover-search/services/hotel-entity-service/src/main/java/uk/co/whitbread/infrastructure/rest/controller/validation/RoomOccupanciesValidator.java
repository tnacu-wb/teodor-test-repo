package uk.co.whitbread.infrastructure.rest.controller.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import uk.co.whitbread.infrastructure.rest.controller.srp.model.in.HotelAvailabilitiesRequestDto;

public class RoomOccupanciesValidator implements
    ConstraintValidator<RoomOccupanciesConstraint, HotelAvailabilitiesRequestDto> {

  @Override
  public boolean isValid(HotelAvailabilitiesRequestDto request,
      ConstraintValidatorContext context) {
    return Stream.of(request.getAdultsNumber(), request.getChildrenNumber())
        .map(List::size)
        .collect(Collectors.toSet())
        .size() == 1;
  }
}

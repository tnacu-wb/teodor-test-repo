package uk.co.whitbread.infrastructure.rest.controller.groupbooking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.infrastructure.rest.controller.groupbooking.model.in.GroupBookingRequestDto;


@Slf4j
public class MealPackagesValidator implements ConstraintValidator<MealPackagesConstraint, GroupBookingRequestDto> {

  public static final String GERMAN_HOTEL = "PID";

  @Override
  public void initialize(MealPackagesConstraint constraint) {
    // Initialization not required.
  }

  @Override
  public boolean isValid(GroupBookingRequestDto request, ConstraintValidatorContext context) {
    if (GERMAN_HOTEL.equalsIgnoreCase(request.getHotelBrand())) {
      return !(request.getIsPackageTypeBf() && request.getIsPackageTypeMealDeal());
    }
    return !Objects.equals(request.getIsPackageTypeBf(), request.getIsPackageTypeMealDeal());
  }
}
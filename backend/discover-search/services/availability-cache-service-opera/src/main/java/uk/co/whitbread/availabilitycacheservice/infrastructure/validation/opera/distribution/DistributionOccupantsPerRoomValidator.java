package uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.distribution;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution.DistributionSearchCriteria;

@Slf4j
public class DistributionOccupantsPerRoomValidator implements
    ConstraintValidator<DistributionOccupantsPerRoomConstraint, DistributionSearchCriteria> {

  @Override
  public void initialize(DistributionOccupantsPerRoomConstraint constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(DistributionSearchCriteria searchCriteria,
      ConstraintValidatorContext constraintValidatorContext) {
    log.debug("Executing \'Room Occupants Validator\' for :{}", searchCriteria);
    return (!isNull(searchCriteria) && isValidAdultsChildLength(searchCriteria));
  }

  private boolean isNull(final DistributionSearchCriteria searchCriteria) {
    log.trace("Validating null inputs for adults, children & room-type fields under search criteria....");
    return (searchCriteria == null || searchCriteria.getAdults() == null || searchCriteria.getChildren() == null);
  }

  private boolean isValidAdultsChildLength(final DistributionSearchCriteria searchCriteria) {
    final int length = searchCriteria.getRooms();
    return (searchCriteria.getAdults().length == length && searchCriteria.getChildren().length == length);
  }

}

package uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.OperaSearchCriteria;

@Slf4j
public class OperaOccupantsPerRoomValidator implements
    ConstraintValidator<OperaOccupantsPerRoomConstraint, OperaSearchCriteria> {

  @Override
  public void initialize(OperaOccupantsPerRoomConstraint constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(OperaSearchCriteria searchCriteria,
                         ConstraintValidatorContext constraintValidatorContext) {
    log.debug("Executing \'Room Occupants Validator\' for :{}", searchCriteria);
    return (!isNull(searchCriteria) && isValidAdultsChildLength(searchCriteria));
  }

  private boolean isNull(final OperaSearchCriteria searchCriteria) {
    log.trace("Validating null inputs for adults, children & room-type fields under search criteria....");
    return (searchCriteria == null || searchCriteria.getAdults() == null || searchCriteria.getChildren() == null);
  }

  private boolean isValidAdultsChildLength(final OperaSearchCriteria searchCriteria) {
    final int length = searchCriteria.getRooms();
    return (searchCriteria.getAdults().length == length && searchCriteria.getChildren().length == length);
  }

}

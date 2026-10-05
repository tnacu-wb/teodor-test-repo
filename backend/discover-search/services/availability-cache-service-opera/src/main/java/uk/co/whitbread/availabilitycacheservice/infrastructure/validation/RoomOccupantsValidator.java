package uk.co.whitbread.availabilitycacheservice.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.RoomType;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

@Slf4j
public class RoomOccupantsValidator implements ConstraintValidator<RoomOccupantsConstraint, SearchCriteria> {

  @Override
  public void initialize(final RoomOccupantsConstraint constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(final SearchCriteria searchCriteria,
      final ConstraintValidatorContext constraintValidatorContext) {
    log.debug("Executing \'Room Occupants Validator\' for :{}", searchCriteria);
    return (!isNull(searchCriteria) && isValidLength(searchCriteria) && isValidRoomType(searchCriteria));
  }

  private boolean isNull(final SearchCriteria searchCriteria) {
    log.trace("Validating null inputs for adults, children & room-type fields under search criteria....");
    return (searchCriteria == null || searchCriteria.getAdults() == null || searchCriteria.getChildren() == null
        || searchCriteria.getType() == null);
  }

  private boolean isValidLength(final SearchCriteria searchCriteria) {
    log.trace("Validating input array size for adults, children & room-types....");
    final int length = searchCriteria.getRooms();
    return (searchCriteria.getAdults().length == length && searchCriteria.getChildren().length == length
        && searchCriteria.getType().length == length);
  }

  private boolean isValidRoomType(final SearchCriteria searchCriteria) {
    log.trace("Validating input room types....");
    boolean isValid = false;
    final String[] roomTypes = searchCriteria.getType();
    for (final String roomType : roomTypes) {
      isValid = Arrays.stream(RoomType.values()).anyMatch(rtEnum -> rtEnum.name().equalsIgnoreCase(roomType));
      if (!isValid) {
        log.error("Room type : {} is not supported", roomType);
        break;
      }
    }
    return isValid;
  }
}

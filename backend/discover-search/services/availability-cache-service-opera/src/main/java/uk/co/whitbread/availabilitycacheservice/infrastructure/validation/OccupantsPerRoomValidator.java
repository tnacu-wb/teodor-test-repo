package uk.co.whitbread.availabilitycacheservice.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Range;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.RoomType;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

@Slf4j
public class OccupantsPerRoomValidator implements ConstraintValidator<OccupantsPerRoomConstraint, SearchCriteria> {

  @Override
  public void initialize(OccupantsPerRoomConstraint constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(SearchCriteria searchCriteria, ConstraintValidatorContext constraintValidatorContext) {
    log.debug("Executing \'Room Occupants Validator\' for :{}", searchCriteria);
    final int length = searchCriteria.getRooms();
    if (searchCriteria.getAdults().length == length && searchCriteria.getChildren().length == length
        && searchCriteria.getType().length == length) {
      return (isValidNumOfOccupants(searchCriteria));
    }
    return false;
  }

  private boolean isValidNumOfOccupants(final SearchCriteria searchCriteria) {
    final String[] roomTypes = searchCriteria.getType();
    int roomTypeCounter = 0;
    boolean isValidNumOfOccupants = false;
    for (final String roomType : roomTypes) {
      log.trace("Validating number for adults & children for room type : {} ....", roomType);
      switch (RoomType.valueOf(roomType)) {
        //Single room, allowed num of adults are 1 and allowed num of children are 0
        case SB:
          int numOfAdults = searchCriteria.getAdults()[roomTypeCounter];
          int numOfChildren = searchCriteria.getChildren()[roomTypeCounter];
          isValidNumOfOccupants = numOfAdults == 1 && numOfChildren == 0;
          break;
        //Single room, allowed num of adults are between 1-2 and allowed num of children are 0
        case DB:
        case DIS:
          final Range<Integer> allowedNumOfAdults = Range.between(1, 2);
          numOfAdults = searchCriteria.getAdults()[roomTypeCounter];
          numOfChildren = searchCriteria.getChildren()[roomTypeCounter];
          isValidNumOfOccupants = (allowedNumOfAdults.contains(numOfAdults) && numOfChildren == 0);
          break;
        case TWIN:
          numOfAdults = searchCriteria.getAdults()[roomTypeCounter];
          numOfChildren = searchCriteria.getChildren()[roomTypeCounter];
          isValidNumOfOccupants = numOfAdults == 2 && numOfChildren == 0;
          break;
        //family room, allowed num of adults are between 1 & 2 and allowed num of children are between 1 & 2
        case FAM:
          final Range<Integer> allowedNumOfOccupants = Range.between(1, 2);
          numOfAdults = searchCriteria.getAdults()[roomTypeCounter];
          numOfChildren = searchCriteria.getChildren()[roomTypeCounter];
          isValidNumOfOccupants = (allowedNumOfOccupants.contains(numOfAdults) && allowedNumOfOccupants.contains(
              numOfChildren));
          break;
        default:
          log.error("Invalid Room Type");
          break;
      }
      if (!isValidNumOfOccupants) {
        log.error("Invalid number of occupants (adults/children) in the chosen room : {}", roomType);
        break;
      }
      roomTypeCounter++;
    }
    return isValidNumOfOccupants;
  }
}

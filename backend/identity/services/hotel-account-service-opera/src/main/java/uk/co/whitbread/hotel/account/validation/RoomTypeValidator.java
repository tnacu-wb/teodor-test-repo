package uk.co.whitbread.hotel.account.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.co.whitbread.hotel.account.model.RoomCriteria;

import java.util.Arrays;
import java.util.List;

public class RoomTypeValidator implements ConstraintValidator<ValidRoomType, RoomCriteria> {

  @Override
  public boolean isValid(RoomCriteria roomCriteria, ConstraintValidatorContext context) {
    if (roomCriteria == null) {
      return true;
    }
    if (roomCriteria.getAdults() == null || roomCriteria.getChildren() == null || roomCriteria.getType() == null) {
      return false; // Ensure all required fields are present
    }
    Long adults = roomCriteria.getAdults();
    Long children = roomCriteria.getChildren();
    String type = roomCriteria.getType();

    List<String> validTypes;
    if (children > 0) {
      validTypes = List.of("FAM");
    } else {
      validTypes = switch (adults.intValue()) {
        case 1 -> Arrays.asList("SB", "DB", "DIS");
        case 2 -> Arrays.asList("DB", "TWIN", "DIS");
        default -> List.of();
      };
    }

    return validTypes.contains(type);
  }
}
package uk.co.whitbread.infrastructure.rest.controller.groupbooking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.infrastructure.rest.controller.groupbooking.model.in.GroupBookingRequestDto;


@Slf4j
public class RoomNumberValidator implements ConstraintValidator<RoomNumberConstraint, GroupBookingRequestDto> {

  public static final int MIN_ROOM_COUNT = 10;

  @Override
  public void initialize(RoomNumberConstraint constraint) {
    // Initialization not required.
  }

  @Override
  public boolean isValid(GroupBookingRequestDto request, ConstraintValidatorContext context) {

    // Retrieve values from the object with null checks
    Integer singleOccupancy = getPropertyValue(request.getSingleOccupancy());
    Integer doubleOccupancy = getPropertyValue(request.getDoubleOccupancy());
    Integer twinRooms = getPropertyValue(request.getTwinRooms());
    Integer familyOf21A1C = getPropertyValue(request.getFamilyOf21A1C());
    Integer familyOf32A1C = getPropertyValue(request.getFamilyOf32A1C());
    Integer familyOf31A2C = getPropertyValue(request.getFamilyOf31A2C());
    Integer familyOf42A2C = getPropertyValue(request.getFamilyOf42A2C());
    Integer accessibleSingle = getPropertyValue(request.getAccessibleSingle());
    Integer accessibleDouble = getPropertyValue(request.getAccessibleDouble());
    Integer accessibleTwin = getPropertyValue(request.getAccessibleTwin());

    return
        (singleOccupancy + doubleOccupancy + twinRooms + familyOf21A1C + familyOf32A1C + familyOf31A2C + familyOf42A2C
            + accessibleSingle + accessibleDouble + accessibleTwin) >= MIN_ROOM_COUNT;
  }

  private Integer getPropertyValue(Integer value) {
    return value == null ? 0 : value;
  }
}
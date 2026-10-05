package uk.co.whitbread.availabilitycacheservice.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapperImpl;
import uk.co.whitbread.availabilitycacheservice.infrastructure.properties.HotelPriceProperties;

@Slf4j
@RequiredArgsConstructor
public class RoomTypeValidator implements ConstraintValidator<RoomTypeConstraint, Object> {

  private final HotelPriceProperties hotelPriceProperties;
  private String roomType;

  private boolean validate(String roomType) {

    final List<String> validRoomTypes = hotelPriceProperties.getRoomTypes();
    log.debug("validRoomTypes: {}", validRoomTypes);

    return validRoomTypes
        .stream()
        .anyMatch(rt -> rt.equalsIgnoreCase(roomType));
  }

  @Override
  public void initialize(final RoomTypeConstraint constraintAnnotation) {
    this.roomType = constraintAnnotation.roomType();
  }

  @Override
  public boolean isValid(Object searchCriteria,
      ConstraintValidatorContext constraintValidatorContext) {

    final String rt = Objects.toString(new BeanWrapperImpl(searchCriteria).getPropertyValue(roomType));

    return validate(rt);
  }
}

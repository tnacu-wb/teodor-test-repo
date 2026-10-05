package uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera;

import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;
import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitizeRoomTypesForLog;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.OperaHotelsSearchCriteria;

@Slf4j
@Component
public class OperaRoomTypesValidator {

  public boolean isValidRoomTypes(OperaHotelsSearchCriteria operaHotelsSearchCriteria) {
    log.debug("Executing \'Room Type Validator\' with operaHotelsSearchCriteria - {}",
        sanitize(operaHotelsSearchCriteria));
    return (!isNull(operaHotelsSearchCriteria) && isValidLength(operaHotelsSearchCriteria)
        && isValidRoomType(operaHotelsSearchCriteria.getRoomTypes()));
  }

  private boolean isNull(OperaHotelsSearchCriteria operaHotelsSearchCriteria) {
    log.debug("Validating null inputs for adults, children & room-type fields under search criteria....");
    return (operaHotelsSearchCriteria == null
        || operaHotelsSearchCriteria.getRoomQty() == null
        || operaHotelsSearchCriteria.getRoomTypes() == null);
  }

  private boolean isValidLength(OperaHotelsSearchCriteria operaHotelsSearchCriteria) {
    log.debug("Validating input array size for roomQty & room-types array size....");
    final int roomQtySum = Arrays.stream(operaHotelsSearchCriteria.getRoomQty()).sum();
    return (
        operaHotelsSearchCriteria.getRoomQty().length == operaHotelsSearchCriteria.getRoomTypes().length
            && roomQtySum == operaHotelsSearchCriteria.getRooms());
  }

  private boolean isValidRoomType(final String[][] roomTypes) {
    log.debug("validating roomTypes to contain only alphabets - {}", sanitizeRoomTypesForLog(roomTypes));
    final Set<String> roomTypesSet = Arrays.stream(roomTypes)
        .flatMap(Arrays::stream).collect(Collectors.toSet());
    return roomTypesSet.stream().noneMatch(roomType -> !roomType.matches("[a-zA-Z]+"));
  }
}

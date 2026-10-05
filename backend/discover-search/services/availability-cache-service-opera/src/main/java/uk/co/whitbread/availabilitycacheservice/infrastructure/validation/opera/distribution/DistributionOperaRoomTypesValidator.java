package uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.distribution;

import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitizeRoomTypesForLog;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution.DistributionPayload;


@Slf4j
@Component
public class DistributionOperaRoomTypesValidator {

  public boolean isValidRoomTypes(final DistributionPayload distributionPayload) {
    log.debug("Executing Room Type Validator with DistributionPayload - {}", distributionPayload);

    return (!isNull(distributionPayload) && isValidLength(distributionPayload)
        && isValidRoomType(distributionPayload.getRoomTypes()));
  }

  private boolean isNull(final DistributionPayload distributionPayload) {
    log.debug("Validating null inputs for adults, children & room-type fields under search criteria....");
    return (distributionPayload == null
        || distributionPayload.getRoomQty() == null || distributionPayload.getRoomTypes() == null);
  }

  private boolean isValidLength(final DistributionPayload distributionPayload) {
    log.debug("Validating input array size for roomQty & room-types array size....");
    final int roomQtySum = Arrays.stream(distributionPayload.getRoomQty()).sum();
    return (distributionPayload.getRoomQty().length == distributionPayload.getRoomTypes().length
        && roomQtySum == distributionPayload.getRooms());
  }

  private boolean isValidRoomType(final String[][] roomTypes) {
    log.debug("validating roomTypes to contain only alphabets - {}", sanitizeRoomTypesForLog(roomTypes));
    final Set<String> roomTypesSet = Arrays.stream(roomTypes)
        .flatMap(Arrays::stream).collect(Collectors.toSet());
    return roomTypesSet.stream().noneMatch(roomType -> !roomType.matches("[a-zA-Z]+"));
  }
}

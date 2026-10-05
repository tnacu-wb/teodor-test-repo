package uk.co.whitbread.domain.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.domain.exceptions.ErrorCode;
import uk.co.whitbread.domain.exceptions.RulesAgentBadRequestException;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsRequest;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityRequest;
import uk.co.whitbread.domain.model.availability.in.Room;
import uk.co.whitbread.domain.model.rulesagent.out.MaxRoomOccupancyData;
import uk.co.whitbread.domain.ports.secondary.ContentServiceOutPort;
import uk.co.whitbread.domain.ports.secondary.RulesAgentOutPort;

@Slf4j
@RequiredArgsConstructor
public class HotelAvailabilityCheckRules {

  private static final String HUB_BRAND = "HUB";
  private static final String TWIN_ROOM = "TWIN";
  private static final String FAM_ROOM = "FAM";

  private final ContentServiceOutPort contentServiceOutPort;
  private final RulesAgentOutPort rulesAgentOutPort;

  public boolean fulfillHubRules(HotelAvailabilityRequest request) {
    log.debug("Entered fulfillHubRules with rateCodeCriteria={}",
            request);
    var brand = contentServiceOutPort.getHotelBrand(request.getHotelId());
    var twinRooms =
            request.getRoomTypes().stream().filter(TWIN_ROOM::equals)
                    .collect(Collectors.toSet());
    var famRooms = request.getRoomTypes()
            .stream()
            .filter(FAM_ROOM::equals)
            .collect(Collectors.toSet());
    var childrenNumberRequest =
            CollectionUtils.isEmpty(request.getChildrenNumber()) ? new ArrayList<Integer>() :
                    request.getChildrenNumber().stream().filter(childNum -> childNum > 0)
                            .collect(Collectors.toSet());
    var cotsRequired =
            CollectionUtils.isEmpty(request.getCotsRequired()) ? new ArrayList<Boolean>() :
                    request.getCotsRequired().stream().filter(cot -> cot).collect(Collectors.toSet());

    if (HUB_BRAND.equals(brand)
            && (!twinRooms.isEmpty()
            || !famRooms.isEmpty()
            || !childrenNumberRequest.isEmpty()
            || !cotsRequired.isEmpty())) {
      return false;
    }
    return true;
  }

  public void validateRoomOccupancyRule(HotelAvailabilityRequest request) {
    this.validateRoomOccupancyRule(request.getRoomTypes(), request.getAdultsNumber(),
        request.getChildrenNumber(), request.getChannel());
  }

  public void validateRoomOccupancyRule(HotelAvailabilityByIdsRequest request) {
    this.validateRoomOccupancyRule(request.getRoomTypes(), request.getAdultsNumber(),
        request.getChildrenNumber(), request.getChannel());
  }

  public void validateRoomOccupancyRule(HotelAvailabilityByIdsV2Request request) {
    this.validateRoomOccupancyRule(request.getRooms().stream().map(Room::getTag).toList(),
        request.getRooms().stream().map(Room::getAdults).toList(),
        request.getRooms().stream().map(Room::getChildren).toList(),
        request.getBookingChannel().getChannel());
  }

  public void validateRoomOccupancyRule(List<String> roomTypes, List<Integer> adultsNumber,
      List<Integer> childrenNumber, String channelId) {
    log.trace("Validating RoomOccupancyRule");
    var maxRoomOccupancyResponse = rulesAgentOutPort.getMaxRoomOccupancyRule(channelId);

    for (int i = 0; i < roomTypes.size(); i++) {
      String roomType = roomTypes.get(i);
      int adults = adultsNumber.get(i);
      int children = childrenNumber.get(i);
      var isValid = maxRoomOccupancyResponse
          .getRoomOccupancies()
          .stream()
          .filter(maxRoomOccupancyData -> maxRoomOccupancyData.getAdultsNumber() == adults
              && maxRoomOccupancyData.getChildrenNumber() == children)
          .map(MaxRoomOccupancyData::getAcceptedRoomTypes)
          .flatMap(Collection::stream)
          .toList()
          .contains(roomType);
      if (!isValid) {
        var message = String.format("Error while trying validate Room Occupancy Rule for roomTypes=%s, "
                + "adultsNumber=%s, childrenNumber=%s", roomTypes, adultsNumber, childrenNumber);
        var exception = new RulesAgentBadRequestException(ErrorCode.DIGITAL_ROOM_OCCUPANCY_RULE_EXCEPTION,
                message);
        ExceptionLogger.log(log, exception);
        throw exception;
      }
    }
  }

}

package uk.co.whitbread.domain.logic;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Objects.nonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.domain.exceptions.ErrorCode;
import uk.co.whitbread.domain.exceptions.RulesAgentBadRequestException;
import uk.co.whitbread.domain.exceptions.SearchRulesBadRequestException;
import uk.co.whitbread.domain.model.feature.FeatureFlag;
import uk.co.whitbread.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.domain.model.rulesagent.out.MaxRoomOccupancyData;
import uk.co.whitbread.domain.model.searchrules.out.RoomOccupancy;
import uk.co.whitbread.domain.model.searchrules.out.SearchRules;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.domain.ports.secondary.ContentServiceOutPort;
import uk.co.whitbread.domain.ports.secondary.RulesAgentOutPort;

@Slf4j
@RequiredArgsConstructor
@Component
public class RulesAgentValidations {

  private final RulesAgentOutPort rulesAgentOutPort;
  private final ContentServiceOutPort contentServiceOutPort;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  public void validateBusinessRules(HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
                                    String channelId, boolean isValidationRequired) {
    if (!isValidationRequired) {
      return;
    }

    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getAemSearchRules())) {
      var searchRules = getSearchRulesByChannel(channelId);
      if (hotelAvailabilitiesRequest.getRoomTypes() == null || hotelAvailabilitiesRequest.getRoomTypes().isEmpty()) {
        var roomTypesListFromRules = retrieveRoomTypesVariants(hotelAvailabilitiesRequest, searchRules);
        hotelAvailabilitiesRequest = hotelAvailabilitiesRequest.toBuilder()
                .roomTypes(roomTypesListFromRules.get(0)).build();
      }
      validateMaxRoomsRuleAem(hotelAvailabilitiesRequest, searchRules.getMaxRooms());
      if (hasNotNullArrivalAndDepartureDate(hotelAvailabilitiesRequest)) {
        validateMaxNightsRuleAem(hotelAvailabilitiesRequest, searchRules.getMaxNights());
      }
      validateRoomOccupancyAem(hotelAvailabilitiesRequest, searchRules.getRoomOccupancies());
    } else {
      validateMaxRoomsRuleRulesAgent(hotelAvailabilitiesRequest, channelId);
      if (hasNotNullArrivalAndDepartureDate(hotelAvailabilitiesRequest)) {
        validateMaxNightsRuleRulesAgent(hotelAvailabilitiesRequest, channelId);
      }
      validateRoomOccupancyRuleRulesAgent(hotelAvailabilitiesRequest, channelId);
    }
  }

  SearchRules getSearchRulesByChannel(String channelId) {
    return contentServiceOutPort.getSearchRules(channelId, Optional.empty());
  }

  private void validateMaxRoomsRuleAem(HotelAvailabilitiesRequest hotelAvailabilitiesRequest, int maxRooms) {
    log.trace("Validating MaxRoomsRule AEM");
    if (hotelAvailabilitiesRequest.getRoomTypes().size() > maxRooms) {
      var message = String.format("Error while trying validate Max Rooms Rule AEM for hotelAvailabilityRequest=%s",
          hotelAvailabilitiesRequest);
      var exception = new SearchRulesBadRequestException(ErrorCode.DIGITAL_MAX_ROOM_RULE_EXCEPTION,
          message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private void validateMaxNightsRuleAem(HotelAvailabilitiesRequest hotelAvailabilitiesRequest, int maxNights) {
    log.trace("Validating MaxNightsRule AEM");
    final LocalDate arrivalDate = LocalDate.parse(hotelAvailabilitiesRequest.getArrivalDate(),
        DateTimeFormatter.ISO_LOCAL_DATE);
    final LocalDate departureDate = LocalDate.parse(hotelAvailabilitiesRequest.getDepartureDate(),
        DateTimeFormatter.ISO_LOCAL_DATE);
    if (DAYS.between(arrivalDate, departureDate) > maxNights) {
      var message = String.format("Error while trying validate Max Nights Rule AEM for hotelAvailabilityRequest=%s",
          hotelAvailabilitiesRequest);
      var exception = new SearchRulesBadRequestException(ErrorCode.DIGITAL_MAX_NIGHT_RULE_EXCEPTION,
          message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private void validateRoomOccupancyAem(HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      List<RoomOccupancy> roomOccupancies) {
    log.trace("Validating RoomOccupancyRule AEM");

    for (int i = 0; i < hotelAvailabilitiesRequest.getRoomTypes().size(); i++) {
      String roomType = hotelAvailabilitiesRequest.getRoomTypes().get(i);
      int adults = hotelAvailabilitiesRequest.getAdultsNumber().get(i);
      int children = hotelAvailabilitiesRequest.getChildrenNumber().get(i);
      var isValid = roomOccupancies
          .stream()
          .filter(maxRoomOccupancyData -> maxRoomOccupancyData.getAdultsNumber() == adults
              && maxRoomOccupancyData.getChildrenNumber() == children)
          .map(RoomOccupancy::getAcceptedRoomTypes)
          .flatMap(Collection::stream)
          .toList()
          .contains(roomType);
      if (!isValid) {
        var message = String.format("Error while trying validate Room Occupancy Rule AEM "
                + "for hotelAvailabilityRequest=%s", hotelAvailabilitiesRequest);
        var exception = new SearchRulesBadRequestException(ErrorCode.DIGITAL_ROOM_OCCUPANCY_RULE_2_EXCEPTION,
            message);
        ExceptionLogger.log(log, exception);
        throw exception;
      }
    }
  }

  private void validateMaxRoomsRuleRulesAgent(HotelAvailabilitiesRequest hotelAvailabilitiesRequest, String channelId) {
    log.trace("Validating MaxRoomsRule");
    var maxRoomsRuleResponse = rulesAgentOutPort.getMaxRoomsRule(channelId);
    if (hotelAvailabilitiesRequest.getRoomTypes().size() > maxRoomsRuleResponse.getMaxRooms()) {
      var message = String.format("Error while trying validate Max Rooms Rule for hotelAvailabilityRequest=%s",
          hotelAvailabilitiesRequest);
      var exception = new RulesAgentBadRequestException(ErrorCode.DIGITAL_MAX_ROOM_RULE_EXCEPTION,
          message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private void validateMaxNightsRuleRulesAgent(HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      String channelId) {
    log.trace("Validating MaxNightsRule");
    var maxNightsRuleResponse = rulesAgentOutPort.getMaxNightsRule(channelId);
    final LocalDate arrivalDate = LocalDate.parse(hotelAvailabilitiesRequest.getArrivalDate(),
            DateTimeFormatter.ISO_LOCAL_DATE);
    final LocalDate departureDate = LocalDate.parse(hotelAvailabilitiesRequest.getDepartureDate(),
            DateTimeFormatter.ISO_LOCAL_DATE);
    if (DAYS.between(arrivalDate, departureDate) > maxNightsRuleResponse.getMaxNights()) {
      var message = String.format("Error while trying validate Max Nights Rule for hotelAvailabilityRequest=%s",
              hotelAvailabilitiesRequest);
      var exception = new RulesAgentBadRequestException(ErrorCode.DIGITAL_MAX_NIGHT_RULE_EXCEPTION,
              message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private void validateRoomOccupancyRuleRulesAgent(HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
                                               String channelId) {
    log.trace("Validating RoomOccupancyRule");
    var maxRoomOccupancyResponse = rulesAgentOutPort.getMaxRoomOccupancyRule(channelId);

    for (int i = 0; i < hotelAvailabilitiesRequest.getRoomTypes().size(); i++) {
      String roomType = hotelAvailabilitiesRequest.getRoomTypes().get(i);
      int adults = hotelAvailabilitiesRequest.getAdultsNumber().get(i);
      int children = hotelAvailabilitiesRequest.getChildrenNumber().get(i);
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
        var message = String.format("Error while trying validate Room Occupancy Rule for hotelAvailabilityRequest=%s",
                hotelAvailabilitiesRequest);
        var exception = new RulesAgentBadRequestException(ErrorCode.DIGITAL_ROOM_OCCUPANCY_RULE_2_EXCEPTION,
                message);
        ExceptionLogger.log(log, exception);
        throw exception;
      }
    }
  }

  protected static boolean hasNotNullArrivalAndDepartureDate(HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    return nonNull(hotelAvailabilitiesRequest.getArrivalDate())
            && nonNull(hotelAvailabilitiesRequest.getDepartureDate());
  }

  public List<List<String>> retrieveRoomTypesVariants(HotelAvailabilitiesRequest request, SearchRules rules) {
    List<List<String>> perRoomOptions = retrieveRoomTypeListFromRules(request, rules);

    int maxVariants = perRoomOptions.stream().mapToInt(List::size).max().orElse(0);
    maxVariants = Math.max(1, maxVariants);

    List<List<String>> roomTypeVariants = new ArrayList<>(maxVariants);

    for (int variantIndex = 0; variantIndex < maxVariants; variantIndex++) {
      final int idx = variantIndex;
      List<String> variant = perRoomOptions.stream()
              .map(options -> {
                if (options.isEmpty()) {
                  return "";
                }
                return idx < options.size() ? options.get(idx) : options.get(0);
              })
              .toList();
      roomTypeVariants.add(variant);
    }
    return roomTypeVariants;
  }

  /**
   * For each room (index in req.adultsNumber) find the accepted roomTypes list from the rules,
   * filter out "DIS" and return a list of per-room option lists.
   */
  private List<List<String>> retrieveRoomTypeListFromRules(HotelAvailabilitiesRequest request, SearchRules rules) {
    int rooms = request.getAdultsNumber().size();
    return IntStream.range(0, rooms)
            .mapToObj(i -> {
              int adults = request.getAdultsNumber().get(i);
              int children = request.getChildrenNumber().get(i);
              return rules.getRoomOccupancies().stream()
                      .filter(ro -> ro.getAdultsNumber() == adults && ro.getChildrenNumber() == children)
                      .map(RoomOccupancy::getAcceptedRoomTypes)
                      .flatMap(Collection::stream)
                      .filter(rt -> !"DIS".equals(rt))
                      .toList();
            })
            .toList();
  }
}

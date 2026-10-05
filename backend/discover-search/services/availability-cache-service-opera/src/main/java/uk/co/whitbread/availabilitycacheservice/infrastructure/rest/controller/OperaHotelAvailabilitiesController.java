package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.controller;

import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;
import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitizeRoomTypesForLog;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.HotelAvailabilitiesMapper.mapHotelsToOperaHotelAvailabilitiesDto;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.HotelAvailabilitiesUtil.buildOperaHotelsSearchCriteria;

import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.OperaHotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.exceptions.HotelAvailabilitiesException;
import uk.co.whitbread.availabilitycacheservice.infrastructure.exceptions.RoomTypesException;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.OperaHotelsSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.OperaSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.OperaHotelAvailabilitiesDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.OperaRoomTypesValidator;

@Slf4j
@AllArgsConstructor
@RestController
public class OperaHotelAvailabilitiesController {

  private final OperaHotelAvailabilitiesPort operaHotelAvailabilitiesPort;
  private final OperaRoomTypesValidator roomTypeValidator;

  @GetMapping(value = "/search/hotels/availabilities/v1", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<OperaHotelAvailabilitiesDto> getOperaHotelAvailabilities(
      @Valid OperaSearchCriteria operaSearchCriteria,
      @RequestParam MultiValueMap<String, String> roomTypeMap,
      @RequestParam(required = false, name = "flagMlos") Boolean flagMlos) {
    try {
      log.debug("Requesting hotelAvailabilities with operaSearchCriteria - {}",
          sanitize(operaSearchCriteria));

      List<String> roomTypesList = roomTypeMap.get("roomTypes");
      log.debug("roomTypesList from searchCriteria - {}", sanitize(roomTypesList));
      OperaHotelsSearchCriteria operaHotelsSearchCriteria =
          getOperaSearchCriteria(operaSearchCriteria, roomTypesList, flagMlos);

      log.debug("operaHotelsSearchCriteria - {}", operaHotelsSearchCriteria);

      final List<Hotel> availableHotels = operaHotelAvailabilitiesPort
          .getOperaHotelAvailabilities(operaHotelsSearchCriteria);
      if (CollectionUtils.isEmpty(availableHotels)) {
        log.debug("No availabilities found in DB and returning Empty HotelAvailabilities with status - 200");
        return new ResponseEntity<>(new OperaHotelAvailabilitiesDto(availableHotels.size(),
            Collections.emptyList()), HttpStatus.OK);
      }

      final OperaHotelAvailabilitiesDto response = mapHotelsToOperaHotelAvailabilitiesDto(
          availableHotels, roomTypeMap);
      final Set<String> availableHotelCodes = availableHotels.stream().map(Hotel::getHotelCode)
          .collect(Collectors.toSet());
      log.debug(
          "Returning response with available opera hotels with size - {}",
          response.getOperaHotelAvailabilities().size());
      log.debug(
          "Returning response for the hotels - {} for the operaHotelsSearchCriteria - {}",
          availableHotelCodes, operaHotelsSearchCriteria);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception ex) {
      log.error("Error occurred while searching hotel availabilities {}", ex.getMessage());
      if (ex instanceof RoomTypesException) {
        throw new HotelAvailabilitiesException(
            "\"errors\": Invalid roomType or roomType & roomQty values",
            HttpStatus.BAD_REQUEST);
      }
      throw new HotelAvailabilitiesException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private OperaHotelsSearchCriteria getOperaSearchCriteria(
      final OperaSearchCriteria operaSearchCriteria,
      final List<String> roomTypesList, Boolean flagMlos) {
    if (roomTypesList == null || roomTypesList.isEmpty()) {
      log.error("unable to read roomTypes from input");
      throw new RoomTypesException("roomTypes cannot be null", HttpStatus.BAD_REQUEST);
    }
    String[][] roomTypes = getRoomTypes(roomTypesList);
    log.debug("roomTypes- {} and size - {}", sanitizeRoomTypesForLog(roomTypes), roomTypes.length);

    OperaHotelsSearchCriteria operaHotelsSearchCriteria =
        buildOperaHotelsSearchCriteria(operaSearchCriteria, roomTypes, flagMlos);

    boolean isValidRoomType = roomTypeValidator.isValidRoomTypes(operaHotelsSearchCriteria);
    if (!isValidRoomType) {
      log.error("RoomTypes and roomQty validation failed");
      throw new RoomTypesException("Invalid roomType or roomType & roomQty values",
          HttpStatus.BAD_REQUEST);
    }
    return operaHotelsSearchCriteria;
  }

  private String[][] getRoomTypes(final List<String> roomTypesList) {
    final String[][] romTypes = new String[roomTypesList.size()][];
    int roomTypeIndex = 0;
    for (String roomTypeStr : roomTypesList) {
      String[] roomArray = roomTypeStr.split(",");
      romTypes[roomTypeIndex] = roomArray;
      roomTypeIndex++;
    }
    return romTypes;
  }

}

package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.controller;

import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.DistributionHotelAvailabilitiesUtil.buildDistributionPayload;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
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
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionHotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.DistributionHotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils;
import uk.co.whitbread.availabilitycacheservice.infrastructure.exceptions.HotelAvailabilitiesException;
import uk.co.whitbread.availabilitycacheservice.infrastructure.exceptions.RoomTypesException;
import uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.dsitribution.DistributionHotelAvailabilitiesMapper;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution.DistributionPayload;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution.DistributionSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.distribution.DistributionHotelAvailabilitiesDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.distribution.DistributionHotelDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.distribution.DistributionOperaRoomTypesValidator;

@Slf4j
@AllArgsConstructor
@RestController
public class DistributionHotelAvailabilitiesController {

  private final DistributionHotelAvailabilitiesPort distributionHotelAvailabilitiesPort;

  private final DistributionOperaRoomTypesValidator distributionOperaRoomTypesValidator;

  private final DistributionHotelAvailabilitiesMapper distributionHotelAvailabilitiesMapper;


  @GetMapping(value = "/dist/search/hotels/availabilities",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DistributionHotelAvailabilitiesDto> distributionAvailabilities(
      @Valid DistributionSearchCriteria distributionSearchCriteria,
      @RequestParam MultiValueMap<String, String> inputMap) {
    try {
      log.info("Requesting hotelAvailabilities with DistributionSearchCriteria - {}",
          sanitize(distributionSearchCriteria));

      final List<String> roomTypesList = inputMap.get("roomTypes");
      log.debug("roomTypesList from searchCriteria - {}",
          roomTypesList == null ? "" :
              roomTypesList.stream()
                  .map(SanitizingUtils::sanitize)
                  .toList());

      final String[][] roomTypesArr = getRoomTypes(roomTypesList);
      final Set<String> rateCodes = getRateCodes(distributionSearchCriteria);

      final DistributionPayload distributionPayload =
          buildDistributionPayload(distributionSearchCriteria, roomTypesArr, rateCodes);

      validateRoomTypeList(roomTypesList, distributionPayload);

      final List<DistributionHotel> distributionHotelList =
          distributionHotelAvailabilitiesPort.getHotelAvailabilities(distributionPayload);

      log.debug("distributionHotelList : {}", distributionHotelList);

      final List<DistributionHotelDto> distributionHotelDtoList =
          distributionHotelAvailabilitiesMapper.toDistributionHotelDtoList(distributionHotelList);

      final DistributionHotelAvailabilitiesDto distributionHotelAvailabilitiesDto =
          buildDistributionHotelAvailabilitiesDto(distributionHotelDtoList);

      log.debug("distributionHotelAvailabilitiesDto: {}", distributionHotelAvailabilitiesDto);

      return new ResponseEntity<>(distributionHotelAvailabilitiesDto, HttpStatus.OK);
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

  private Set<String> getRateCodes(DistributionSearchCriteria distributionSearchCriteria) {
    return CollectionUtils.isNotEmpty(distributionSearchCriteria.getRatePlanCodes())
        ? distributionSearchCriteria.getRatePlanCodes()
        : null;
  }

  private DistributionHotelAvailabilitiesDto buildDistributionHotelAvailabilitiesDto(
      final List<DistributionHotelDto> distributionHotelDtoList) {
    final DistributionHotelAvailabilitiesDto distributionHotelAvailabilitiesDto =
        new DistributionHotelAvailabilitiesDto(
            distributionHotelDtoList.size(), distributionHotelDtoList);
    log.debug("distributionHotelAvailabilitiesDto : {}", distributionHotelAvailabilitiesDto);

    return distributionHotelAvailabilitiesDto;

  }

  private void validateRoomTypeList(
      final List<String> roomTypesList,
      final DistributionPayload distributionPayload) {
    if (roomTypesList == null || roomTypesList.isEmpty()) {
      log.error("unable to read roomTypes from input");
      throw new RoomTypesException("roomTypes cannot be null", HttpStatus.BAD_REQUEST);
    }

    boolean isValidRoomType =
        distributionOperaRoomTypesValidator.isValidRoomTypes(distributionPayload);
    if (!isValidRoomType) {
      log.error("RoomTypes and roomQty validation failed");
      throw new RoomTypesException("Invalid roomType or roomType & roomQty values",
          HttpStatus.BAD_REQUEST);
    }
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

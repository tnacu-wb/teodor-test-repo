package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.controller;

import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.HotelAvailabilitiesMapper.mapHotelToHotelDto;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.HotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.exceptions.HotelAvailabilitiesException;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.HotelAvailabilitiesDto;

@Slf4j
@AllArgsConstructor
@RestController
public class HotelAvailabilitiesController {

  private final HotelAvailabilitiesPort hotelAvailabilitiesPort;

  @GetMapping(value = "/search/hotels/availabilities", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<HotelAvailabilitiesDto> getHotelAvailabilities(@Valid SearchCriteria searchCriteria) {
    try {
      log.debug("Requesting availabilities for {} hotels with {} ", searchCriteria.getHotelCodes().size(),
          sanitize(searchCriteria));

      List<Hotel> availableHotels = hotelAvailabilitiesPort.getHotelAvailabilities(searchCriteria);

      if (CollectionUtils.isEmpty(availableHotels)) {
        log.debug("No availabilities found in DB and returning Empty HotelAvailabilities with status - 200");
        return new ResponseEntity<>(new HotelAvailabilitiesDto(availableHotels.size(),
            searchCriteria.getPage(), searchCriteria.getSize(), Collections.emptyList()), HttpStatus.OK);
      }

      final HotelAvailabilitiesDto response = new HotelAvailabilitiesDto(availableHotels.size(),
          searchCriteria.getPage(), searchCriteria.getSize(), mapHotelToHotelDto(availableHotels));

      final Set<String> availableHotelCodes = availableHotels.stream().map(Hotel::getHotelCode).collect(
          Collectors.toSet());
      log.debug("Returning response for the hotels - {}", availableHotelCodes);
      log.debug("Returning response with {} available hotels", response.getHotelAvailabilities().size());

      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (HotelAvailabilitiesException ex) {
      log.error("No availabilities found", ex);
      throw new HotelAvailabilitiesException(ex.getMessage(), ex.getStatus());
    } catch (Exception ex) {
      log.error("Error occurred while searching hotel availabilities {}", ex);
      throw new HotelAvailabilitiesException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
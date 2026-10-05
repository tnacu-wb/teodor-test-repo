package uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.controller;

import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.ondemandrefreshservice.domain.logic.ProcessRefreshHotelAvailability;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.OnDemandProcessResponse;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.util.SanitizingUtils;

import static uk.co.whitbread.ondemandrefreshservice.infrastructure.util.SanitizingUtils.sanitize;

@RestController
@Slf4j
@AllArgsConstructor
@Validated
public class HotelAvailabilityOnDemandRefreshController {

  private final ProcessRefreshHotelAvailability processRefreshHotelAvailability;

  @GetMapping(value = "/refresh/hotels", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> refreshHotelAvailabilityOnDemand(
      @RequestParam("hotelIds") Set<String> hotelIds, @RequestParam(required = false) String startDate,
      @RequestParam(required = false) String endDate) {
    //Sanitize hotelIds to prevent log injection
    Set<String> sanitizedHotelIds = hotelIds.stream()
            .map(SanitizingUtils::sanitize)
            .collect(Collectors.toSet());
    log.info("Refresh Hotel availability for hotelIds: {} with startDate: {} endDate: {}", sanitizedHotelIds,
        StringUtils.normalizeSpace(sanitize(startDate)), StringUtils.normalizeSpace(sanitize(endDate)));

    try {
      OnDemandProcessResponse onDemandProcessResponse = processRefreshHotelAvailability
          .refreshOnDemandWithDates(hotelIds, startDate, endDate);
      log.info("Refresh hotel availability on demand completed");
      return new ResponseEntity<>(onDemandProcessResponse.getMessage(), onDemandProcessResponse.getStatus());
    } catch (Exception exception) {
      log.info("Unable to process on refresh hotel availability");
      return new ResponseEntity<>(exception.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}

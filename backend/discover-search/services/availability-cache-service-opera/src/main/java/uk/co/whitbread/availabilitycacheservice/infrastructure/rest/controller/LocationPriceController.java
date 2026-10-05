package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.controller;

import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.hotelprice.BestPricedHotelDtoMapper.buildLocationPriceResponse;

import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.hotelprice.LocationPricePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.LocationPriceRequest;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.LocationPriceResponse;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LocationPriceController {

  private final LocationPricePort locationPricePort;


  @GetMapping(value = "/search/locations/prices", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<LocationPriceResponse> getBestPricedHotels(@Valid LocationPriceRequest locationPriceRequest) {

    log.debug("Requesting startDate={}, endDate={}, priceThresHold={}, altpriceThresHold={}",
        sanitize(locationPriceRequest.getStartDate()), sanitize(locationPriceRequest.getEndDate()),
        sanitize(locationPriceRequest.getPriceThreshold()), sanitize(locationPriceRequest.getAltPriceThreshold()));

    final List<Hotel> locationAvailabilities = locationPricePort.getBestPricedHotels(locationPriceRequest);
    final int noOfHotels = locationAvailabilities.size();
    log.debug("End call for locations. {} items returned", noOfHotels);

    LocationPriceResponse locationPriceResponse = buildLocationPriceResponse(locationAvailabilities);

    if (noOfHotels == 0) {
      return new ResponseEntity<>(buildLocationPriceResponse(Collections.emptyList()), HttpStatus.OK);
    }

    return new ResponseEntity<>(locationPriceResponse, HttpStatus.OK);
  }
}

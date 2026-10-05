package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.controller;

import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.hotelprice.BestPricedHotelDtoMapper.mapHotelToBestPricedHotelDto;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.hotelprice.BestPricedHotelDtoMapper.mapHotelToHotelPriceCalendar;

import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.hotelprice.HotelPricePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.HotelPriceCalendarRequest;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.HotelPriceRequest;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.BestPricedHotelDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.HotelPriceCalendar;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.HotelPriceCalendarResponse;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.HotelPriceResponseDto;

@Slf4j
@RequiredArgsConstructor
@RestController
public class HotelPriceController {

  private final HotelPricePort hotelPricePort;

  @GetMapping(value = "/search/hotels/prices", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<HotelPriceResponseDto> getHotelsWithPrices(@Valid HotelPriceRequest hotelPriceRequest) {

    log.debug("Requesting hotel prices: hotelCodes:{},arrival={}, departure={},roomType:{}",
        sanitize(hotelPriceRequest.getHotelCodes()), sanitize(hotelPriceRequest.getArrival()),
        sanitize(hotelPriceRequest.getDeparture()),
        sanitize(hotelPriceRequest.getRoomType()));

    final List<Hotel> hotels = hotelPricePort.getBestPricedHotels(hotelPriceRequest);

    List<BestPricedHotelDto> bestPricedHotelDtoList = mapHotelToBestPricedHotelDto(hotels);

    final HotelPriceResponseDto hotelPriceResponseDto = buildHotelPriceResponseDto(bestPricedHotelDtoList);

    final int noOfHotels = hotelPriceResponseDto.getBestPricedHotels().size();

    log.debug("Number of best priced hotels being returned: {}", noOfHotels);

    if (noOfHotels == 0) {
      return new ResponseEntity<>(buildHotelPriceResponseDto(Collections.emptyList()), HttpStatus.OK);
    }

    return new ResponseEntity<>(hotelPriceResponseDto, HttpStatus.OK);
  }

  @GetMapping(value = "/search/hotels/{hotelCode}/calendars", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<HotelPriceCalendarResponse> getHotelPriceDetails(
      @PathVariable String hotelCode,
      @Valid HotelPriceCalendarRequest hotelPriceCalendarRequest) {

    log.debug(
        "Requesting best price per date for hotelCode:{},arrival={}, departure={},roomType:{}",
        sanitize(hotelCode), sanitize(hotelPriceCalendarRequest.getArrival()),
        sanitize(hotelPriceCalendarRequest.getDeparture()),
        sanitize(hotelPriceCalendarRequest.getRoomType()));

    final List<Hotel> hotels = hotelPricePort
        .getBestPricedHotelsForGivenHotelCode(hotelCode, hotelPriceCalendarRequest);

    final List<HotelPriceCalendar> bestPricedHotelDtoList = mapHotelToHotelPriceCalendar(hotels);

    final HotelPriceCalendarResponse hotelPriceCalendarResponse =
        buildHotelPriceCalendarResponse(bestPricedHotelDtoList);

    final int numberOfRecords = hotelPriceCalendarResponse.getBestPricedHotels().size();

    log.debug("Best Price for {} number of dates being returned.", numberOfRecords);

    if (numberOfRecords == 0) {
      return new ResponseEntity<>(buildHotelPriceCalendarResponse(Collections.emptyList()), HttpStatus.OK);
    }

    return new ResponseEntity<>(hotelPriceCalendarResponse, HttpStatus.OK);
  }

  private HotelPriceResponseDto buildHotelPriceResponseDto(final List<BestPricedHotelDto> bestPricedHotelDtoList) {
    return HotelPriceResponseDto
        .builder()
        .total(bestPricedHotelDtoList.size())
        .bestPricedHotels(bestPricedHotelDtoList)
        .build();

  }

  private HotelPriceCalendarResponse buildHotelPriceCalendarResponse(
      final List<HotelPriceCalendar> bestPricedHotelDtoList) {

    return HotelPriceCalendarResponse
        .builder()
        .bestPricedHotels(bestPricedHotelDtoList)
        .build();
  }

}
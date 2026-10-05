package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.controller;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.CalendarPriceFinderHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.PriceFinderHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.PriceFinderOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.PriceFinderHotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.pricefinder.PriceFinderHotelAvailabilitiesMapper;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.CalendarPriceFinderLocationSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.PriceFinderLocationSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.PriceFinderSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.pricefinder.model.out.CalendarPriceFinderHotelAvailabilitiesDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.pricefinder.model.out.HotelCodeView;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.pricefinder.model.out.HotelNameView;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.pricefinder.model.out.PriceFinderHotelAvailabilitiesDto;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PriceFinderController {

  private final PriceFinderHotelAvailabilitiesMapper mapper;

  private final PriceFinderHotelAvailabilitiesPort priceFinderHotelAvailabilitiesPort;

  @GetMapping(value = "/search/price-finder/location", produces = MediaType.APPLICATION_JSON_VALUE)
  @JsonView(HotelNameView.class)

  public ResponseEntity<PriceFinderHotelAvailabilitiesDto> loadLowestPricesByLocation(
      @Valid PriceFinderLocationSearchCriteria criteria) {

    PriceFinderHotelAvailabilities availabilities;
    if (criteria.getFilterByRoomType() != null && !criteria.getFilterByRoomType().isEmpty()) {
      List<String> filterByRoomTypeList = List.of(criteria.getFilterByRoomType().split(","));
      availabilities = priceFinderHotelAvailabilitiesPort
          .getLowestPricesByLocationWithRoomTypeSubstitution(criteria, filterByRoomTypeList);
    } else {
      availabilities = priceFinderHotelAvailabilitiesPort
          .getLowestPricesByLocation(criteria);
    }

    PriceFinderHotelAvailabilitiesDto priceFinderAvailabilitiesResponseDto = mapper
            .toPriceFinderAvailabilitiesResponseDto(availabilities);

    return new ResponseEntity<>(priceFinderAvailabilitiesResponseDto, HttpStatus.OK);
  }

  @GetMapping(value = "/search/price-finder/calendar", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CalendarPriceFinderHotelAvailabilitiesDto> getLowestPricesByLocationForCalendar(
      @Valid CalendarPriceFinderLocationSearchCriteria criteria) {

    CalendarPriceFinderHotelAvailabilities availabilities =
        priceFinderHotelAvailabilitiesPort.getLowestPricesByLocationForCalendar(criteria);

    CalendarPriceFinderHotelAvailabilitiesDto calendarPriceFinderHotelAvailabilitiesDto =
        mapper.toCalendarPriceFinderHotelAvailabilitiesDto(availabilities);

    return new ResponseEntity<>(calendarPriceFinderHotelAvailabilitiesDto, HttpStatus.OK);
  }

  @GetMapping(value = "/search/price-finder/hotels", produces = MediaType.APPLICATION_JSON_VALUE)
  @JsonView(HotelCodeView.class)
  public ResponseEntity<PriceFinderHotelAvailabilitiesDto> getLowestPricesByHotel(
      @Valid PriceFinderSearchCriteria priceFinderSearchCriteria) {

    final List<PriceFinderOperaHotelAvailabilities> hotels = priceFinderHotelAvailabilitiesPort
            .getLowestPricesByHotel(priceFinderSearchCriteria);

    PriceFinderHotelAvailabilitiesDto priceFinderHotelAvailabilitiesDto = PriceFinderHotelAvailabilitiesDto.builder()
        .priceFinderOperaHotelAvailabilitiesDtoList(mapper.toPriceFinderOperaHotelAvailabilitiesDtoList(hotels))
        .build();

    return new ResponseEntity<>(priceFinderHotelAvailabilitiesDto, HttpStatus.OK);
  }

}

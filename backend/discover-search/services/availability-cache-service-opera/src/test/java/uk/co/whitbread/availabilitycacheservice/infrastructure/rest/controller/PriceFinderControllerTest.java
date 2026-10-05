package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.Availabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.CalendarPriceFinderHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.PriceFinderHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.PriceFinderOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.PriceFinderHotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.pricefinder.PriceFinderHotelAvailabilitiesMapper;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.CalendarPriceFinderLocationSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.PriceFinderLocationSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.PriceFinderSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.pricefinder.model.out.CalendarPriceFinderHotelAvailabilitiesDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.pricefinder.model.out.PriceFinderHotelAvailabilitiesDto;

@ExtendWith(MockitoExtension.class)
class PriceFinderControllerTest {

  @InjectMocks
  private PriceFinderController priceFinderController;

  @Mock
  private PriceFinderHotelAvailabilitiesMapper priceFinderHotelAvailabilitiesMapper;

  @Mock
  private PriceFinderHotelAvailabilitiesPort priceFinderHotelAvailabilitiesPort;

  @Test
  void loadLowestPricesByLocation_success() {
    when(priceFinderHotelAvailabilitiesPort.getLowestPricesByLocation(any()))
            .thenReturn(new PriceFinderHotelAvailabilities());
    when(priceFinderHotelAvailabilitiesMapper.toPriceFinderAvailabilitiesResponseDto(any()))
            .thenReturn(new PriceFinderHotelAvailabilitiesDto());

    ResponseEntity<PriceFinderHotelAvailabilitiesDto> result =
            priceFinderController.loadLowestPricesByLocation(new PriceFinderLocationSearchCriteria());

    assertNotNull(result);
  }

  @Test
  void getLowestPricesByLocationForCalendar_success() {
    when(priceFinderHotelAvailabilitiesPort.getLowestPricesByLocationForCalendar(any()))
        .thenReturn(new CalendarPriceFinderHotelAvailabilities());
    when(priceFinderHotelAvailabilitiesMapper.toCalendarPriceFinderHotelAvailabilitiesDto(any()))
        .thenReturn(any());

    ResponseEntity<CalendarPriceFinderHotelAvailabilitiesDto> result =
        priceFinderController.getLowestPricesByLocationForCalendar(new CalendarPriceFinderLocationSearchCriteria());

    assertNotNull(result);
  }

  @Test
  void getLowestPricesByHotel_success() {
    when(priceFinderHotelAvailabilitiesPort.getLowestPricesByHotel(buildSearchCriteria()))
            .thenReturn(buildListOfOperaHotelAvailabilities());

    when(priceFinderHotelAvailabilitiesMapper.toPriceFinderOperaHotelAvailabilitiesDtoList(anyList()))
            .thenReturn(new ArrayList<>());

    ResponseEntity<PriceFinderHotelAvailabilitiesDto> response =
            priceFinderController.getLowestPricesByHotel(buildSearchCriteria());

    assertNotNull(response);
  }

  private PriceFinderSearchCriteria buildSearchCriteria() {
    return PriceFinderSearchCriteria.builder()
            .hotelCodes(Arrays.asList("LONKIN", "LONEUS"))
            .arrival("2025-12-12")
            .departure("2025-12-16")
            .country("GB")
            .language("EN")
            .build();
  }

  private List<PriceFinderOperaHotelAvailabilities> buildListOfOperaHotelAvailabilities() {
    final List<PriceFinderOperaHotelAvailabilities> priceFinderOperaHotelAvailabilitiesList = new ArrayList<>();
    final PriceFinderOperaHotelAvailabilities priceFinderOperaHotelAvailabilities =
            new PriceFinderOperaHotelAvailabilities();
    final Set<Availabilities> availabilitiesList = new HashSet<>();

    Availabilities availabilities = new Availabilities();
    availabilities.setAvailableDate("2025-03-12");
    availabilities.setCurrency("GBP");
    availabilities.setMinimumRate(new BigDecimal("40.00"));

    availabilitiesList.add(availabilities);
    priceFinderOperaHotelAvailabilities.setHotelCode("LONKIN");
    priceFinderOperaHotelAvailabilities.setAvailabilities(availabilitiesList);

    priceFinderOperaHotelAvailabilitiesList.add(priceFinderOperaHotelAvailabilities);

    return priceFinderOperaHotelAvailabilitiesList;
  }
}

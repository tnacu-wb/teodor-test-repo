package uk.co.whitbread.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.model.availabilitycache.in.AvailabilityCacheSearchCriteria;
import uk.co.whitbread.domain.model.availabilitycache.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.domain.model.availabilitycache.out.HotelResponse;
import uk.co.whitbread.domain.model.availabilitycache.out.PriceResponse;
import uk.co.whitbread.domain.model.availabilitycache.out.RatePlanResponse;
import uk.co.whitbread.domain.model.availabilitycache.out.RoomResponse;
import uk.co.whitbread.domain.ports.secondary.AvailabilityCacheSearchOutPort;

@ExtendWith(MockitoExtension.class)
class AvailabilityCacheSearchInPortImplTest {

  @Mock
  private AvailabilityCacheSearchOutPort availabilityCacheOutPort;

  @InjectMocks
  private AvailabilityCacheSearchInPortImpl availabilityCacheSearchInPortImpl;

  @Test
  void getAvailabilitiesFromAvailabilityCache__ShouldReturnOk() {
    var request = createAvailabilitySearchCriteria();
    var response = mockAvailabilityCacheResponse();

    when(availabilityCacheOutPort.getAvailabilitiesFromAvailabilityCache(
        any())).thenReturn(response);

    var availabilityCacheResponse = availabilityCacheSearchInPortImpl.getAvailabilitiesFromAvailabilityCache(
        request);

    assertThat(availabilityCacheResponse, notNullValue());
    assertEquals(response, availabilityCacheResponse);

  }

  private AvailabilityCacheSearchCriteria createAvailabilitySearchCriteria() {
    return AvailabilityCacheSearchCriteria.builder()
        .hotelCodes(Arrays.asList("LONLEI", "LONEUS"))
        .arrival("2022-01-01")
        .departure("2022-01-05")
        .language("en")
        .country("gb")
        .rooms(1)
        .adults(List.of(1))
        .children(List.of(0))
        .type(List.of("DB"))
        .page(1)
        .size(40)
        .build();
  }

  private HotelAvailabilitiesResponse mockAvailabilityCacheResponse() {
    return HotelAvailabilitiesResponse.builder()
        .total(1)
        .page(1)
        .pageSize(1)
        .hotelAvailabilities(createListOfAvailabilities())
        .build();
  }

  private List<HotelResponse> createListOfAvailabilities() {
    List<HotelResponse> hotelResponses = new LinkedList<>();
    hotelResponses.add(HotelResponse.builder()
        .hotelCode("LONLEI")
        .hotelName("Test Hotel Name")
        .distance("100")
        .hotelBrand("pi")
        .available(true)
        .limitedAvailability(false)
        .arrivalDateToday(false)
        .rates(createListOfRates())
        .build());
    return hotelResponses;
  }

  private List<RatePlanResponse> createListOfRates() {
    List<RatePlanResponse> ratePlanResponses = new LinkedList<>();
    ratePlanResponses.add(RatePlanResponse.builder()
        .code("FLEXRATE")
        .name("Flex")
        .description(
            "Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival")
        .classification("A")
        .order("1")
        .totalCost(PriceResponse.builder()
            .amount(new BigDecimal("60.00"))
            .currency("EUR").build())
        .rooms(createRoomsList())
        .build());
    return ratePlanResponses;
  }

  private List<RoomResponse> createRoomsList() {
    List<RoomResponse> roomResponses = new LinkedList<>();
    roomResponses.add(RoomResponse.builder()
        .type("DB")
        .adults(1)
        .children(0)
        .totalCost(PriceResponse.builder()
            .amount(new BigDecimal("60.00"))
            .currency("EUR").build())
        .build());
    return roomResponses;
  }

}

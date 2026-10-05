package uk.co.whitbread.infrastructure.rest.client.availabilitycache;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
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
import uk.co.whitbread.infrastructure.rest.client.AvailabilityCacheClient;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.exception.AvailabilityCacheException;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.mapper.AvailabilityCacheResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.mapper.AvailabilityCacheSearchMapper;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.in.AvailabilityCacheRequest;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.out.HotelAvailabilitiesDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.out.HotelDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.out.PriceDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.out.RatePlanDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.out.RoomDto;

@ExtendWith(MockitoExtension.class)
class AvailabilityCacheSearchOutPortImplTest {

  @InjectMocks
  AvailabilityCacheSearchOutPortImpl outPort;
  @Mock
  private AvailabilityCacheSearchMapper availabilityCacheSearchMapper;
  @Mock
  private AvailabilityCacheResponseMapper availabilityCacheResponseMapper;
  @Mock
  private AvailabilityCacheClient availabilityCacheClient;

  @Test
  void getAvailabilitiesFromAvailabilityCache_shouldReturnOK() {
    //Arrange
    when(availabilityCacheSearchMapper.toDto(any(AvailabilityCacheSearchCriteria.class)))
        .thenReturn(createAvailabilityCacheRequest());
    when(availabilityCacheClient.getAvailabilitiesFromCache(any(AvailabilityCacheRequest.class)))
        .thenReturn(createAvailabilityCacheResponse());
    when(availabilityCacheResponseMapper.toModel(any(HotelAvailabilitiesDto.class)))
        .thenReturn(mapAvailabilityCacheResponse());

    //Act
    var availabilityResponse = outPort.getAvailabilitiesFromAvailabilityCache(
        createAvailabilityRequest());

    //Assert
    assertThat(availabilityResponse, notNullValue());
    assertThat(availabilityResponse.getHotelAvailabilities().get(0).getHotelCode(), is("LONLEI"));
    assertThat(availabilityResponse.getHotelAvailabilities().get(0).getHotelName(),
        is("Test Hotel Name"));
    assertThat(availabilityResponse.getHotelAvailabilities()
        .get(0).getRates().get(0).getName(), is("Flex"));
    assertThat(availabilityResponse.getHotelAvailabilities()
        .get(0).getRates().get(0).getRooms().get(0).getType(), is("DB"));
  }

  @Test
  void getAvailabilitiesFromAvailabilityCache_shouldThrowException() {
    //Arrange
    String error = "An error was returned by Availability Cache Service";

    when(availabilityCacheSearchMapper.toDto(any(AvailabilityCacheSearchCriteria.class)))
        .thenReturn(createAvailabilityCacheRequest());
    when(availabilityCacheClient.getAvailabilitiesFromCache(any(AvailabilityCacheRequest.class)))
        .thenThrow(new AvailabilityCacheException("message", "An error was returned by Availability Cache Service",
                new Exception(), 1));

    //Act
    AvailabilityCacheSearchCriteria request = createAvailabilityRequest();

    var exception = Assertions.assertThrows(AvailabilityCacheException.class,
        () -> outPort.getAvailabilitiesFromAvailabilityCache(request));

    //Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  private HotelAvailabilitiesDto createAvailabilityCacheResponse() {
    return new HotelAvailabilitiesDto(1, 1, 1, createHotelAvailabilities());
  }

  private List<HotelDto> createHotelAvailabilities() {
    List<HotelDto> hotelDtoList = new LinkedList<>();
    hotelDtoList.add(new HotelDto("LONLEI", "Test Hotel Name", "pi",
        true, false, false, createRatesList()));
    return hotelDtoList;
  }

  private List<RatePlanDto> createRatesList() {
    List<RatePlanDto> ratePlanDtoList = new LinkedList<>();
    ratePlanDtoList.add(new RatePlanDto("FLEXRATE", "Flex",
        "Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival",
        "A", "1", new PriceDto(new BigDecimal("60.00"), "EUR"), createRooms()));
    return ratePlanDtoList;
  }

  private List<RoomDto> createRooms() {
    List<RoomDto> roomDtoList = new LinkedList<>();
    roomDtoList.add(new RoomDto("DB", 1, 0, new PriceDto(new BigDecimal("60.00"), "EUR")));
    return roomDtoList;
  }

  private HotelAvailabilitiesResponse mapAvailabilityCacheResponse() {
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

  private AvailabilityCacheRequest createAvailabilityCacheRequest() {
    return AvailabilityCacheRequest.builder()
        .hotelCodes(Arrays.asList("LONLEI", "LONEUS"))
        .arrival("2022-01-01")
        .departure("2022-01-05")
        .language("en")
        .country("gb")
        .rooms(1)
        .adults(new int[]{1})
        .children(new int[]{0})
        .type(new String[]{"DB"})
        .page(1)
        .size(40)
        .build();
  }

  private AvailabilityCacheSearchCriteria createAvailabilityRequest() {
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

}
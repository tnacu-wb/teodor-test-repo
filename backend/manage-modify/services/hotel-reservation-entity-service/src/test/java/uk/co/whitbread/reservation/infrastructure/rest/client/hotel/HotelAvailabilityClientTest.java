package uk.co.whitbread.reservation.infrastructure.rest.client.hotel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.hotel.entity.service.generated.models.hotel.HotelAvailabilityByIdsV2Dto;
import uk.co.whitbread.hotel.entity.service.generated.models.hotel.HotelAvailabilityV2Dto;
import uk.co.whitbread.hotel.entity.service.generated.models.hotel.RoomStayV2Dto;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotel.exceptions.HotelAvailabilityException;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotel.model.HotelAvailabilityV2RequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotel.service.HotelAvailabilityClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotel.service.properties.HotelAvailabilityClientProperties;
import uk.co.whitbread.reservation.infrastructure.rest.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
public class HotelAvailabilityClientTest {
  public static final String HOTEL_ID = "TKINPT";

  @Mock
  private WebClient webClient;
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec customResponseSpec;
  @Mock
  private HotelAvailabilityClientProperties hotelAvailabilityClientProperties;
  @InjectMocks
  private HotelAvailabilityClient hotelAvailabilityClient;

  @Test
  void testGetAvailabilityV2_success() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(hotelAvailabilityClientProperties.getHotelsAvailabilityV2Endpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelAvailabilityByIdsV2Dto.class)).thenReturn(
        mockHotelAvailabilityV2Response());

    // Act
    var response = hotelAvailabilityClient.getHotelAvailabilityV2(new HotelAvailabilityV2RequestDto());

    // Assert
    assertThat(response.getHotelAvailability().get(0).getHotelId(), is(HOTEL_ID));
  }

  @Test
  void testGetAvailabilityV2_ThrowsException() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(hotelAvailabilityClientProperties.getHotelsAvailabilityV2Endpoint())).thenReturn(
        requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(HotelAvailabilityException.class,
        () -> hotelAvailabilityClient.getHotelAvailabilityV2(new HotelAvailabilityV2RequestDto()));
  }

  private Mono<HotelAvailabilityByIdsV2Dto> mockHotelAvailabilityV2Response() {

    var response = new HotelAvailabilityByIdsV2Dto();
    var hotelAvailability = new HotelAvailabilityV2Dto();
    var roomStays = new RoomStayV2Dto();
    roomStays.setRoomClass("ST");
    hotelAvailability.setHotelId(HOTEL_ID);
    hotelAvailability.setRoomStays(List.of(roomStays));
    response.setHotelAvailability(List.of(hotelAvailability));
    return Mono.just(response);
  }

}

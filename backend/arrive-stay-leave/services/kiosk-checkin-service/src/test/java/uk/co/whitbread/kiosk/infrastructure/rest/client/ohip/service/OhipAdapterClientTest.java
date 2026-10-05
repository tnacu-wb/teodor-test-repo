package uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Set;
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
import uk.co.whitbread.kiosk.domain.model.checkin.in.KioskCheckInRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.in.ProfileRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.in.UpdateCommentRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.kiosk.domain.model.checkin.out.ReservationAmounts;
import uk.co.whitbread.kiosk.domain.model.roomallocation.in.AllocateRequest;
import uk.co.whitbread.kiosk.domain.model.roomallocation.in.Criteria;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.AllocationResponse;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.HouseKeepingResponse;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.KioskReservationPreferences;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.VacantRoomResponse;
import uk.co.whitbread.kiosk.infrastructure.rest.CustomTestResponseSpec;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.exceptions.OhipAdapterException;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.service.properties.OhipAdapterProperties;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class OhipAdapterClientTest {

  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
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
  private OhipAdapterProperties ohipAdapterPropertiesMock;

  @InjectMocks
  private OhipAdapterClient ohipAdapterClient;

  private static final String HOTEL_NAME = "TEST";
  private static final String RESERVATION_NUMBER = "ABCD122";
  private static final String ROOM_TYPE = "DOUBLE";

  @Test
  void createProfile_WhenOhipClientReturnsAnError_ThenProfileExceptionIsThrown() {
    OhipAdapterException ex = mock(OhipAdapterException.class);
    when(customResponseSpec.toBodilessEntity()).thenReturn(Mono.error(ex));
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    ProfileRequest createProfileRequest = new ProfileRequest();
    assertThrows(OhipAdapterException.class,
        () -> ohipAdapterClient.createProfile(createProfileRequest, HOTEL_NAME, RESERVATION_NUMBER));
  }

  @Test
  void doCheckIn_WenOhipClientReturnsAnError_ThenCheckInExceptionIsThrown() {
    OhipAdapterException ex = mock(OhipAdapterException.class);
    Mono<CheckInResponse> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(CheckInResponse.class)).thenReturn(rsp);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    KioskCheckInRequest kioskCheckInRequest = new KioskCheckInRequest();
    assertThrows(OhipAdapterException.class,
        () -> ohipAdapterClient.doCheckIn(kioskCheckInRequest));
  }

  @Test
  void allocateRooms_WhenOhipClientReturnsAnError_ThenOhipAdapterExceptionIsThrown() {
    OhipAdapterException ex = mock(OhipAdapterException.class);
    Mono<AllocationResponse> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(AllocationResponse.class)).thenReturn(rsp);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(ohipAdapterPropertiesMock.getAllocateRoomEndpoint())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    AllocateRequest allocateRequest = new AllocateRequest(new Criteria());
    assertThrows(OhipAdapterException.class,
        () -> ohipAdapterClient.allocateRooms(allocateRequest));
  }

  @Test
  void getVacantRooms_WhenOhipClientReturnsAnError_ThenOhipAdapterExceptionIsThrown() {
    OhipAdapterException ex = mock(OhipAdapterException.class);
    Mono<VacantRoomResponse> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(VacantRoomResponse.class)).thenReturn(rsp);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    assertThrows(OhipAdapterException.class,
        () -> ohipAdapterClient.getVacantRooms(HOTEL_NAME, ROOM_TYPE));
  }

  @Test
  void fetchHouseKeepingStatus_WenOhipClientReturnsAnError_ThenOhipAdapterExceptionIsThrown() {
    OhipAdapterException ex = mock(OhipAdapterException.class);
    Mono<HouseKeepingResponse> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(HouseKeepingResponse.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    assertThrows(OhipAdapterException.class,
        () -> ohipAdapterClient.fetchHouseKeepingStatus(HOTEL_NAME, ROOM_TYPE));
  }

  @Test
  void fetchReservationPreferences_WenOhipClientReturnsAnError_ThenOhipAdapterExceptionIsThrown() {
    OhipAdapterException ex = mock(OhipAdapterException.class);
    Mono<KioskReservationPreferences> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(KioskReservationPreferences.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    assertThrows(OhipAdapterException.class,
        () -> ohipAdapterClient.fetchReservationPreferences(HOTEL_NAME, ROOM_TYPE));
  }

  @Test
  void updateReservationComments_WhenOhipClientReturnsAnError_ThenOhipAdapterExceptionIsThrown() {
    OhipAdapterException ex = mock(OhipAdapterException.class);
    Mono<Object> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(Object.class)).thenReturn(rsp);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    UpdateCommentRequest updateCommentRequest = new UpdateCommentRequest();
    Mono<Object> mono =
        ohipAdapterClient.updateReservationComments(HOTEL_NAME, RESERVATION_NUMBER, updateCommentRequest);
    assertThrows(OhipAdapterException.class, mono::block);
  }

  @Test
  void updateProfile_WhenOhipClientReturnsAnError_ThenProfileExceptionIsThrown() {
    OhipAdapterException ex = mock(OhipAdapterException.class);
    when(customResponseSpec.toBodilessEntity()).thenReturn(Mono.error(ex));
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    ProfileRequest updateProfileRequest = new ProfileRequest();
    assertThrows(OhipAdapterException.class,
        () -> ohipAdapterClient.updateProfile(updateProfileRequest, HOTEL_NAME, RESERVATION_NUMBER));
  }

  @Test
  void getReservationAmounts_WhenOhipClientReturnsAnError_ThenExceptionIsThrown() {
    OhipAdapterException ex = mock(OhipAdapterException.class);
    Mono<ReservationAmounts> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(ReservationAmounts.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    Set<String> reservationNumbers = Set.of(RESERVATION_NUMBER);
    assertThrows(OhipAdapterException.class, () ->
        ohipAdapterClient.getReservationAmounts(reservationNumbers, HOTEL_NAME));
  }

  @Test
  void getReservationAmounts_success() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationAmounts.class)).thenReturn(
        Mono.just(new ReservationAmounts()));

    // Act
    var response = ohipAdapterClient.getReservationAmounts(Set.of(RESERVATION_NUMBER), HOTEL_NAME);

    // Assert
    assertNotNull(response);
    verifyNoMoreInteractions(webClient);
  }

}
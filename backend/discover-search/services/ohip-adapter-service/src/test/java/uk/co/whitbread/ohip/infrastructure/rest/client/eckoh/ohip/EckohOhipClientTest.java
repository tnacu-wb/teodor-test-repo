package uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.ohip;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservationDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPaymentMethodType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsDetailsReservations;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class EckohOhipClientTest {

  @InjectMocks
  private EckohOhipClient eckohOhipClient;
  @Mock
  private WebClient webClient;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;


  @Test
  void sendChangeReservationRequest_ShouldReturnOk() {

    //Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>)any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ChangeReservationDetails.class)).thenReturn(
        mockGetChangeReservation());

    //Act
    ChangeReservationDetails changedReservation =
        eckohOhipClient.sendChangeReservationRequest("BERALX", "35107",
            new ChangeReservation());

    //Assert
    assertThat(changedReservation, notNullValue());
    assertThat(changedReservation.getReservations().getReservation().get(0).getHotelId(),
        is("BERALX"));
    assertThat(
        changedReservation.getReservations().getReservation().get(0).getReservationIdList().get(0)
            .getId(), is("4353535"));
    assertThat(
        changedReservation.getReservations().getReservation().get(0).getReservationIdList().get(0)
            .getType(), is("Reservation"));
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getReservationsByBlockId_ShouldReturnOk() {

    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationsDetails.class)).thenReturn(
        mockGetReservationsDetails());

    //Act
    ReservationsDetails changedReservation =
        eckohOhipClient.getReservationsByBlockId("123434");

    //Assert
    assertThat(changedReservation, notNullValue());
    assertThat(
        changedReservation.getReservations().getReservationInfo().get(0).getReservationIdList()
            .get(0).getType(), is("Reservation"));
    assertThat(
        changedReservation.getReservations().getReservationInfo().get(0).getReservationIdList()
            .get(0).getId(), is("4353535"));
    assertThat(changedReservation.getReservations().getReservationInfo().get(0).getHotelId(),
        is("BERALX"));
    verifyNoMoreInteractions(webClient);
  }

  private Mono<ReservationsDetails> mockGetReservationsDetails() {
    var reservationsDetails = new ReservationsDetails();
    var reservationsDetailsReservations = new ReservationsDetailsReservations();
    var reservationInfoType = new ReservationInfoType();
    var uniqueIDType = new UniqueIDType();
    var reservationPaymentMethodType = new ReservationPaymentMethodType();
    var resPaymentCardType = new ResPaymentCardType();
    uniqueIDType.setType("Reservation");
    uniqueIDType.setId("4353535");
    resPaymentCardType.setCardType(CardTypeType.VA);
    reservationPaymentMethodType.setPaymentMethod("VA");
    reservationPaymentMethodType.setPaymentCard(resPaymentCardType);
    reservationInfoType.setReservationIdList(List.of(uniqueIDType));
    reservationInfoType.setPaymentMethod("VA");
    reservationInfoType.setReservationPaymentMethod(reservationPaymentMethodType);
    reservationInfoType.hotelId("BERALX");
    reservationsDetailsReservations.setReservationInfo(List.of(reservationInfoType));
    reservationsDetails.setReservations(reservationsDetailsReservations);
    return Mono.just(reservationsDetails);
  }

  private Mono<ChangeReservationDetails> mockGetChangeReservation() {
    var changeReservationDetails = new ChangeReservationDetails();
    var hotelReservationsType = new HotelReservationsType();
    var hotelReservationType = new HotelReservationType();
    var uniqueIDType = new UniqueIDType();
    uniqueIDType.setType("Reservation");
    uniqueIDType.setId("4353535");
    hotelReservationType.setHotelId("BERALX");
    hotelReservationType.setReservationIdList(List.of(uniqueIDType));
    hotelReservationsType.setReservation(List.of(hotelReservationType));
    changeReservationDetails.setReservations(hotelReservationsType);
    return Mono.just(changeReservationDetails);
  }
}

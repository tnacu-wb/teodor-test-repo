package uk.co.whitbread.ohip.infrastructure.rest.client.eckoh;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPaymentMethodType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsDetailsReservations;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.eckoh.in.CardScheme;
import uk.co.whitbread.ohip.domain.model.eckoh.in.EckohCardType;
import uk.co.whitbread.ohip.domain.model.eckoh.in.EckohWebhook;
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlag;
import uk.co.whitbread.ohip.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.exceptions.EckohException;
import uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.mapper.EckohChangeRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.ohip.EckohOhipClient;

@ExtendWith(MockitoExtension.class)
class EckohOutPortImplTest {

  @Mock
  private EckohOhipClient eckohOhipClient;

  @Mock
  private EckohChangeRequestMapper eckohChangeRequestMapper;

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @InjectMocks
  private EckohOutPortImpl eckohOutPort;

  @Test
  void eckohWebhook_shouldReturnOk() {
    //Arrange
    var eckohWebhookRequest = createEckohWebhookRequest("1226", CardScheme.VISA);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getPibaBooking()).thenReturn(mock(FeatureFlag.Feature.class));
    when(eckohOhipClient.getReservationsByBlockId(eckohWebhookRequest.getReference())).thenReturn(
        createReservationDetails("Reservation"));
    when(eckohChangeRequestMapper.toChangeReservationModel(any())).thenReturn(
        createChangeReservation());
    //Act
    eckohOutPort.eckohWebhook(eckohWebhookRequest);

    //Assert
    verifyNoMoreInteractions(eckohChangeRequestMapper);
  }

  @Test
  void eckohWebhook_wrongExpiryDate_shouldThrowError() {
    //Arrange

    var eckohWebhookRequest = createEckohWebhookRequest("12/26", CardScheme.VISA);
    String error = "Unable to format: 12/26";
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getPibaBooking()).thenReturn(mock(FeatureFlag.Feature.class));
    when(eckohOhipClient.getReservationsByBlockId(eckohWebhookRequest.getReference())).thenReturn(
        createReservationDetails("Reservation"));

    //Act
    EckohException exception = Assertions
        .assertThrows(EckohException.class, () -> {
          eckohOutPort.eckohWebhook(eckohWebhookRequest);
        });

    //Assert
    assertTrue(exception.getMessage().contains(error));

  }

  @Test
  void eckohWebhook_nullReservationId_shouldThrowError() {
    //Arrange
    String error = "Unable to get reservationId";
    var eckohWebhookRequest = createEckohWebhookRequest("12/26", CardScheme.VISA);
    when(eckohOhipClient.getReservationsByBlockId(eckohWebhookRequest.getReference())).thenReturn(
        createReservationDetails("Confirmation"));

    //Act
    EckohException exception = Assertions
        .assertThrows(EckohException.class, () -> {
          eckohOutPort.eckohWebhook(eckohWebhookRequest);
        });

    //Assert
    assertEquals(exception.getMessage(), error);

  }

  private ReservationsDetails createReservationDetails(String reservationType) {
    var reservationsDetails = new ReservationsDetails();
    var reservationsDetailsReservations = new ReservationsDetailsReservations();
    var reservationInfoType = new ReservationInfoType();
    var uniqueIDType = new UniqueIDType();
    var reservationPaymentMethodType = new ReservationPaymentMethodType();
    var resPaymentCardType = new ResPaymentCardType();
    uniqueIDType.setType(reservationType);
    uniqueIDType.setId("4353535");
    resPaymentCardType.setCardType(CardTypeType.VA);
    reservationPaymentMethodType.setPaymentMethod("VA");
    reservationPaymentMethodType.setPaymentCard(resPaymentCardType);
    reservationInfoType.setReservationIdList(List.of(uniqueIDType));
    reservationInfoType.setPaymentMethod("VA");
    reservationInfoType.setReservationPaymentMethod(reservationPaymentMethodType);
    reservationsDetailsReservations.setReservationInfo(List.of(reservationInfoType));
    reservationsDetails.setReservations(reservationsDetailsReservations);
    return reservationsDetails;
  }

  private ChangeReservation createChangeReservation() {
    var changeReservation = new ChangeReservation();
    var hotelReservationInstructionType = new HotelReservationInstructionType();
    var uniqueIDType = new UniqueIDType();
    uniqueIDType.setType("Reservation");
    uniqueIDType.setId("4353535");
    hotelReservationInstructionType.setHotelId("BERALX");
    hotelReservationInstructionType.setReservationIdList(List.of(uniqueIDType));
    var reservationPaymentMethodType = new ReservationPaymentMethodType();
    var resPaymentCardType = new ResPaymentCardType();
    resPaymentCardType.setCardType(CardTypeType.VA);
    reservationPaymentMethodType.setPaymentMethod("VA");
    reservationPaymentMethodType.setPaymentCard(resPaymentCardType);
    hotelReservationInstructionType.setReservationPaymentMethods(
        List.of(reservationPaymentMethodType));
    changeReservation.reservations(List.of(hotelReservationInstructionType));
    return changeReservation;
  }

  private EckohWebhook createEckohWebhookRequest(String expiry, CardScheme scheme) {
    return EckohWebhook.builder()
        .result("succes")
        .resultCode(100)
        .maskedPan("444433XXXXXX1111")
        .expiry(expiry)
        .scheme(scheme.name())
        .type(EckohCardType.CREDIT)
        .token("4216333880397891103")
        .reference("15951")
        .build();
  }
}

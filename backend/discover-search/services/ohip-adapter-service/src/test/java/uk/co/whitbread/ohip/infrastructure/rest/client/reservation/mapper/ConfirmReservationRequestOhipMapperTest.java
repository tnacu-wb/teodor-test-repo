package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardNumberTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardProcessingType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardTypeType;
import uk.co.whitbread.ohip.domain.model.reservation.in.ConfirmReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.PaymentCard;
import uk.co.whitbread.ohip.domain.model.reservation.in.PaymentOption;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.properties.ReservationOhipProperties;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {PaymentMethodOhipMapperImpl.class,
    ConfirmReservationOhipMapperImpl.class,
    ReservationOhipProperties.class, ConfirmReservationRequestOhipMapperImpl.class})
class ConfirmReservationRequestOhipMapperTest {

  @Autowired
  ConfirmReservationRequestOhipMapper confirmReservationRequestMapper;

  @Test
  void createRequestToConfirmReservation__ShouldReturnOK() {
    //Arrange
    ConfirmReservationRequest confirmReservationRequest = createRequest("VA", "VA");

    //Act
    var confirmationRequest = confirmReservationRequestMapper.toChangeReservationModel(
        confirmReservationRequest);

    //Assert
    assertEquals("LONEUS", confirmationRequest.getReservations().get(0).getHotelId());
    assertEquals("VA", confirmationRequest.getReservations().get(0).getReservationPaymentMethods()
        .get(0).getPaymentMethod());
    assertEquals(1, confirmationRequest.getReservations().get(0).getReservationPaymentMethods()
        .get(0).getFolioView());
    assertEquals(CardTypeType.VA,
        confirmationRequest.getReservations().get(0).getReservationPaymentMethods()
            .get(0).getPaymentCard().getCardType());
    assertEquals(CardProcessingType.MANUAL.getValue(),
        confirmationRequest.getReservations().get(0).getReservationPaymentMethods()
            .get(0).getPaymentCard().getProcessing().getValue());
    assertEquals("4111111111111111",
        confirmationRequest.getReservations().get(0).getReservationPaymentMethods()
            .get(0).getPaymentCard().getCardNumber());
    assertEquals(CardNumberTypeType.TOKEN.getValue(),
        confirmationRequest.getReservations().get(0).getReservationPaymentMethods()
            .get(0).getPaymentCard().getCardOrToken().getValue());
    assertEquals("CC", confirmationRequest.getReservations().get(0).getRoomStay().getGuarantee()
        .getGuaranteeCode());
  }

  @Test
  void createRequestToConfirmReservation__PibaShouldReturnOK() {
    //Arrange
    ConfirmReservationRequest confirmReservationRequest = createRequest("ZZ", "BU");

    //Act
    var confirmationRequest = confirmReservationRequestMapper.toChangeReservationModel(
        confirmReservationRequest);

    //Assert
    assertEquals("LONEUS", confirmationRequest.getReservations().get(0).getHotelId());
    assertEquals("BU", confirmationRequest.getReservations().get(0).getReservationPaymentMethods()
        .get(0).getPaymentMethod());
    assertEquals(1, confirmationRequest.getReservations().get(0).getReservationPaymentMethods()
        .get(0).getFolioView());
    assertEquals(CardTypeType.ZZ,
        confirmationRequest.getReservations().get(0).getReservationPaymentMethods()
            .get(0).getPaymentCard().getCardType());
    assertEquals(CardProcessingType.MANUAL.getValue(),
        confirmationRequest.getReservations().get(0).getReservationPaymentMethods()
            .get(0).getPaymentCard().getProcessing().getValue());
    assertEquals("4111111111111111",
        confirmationRequest.getReservations().get(0).getReservationPaymentMethods()
            .get(0).getPaymentCard().getCardNumber());
    assertEquals(CardNumberTypeType.TOKEN.getValue(),
        confirmationRequest.getReservations().get(0).getReservationPaymentMethods()
            .get(0).getPaymentCard().getCardOrToken().getValue());
    assertEquals("CC", confirmationRequest.getReservations().get(0).getRoomStay().getGuarantee()
        .getGuaranteeCode());
  }

  private ConfirmReservationRequest createRequest(String cardType, String paymentType) {
    var paymentCard = PaymentCard.builder()
        .cardType(cardType)
        .token("4111111111111111")
        .expirationDate("2025-03-31")
        .cardNumberLast4Digits("1234")
        .cardHolderName("Test")
        .build();
    return ConfirmReservationRequest.builder()
        .reservationId("34865")
        .hotelId("LONEUS")
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .paymentCard(paymentCard)
        .paymentType(paymentType)
        .build();
  }

}

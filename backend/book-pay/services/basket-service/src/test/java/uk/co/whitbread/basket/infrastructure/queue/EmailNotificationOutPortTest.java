package uk.co.whitbread.basket.infrastructure.queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.logic.config.DistributionProperties;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketItem;
import uk.co.whitbread.basket.domain.model.email.out.EmailNotificationEventType;
import uk.co.whitbread.basket.domain.model.email.out.TransactionData;
import uk.co.whitbread.basket.generated.models.reservation.ReservationByBasketRefRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.ReservationByBasketRefResponseDto;
import uk.co.whitbread.basket.generated.models.reservation.ReservationByIdDto;
import uk.co.whitbread.basket.generated.models.reservation.ReservationEmailNotificationsDto;
import uk.co.whitbread.basket.generated.models.reservation.RoomStayByIdDto;
import uk.co.whitbread.basket.infrastructure.queue.exception.BasketOrderException;
import uk.co.whitbread.basket.infrastructure.queue.model.EmailNotificationEvent;
import uk.co.whitbread.basket.infrastructure.queue.producer.EmailNotificationProducer;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.service.ReservationClient;

@ExtendWith(MockitoExtension.class)
class EmailNotificationOutPortTest {

    @Mock
    private EmailNotificationProducer emailNotificationProducer;

    @Mock
    private ReservationClient reservationClient;

    @InjectMocks
    private EmailNotificationOutPortImpl emailNotificationOutPort;

    @Mock
    private DistributionProperties distributionProperties;

  @Test
  void testSendNotificationEvent_BasketOrderException() {
    //Arrange
    var basket = new Basket();
    var transactionData = new TransactionData();
    var distribution = distributionProperties.getMailSuppressionSorceCodes();
    //Act
    var exception = assertThrows(BasketOrderException.class,
        () -> emailNotificationOutPort
            .sendEmailNotificationEvent(basket, EmailNotificationEventType.CONFIRM,
                "test@gmail.com", transactionData, distribution));
    //Assert
    String actualMessage = exception.getMessage();
    assertEquals("No items to process", actualMessage);
  }

  @Test
    void testSendNotificationEventForPIAndCCUI() {
        var basketItems = Collections.singletonList(BasketItem.builder().sourceId("30000").type("STAY").sourceId("OPERA").build());
        var basket = Basket.builder().reference("MAN12345").hotelId("LONEUS").channel("PI").sendMail(true).items(basketItems).build();

        emailNotificationOutPort.sendEmailNotificationEvent(basket, EmailNotificationEventType.CONFIRM, "test@gmail.com",
            new TransactionData(), distributionProperties.getMailSuppressionSorceCodes());

        verify(emailNotificationProducer, times(1)).sendEmailNotificationEvent(any(EmailNotificationEvent.class));
    }

    @Test
    void testSendNotificationEventForDistribution() {

        var basketItems = Collections.singletonList(BasketItem.builder().sourceId("30000").type("STAY").sourceId("OPERA").build());
        var basket = Basket.builder().reference("MAN12345").hotelId("LONEUS").channel("DISTR").sendMail(true).items(basketItems).build();
        var mailSuppressionSorceCodes = List.of("38", "43");

        var reservationByBasketRefResponseDto = mockReservationByBasketRefResponseDto("35", false);
      var requestDto = new ReservationByBasketRefRequestDto();
      requestDto.setPriceBreakDownNeeded("false");
      requestDto.setRateInfoNeeded(false);
        when(reservationClient.getReservationsByBasketReference(basket.getBasketId(),
            requestDto)).thenReturn(reservationByBasketRefResponseDto);

        emailNotificationOutPort.sendEmailNotificationEvent(basket, EmailNotificationEventType.CONFIRM, "test@gmail.com",
            new TransactionData(), mailSuppressionSorceCodes);

        verify(emailNotificationProducer, times(1)).sendEmailNotificationEvent(any(EmailNotificationEvent.class));
    }

    @Test
    void testSendNotificationEventForDistributionGdsRequest() {
      var basketItems = Collections.singletonList(BasketItem.builder().sourceId("30000").type("STAY").sourceId("OPERA").build());
      var basket = Basket.builder().reference("MAN12345").hotelId("LONEUS").channel("DISTR").subChannel("AMADEUS").sendMail(true).items(basketItems).build();
      var mailSuppressionSorceCodes = List.of("38", "43");

      var reservationByBasketRefResponseDto = mockReservationByBasketRefResponseDto("35", false);
      var requestDto = new ReservationByBasketRefRequestDto();
      requestDto.setPriceBreakDownNeeded("false");
      requestDto.setRateInfoNeeded(false);
      when(reservationClient.getReservationsByBasketReference(basket.getBasketId(),
          requestDto)).thenReturn(reservationByBasketRefResponseDto);

      emailNotificationOutPort.sendEmailNotificationEvent(basket, EmailNotificationEventType.CONFIRM, "test@gmail.com",
          new TransactionData(), mailSuppressionSorceCodes);

      verify(emailNotificationProducer, times(1)).sendEmailNotificationEvent(any(EmailNotificationEvent.class));
    }

    @Test
    void testSendNotificationEventForDistribution_mailSuppression() {

        var basketItems = Collections.singletonList(BasketItem.builder().sourceId("30000").type("STAY").sourceId("OPERA").build());
        var basket = Basket.builder().reference("MAN12345").hotelId("LONEUS").channel("DISTR").sendMail(true).items(basketItems).build();
        var mailSuppressionSorceCodes = List.of("38", "43");

        var reservationByBasketRefResponseDto = mockReservationByBasketRefResponseDto("38", true);
      var requestDto = new ReservationByBasketRefRequestDto();
      requestDto.setPriceBreakDownNeeded("false");
      requestDto.setRateInfoNeeded(false);
        when(reservationClient.getReservationsByBasketReference(basket.getBasketId(),
            requestDto)).thenReturn(reservationByBasketRefResponseDto);

        emailNotificationOutPort.sendEmailNotificationEvent(basket, EmailNotificationEventType.CONFIRM, "test@gmail.com",
            new TransactionData(), mailSuppressionSorceCodes);

        verify(emailNotificationProducer, times(1)).sendEmailNotificationEvent(any(EmailNotificationEvent.class));
    }

    private ReservationByBasketRefResponseDto mockReservationByBasketRefResponseDto(String sourceCode, boolean emailFlag){
        var reservationResponse = new ReservationByBasketRefResponseDto();
        var reservation = new ReservationByIdDto();
        var roomStay = new RoomStayByIdDto();
        roomStay.setSourceCode(sourceCode);
        reservation.setRoomStay(roomStay);
        var notifications = new ReservationEmailNotificationsDto();
        reservation.setReservationEmailNotifications(notifications);
        notifications.setSendEmailConfirmation(emailFlag);
        reservationResponse.setReservationByIdList(List.of(reservation));
        return reservationResponse;
    }
}

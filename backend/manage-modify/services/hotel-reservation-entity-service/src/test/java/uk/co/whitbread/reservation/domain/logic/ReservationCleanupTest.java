package uk.co.whitbread.reservation.domain.logic;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
import uk.co.whitbread.reservation.domain.exceptions.GenericBadRequestException;
import uk.co.whitbread.reservation.domain.model.in.PaymentOption;
import uk.co.whitbread.reservation.domain.model.out.BasketItemResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.HotelReservationOhipOutPort;
import uk.co.whitbread.reservation.domain.exceptions.BasketNotFoundException;

@ExtendWith(MockitoExtension.class)
class ReservationCleanupTest {

  @Mock
  private BasketOutPort basketOutPort;

  @Mock
  private HotelReservationOhipOutPort hotelReservationOhipOutPort;

  @InjectMocks
  private ReservationCleanupImpl reservationCleanup;

  @Test
  void reservationCleanup_SuccessDeleteBasket() {
    // Arrange
    String basketRef = "basketRef";
    String eTag = "1679667965000";

    BasketResponse basket = getMockBasketResponse(basketRef, eTag);

    doNothing().when(hotelReservationOhipOutPort)
        .deleteReservation(basket.getHotelId(), "111111");
    doNothing().when(hotelReservationOhipOutPort)
        .deleteReservation(basket.getHotelId(), "333333");
    when(basketOutPort.removeItem(eq(basketRef), anyString(), anyString())).thenReturn(basket);

    doNothing().when(basketOutPort).deleteBasket(eq(basketRef), anyString());

    // Act
    reservationCleanup.cleanupTempBasket(basket);

    // Assert
    verify(basketOutPort).deleteBasket(basketRef, eTag);

  }

  @Test
  void reservationCleanup_SuccessDeleteBasketWithAmendedStatus() {
    // Arrange
    String basketRef = "basketRef";
    String eTag = "1679667965000";

    BasketResponse basket = getMockBasketResponse(basketRef, eTag);
    basket.setStatus("AMENDED");

    doNothing().when(hotelReservationOhipOutPort)
        .deleteReservation(basket.getHotelId(), "111111");
    doNothing().when(hotelReservationOhipOutPort)
        .deleteReservation(basket.getHotelId(), "333333");
    when(basketOutPort.removeItem(eq(basketRef), anyString(), anyString())).thenReturn(basket);

    doNothing().when(basketOutPort).deleteBasket(eq(basketRef), anyString());

    // Act
    reservationCleanup.cleanupTempBasket(basket);

    // Assert
    verify(basketOutPort).deleteBasket(basketRef, eTag);

  }


  @Test
  void reservationCleanup_BasketStatusNotOpenOrAmended_ShouldThrowBadRequestsException() {
    // Arrange
    String basketRef = "basketRef";
    String eTag = "1679667965000";

    BasketResponse basket = getMockBasketResponse(basketRef, eTag);
    basket.setStatus("PAY_PENDING");
    // Act & Assert
    assertThrows(GenericBadRequestException.class,
        () -> reservationCleanup.cleanupTempBasket(basket));

  }

  @Test
  void reservationCleanup_BasketStatusNotOpenOrAmended_ShouldThrowBasketException() {
    // Arrange
    String basketRef = "basketRef";
    String eTag = "1679667965000";

    BasketResponse basket = getMockBasketResponse(basketRef, eTag);

    doNothing().when(hotelReservationOhipOutPort)
        .deleteReservation(basket.getHotelId(), "111111");
    doNothing().when(hotelReservationOhipOutPort)
        .deleteReservation(basket.getHotelId(), "333333");
    when(basketOutPort.removeItem(eq(basketRef), anyString(), anyString())).thenReturn(basket);
    doThrow(BasketNotFoundException.class).when(basketOutPort)
        .deleteBasket(eq(basketRef), anyString());

    // Act & Assert
    assertThrows(BasketNotFoundException.class,
        () -> reservationCleanup.cleanupTempBasket(basket));

  }

  @Test
  void reservationCleanup_SuccessDeleteBasketByReference() {
    // Arrange
    String basketRef = "basketRef";
    String eTag = "1679667965000";

    BasketResponse basket = getMockBasketResponse(basketRef, eTag);

    when(basketOutPort.getBasketById(eq(basketRef))).thenReturn(basket);

    doNothing().when(hotelReservationOhipOutPort)
        .deleteReservation(basket.getHotelId(), "111111");
    doNothing().when(hotelReservationOhipOutPort)
        .deleteReservation(basket.getHotelId(), "333333");
    when(basketOutPort.removeItem(eq(basketRef), anyString(), anyString())).thenReturn(basket);

    doNothing().when(basketOutPort).deleteBasket(eq(basketRef), anyString());

    // Act
    reservationCleanup.cleanupTempBasket(basketRef);

    // Assert
    verify(basketOutPort).deleteBasket(basketRef, eTag);

  }

  @Test
  void originalBasketCleanup_Success() {
    // Arrange
    String basketRef = "basketRef";
    String eTag = "1679667965000";

    BasketResponse basket = getMockBasketResponse(basketRef, eTag);

    when(basketOutPort.removeItems(eq(basketRef), anyList(), anyString())).thenReturn(basket);

    // Act
    reservationCleanup.cleanupOriginalBasket(basket, List.of("333333"));

    // Assert
    verify(basketOutPort, times(1)).removeItems(any(), anyList(), any());
  }

  @Test
  void originalBasketCleanup_noItemsToClean() {
    // Arrange
    String basketRef = "basketRef";
    String eTag = "1679667965000";

    BasketResponse basket = getMockBasketResponse(basketRef, eTag);

    // Act
    reservationCleanup.cleanupOriginalBasket(basket, Collections.emptyList());

    // Assert
    verify(basketOutPort, times(0)).removeItem(any(), anyString(), any());
  }

  private BasketResponse getMockBasketResponse(String basketRef, String eTag) {
    return BasketResponse.builder()
        .bookingReference(basketRef)
        .reference(basketRef)
        .status("OPEN")
        .hotelId("hotelTest")
        .items(List.of(BasketItemResponse.builder().sourceId("111111").build(),
            BasketItemResponse.builder().sourceId("333333").build()))
        .eTag(eTag)
        .paymentOption(PaymentOption.PAY_NOW)
        .build();
  }
}


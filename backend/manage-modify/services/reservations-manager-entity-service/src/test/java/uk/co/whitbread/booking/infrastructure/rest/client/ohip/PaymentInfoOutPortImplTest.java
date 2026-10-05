package uk.co.whitbread.booking.infrastructure.rest.client.ohip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.booking.domain.model.history.out.Booking;
import uk.co.whitbread.booking.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.exceptions.HotelReservationOhipException;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.ReservationInfoPaymentTypeDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.ReservationPaymentCardTypeDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.UniqueIdTypeDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.service.OhipAdapterClient;

@ExtendWith(MockitoExtension.class)
class PaymentInfoOutPortImplTest {

  @Mock
  private OhipAdapterClient ohipAdapterClient;

  @Mock
  private BasketOutPort basketOutPort;

  @InjectMocks
  private PaymentInfoOutPortImpl paymentInfoOutPort;

  @ParameterizedTest
  @CsvSource({
      "false,false,0",
      "true,false,1",
      "false,true,1",
      "true,true,2"
  })
  void testGetPibaCpReservations_withValidBookings_returnsBookingRefs(boolean excludePibaCP,
      boolean excludePibaCNP, int expectedSize) {
    // Arrange
    var bookings = List.of(
        mockBooking("hotel1", "bookingRef1"),
        mockBooking("hotel2", "bookingRef2")
    );
    var baskets = java.util.Map.of(
        "bookingRef1", java.util.Set.of("sourceIdA", "sourceIdB"),
        "bookingRef2", java.util.Set.of("sourceIdC", "sourceIdD")
    );
    if (excludePibaCNP || excludePibaCP) {
      when(basketOutPort.getBasketSourceIdsForBookingRefs(any())).thenReturn(baskets);
      var paymentsDto = List.of(
          new ReservationInfoPaymentTypeDto(
              List.of(UniqueIdTypeDto.builder().id("sourceIdA").type("Reservation").build()),
              ReservationPaymentCardTypeDto.builder().paymentMethod("BU").folioView(1).build()),
          new ReservationInfoPaymentTypeDto(
              List.of(UniqueIdTypeDto.builder().id("sourceIdC").type("Reservation").build()),
              ReservationPaymentCardTypeDto.builder().paymentMethod("BD").folioView(2).build())
      );
      when(ohipAdapterClient.getReservationsPaymentTypeByReservationIds(any(), any())).thenReturn(
          paymentsDto);
    }
    //Act
    var result = paymentInfoOutPort.getPibaCpReservations(bookings, excludePibaCP, excludePibaCNP);

    // Assert
    Assertions.assertEquals(expectedSize, result.size());
    if (excludePibaCP) {
      assertTrue(result.contains("bookingRef1"));
    }
    if (excludePibaCNP) {
      assertTrue(result.contains("bookingRef2"));
    }
  }

  @ParameterizedTest
  @CsvSource({
      "false,false,0",
      "true,false,2",
      "false,true,2",
      "true,true,4"
  })
  void getReservationPaymentTypeInfo_ShouldReturnOK(boolean excludePibaCP,
      boolean excludePibaCNP, int size) {
    //Arrange
    var refBooking1 = mockBooking("HEAPTI", "refBooking1");
    var refBooking2 = mockBooking("HEAPTI", "refBooking2");
    var refBooking3 = mockBooking("LONEUS", "refBooking3");
    var refBooking4 = mockBooking("GATGAT", "refBooking4");
    var refBooking5 = mockBooking("HEAPTI", "refBooking5");
    var refBooking6 = mockBooking("HEAPTI", "refBooking6");
    var refBooking7 = mockBooking("LONEUS", "refBooking7");
    var refBooking8 = mockBooking("GATGAT", "refBooking8");
    List<UniqueIdTypeDto> ids1 = List.of(
        UniqueIdTypeDto.builder().type("Reservation").id("sourceId1").build(),
        UniqueIdTypeDto.builder().type("Reservation").id("sourceId11").build());
    List<UniqueIdTypeDto> ids2 = List.of(
        UniqueIdTypeDto.builder().type("Reservation").id("sourceId2").build());
    List<UniqueIdTypeDto> ids3 = List.of(
        UniqueIdTypeDto.builder().type("Reservation").id("sourceId3").build());
    List<UniqueIdTypeDto> ids4 = List.of(
        UniqueIdTypeDto.builder().type("Confirmation").id("sourceId4").build());
    List<UniqueIdTypeDto> ids5 = List.of(
        UniqueIdTypeDto.builder().type("Reservation").id("sourceId5").build(),
        UniqueIdTypeDto.builder().type("Reservation").id("sourceId55").build());
    List<UniqueIdTypeDto> ids6 = List.of(
        UniqueIdTypeDto.builder().type("Reservation").id("sourceId6").build());
    List<UniqueIdTypeDto> ids7 = List.of(
        UniqueIdTypeDto.builder().type("Reservation").id("sourceId7").build());
    List<UniqueIdTypeDto> ids8 = List.of(
        UniqueIdTypeDto.builder().type("Confirmation").id("sourceId8").build());
    List<ReservationInfoPaymentTypeDto> list = List.of(
        new ReservationInfoPaymentTypeDto(ids1,
            ReservationPaymentCardTypeDto.builder().paymentMethod("BD").folioView(1).build()),
        new ReservationInfoPaymentTypeDto(ids2,
            ReservationPaymentCardTypeDto.builder().paymentMethod("BU").folioView(1).build()),
        new ReservationInfoPaymentTypeDto(ids3,
            ReservationPaymentCardTypeDto.builder().paymentMethod("CA").folioView(1).build()),
        new ReservationInfoPaymentTypeDto(ids4,
            ReservationPaymentCardTypeDto.builder().paymentMethod("BD").folioView(1).build()),
        new ReservationInfoPaymentTypeDto(ids5,
            ReservationPaymentCardTypeDto.builder().paymentMethod("BD").folioView(2).build()),
        new ReservationInfoPaymentTypeDto(ids6,
            ReservationPaymentCardTypeDto.builder().paymentMethod("BU").folioView(2).build()),
        new ReservationInfoPaymentTypeDto(ids7,
            ReservationPaymentCardTypeDto.builder().paymentMethod("CA").folioView(2).build()),
        new ReservationInfoPaymentTypeDto(ids8,
            ReservationPaymentCardTypeDto.builder().paymentMethod("BD").folioView(2).build())
    );
    AtomicInteger i = new AtomicInteger(-1);
    if (excludePibaCP || excludePibaCNP) {
      when(ohipAdapterClient.getReservationsPaymentTypeByReservationIds(any(), any()))
          .thenReturn(list);

      when(ohipAdapterClient.getReservationsPaymentTypeByReservationIds(any(), any()))
          .thenAnswer(invocation -> {
            i.getAndIncrement();
            return List.of(list.get(i.get()));
          });

      var baskets = java.util.Map.of(
          "refBooking1", java.util.Set.of("sourceId1", "sourceId11"),
          "refBooking2", java.util.Set.of("sourceId2"),
          "refBooking3", java.util.Set.of("sourceId3"),
          "refBooking4", java.util.Set.of("sourceId4"),
          "refBooking5", java.util.Set.of("sourceId5", "sourceId55"),
          "refBooking6", java.util.Set.of("sourceId6"),
          "refBooking7", java.util.Set.of("sourceId7"),
          "refBooking8", java.util.Set.of("sourceId8")
      );
      when(basketOutPort.getBasketSourceIdsForBookingRefs(any())).thenReturn(baskets);
    }
    var request = List.of(refBooking1, refBooking2, refBooking3, refBooking4, refBooking5,
        refBooking6, refBooking7, refBooking8);

    //Act
    var result = paymentInfoOutPort.getPibaCpReservations(request, excludePibaCP, excludePibaCNP);

    //Assert
    assertNotNull(result);
    assertEquals(size, result.size());
    if (size > 0) {
      assertEquals(7, i.get());
    }
    if (excludePibaCP) {
      assertTrue(result.contains("refBooking1"));
      assertTrue(result.contains("refBooking2"));
    }
    if (excludePibaCNP) {
      assertTrue(result.contains("refBooking5"));
      assertTrue(result.contains("refBooking6"));
    }
  }

  @Test
  void getReservationPaymentTypeInfo_ShouldThrowException() {
    // Arrange
    var refBooking1 = mockBooking("HEAPTI", "refBooking1");
    var refBooking2 = mockBooking("HEAPTI", "refBooking2");
    var refBooking3 = mockBooking("LONEUS", "refBooking3");
    var baskets = java.util.Map.of(
        "refBooking1", java.util.Set.of("sourceId1", "sourceId11"),
        "refBooking2", java.util.Set.of("sourceId2"),
        "refBooking3", java.util.Set.of("sourceId3"),
        "refBooking4", java.util.Set.of("sourceId4")
    );
    when(basketOutPort.getBasketSourceIdsForBookingRefs(any())).thenReturn(baskets);
    when(ohipAdapterClient.getReservationsPaymentTypeByReservationIds(any(), any()))
        .thenAnswer(invocation -> {
          throw new HotelReservationOhipException("error", "ohip error", new Throwable(), 1);
        });
    var request = List.of(refBooking1, refBooking2, refBooking3);
    //Act
    var exception = Assertions.assertThrows(HotelReservationOhipException.class,
        () -> paymentInfoOutPort.getPibaCpReservations(request, true, false));
    //Assert
    assertEquals("ohip error", exception.getMessage());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void testGetPibaCpReservations_withNullOrEmptyBookings_returnsEmptyList(
      List<Booking> bookings) {
    //Act
    var result = paymentInfoOutPort.getPibaCpReservations(bookings, true, false);
    // Assert
    assertTrue(result.isEmpty());
  }

  @ParameterizedTest
  @CsvSource({
      "false,false",
      "true,false",
      "false,true",
      "true,true"
  })
  void testGetPibaCpReservations_whenBasketSourceIdsForBookingRefsEmpty_returnsEmptyList(
      boolean excludePibaCP,
      boolean excludePibaCNP) {
    // Arrange
    var bookings = List.of(mockBooking("hotelCode", "bookingRef1"));
    if (excludePibaCP || excludePibaCNP) {
      Map<String, Set<String>> baskets = java.util.Map.of();
      when(basketOutPort.getBasketSourceIdsForBookingRefs(any())).thenReturn(baskets);
    }
    // Act
    var result = paymentInfoOutPort.getPibaCpReservations(bookings, excludePibaCP, excludePibaCNP);
    // Assert
    assertTrue(result.isEmpty());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void testGetPibaCpReservations_whenPibaCnpPaymentTypesEmpty_returnsEmptyList(boolean returnNull) {
    // Arrange
    var bookings = List.of(mockBooking("hotel1", "bookingRef1"));
    var baskets = java.util.Map.of("bookingRef1", java.util.Set.of("sourceIdA"));
    when(basketOutPort.getBasketSourceIdsForBookingRefs(any())).thenReturn(baskets);
    when(ohipAdapterClient.getReservationsPaymentTypeByReservationIds(any(), any()))
        .thenReturn(returnNull ? null : java.util.Collections.emptyList());
    // Act
    var result = paymentInfoOutPort.getPibaCpReservations(bookings, true, false);
    // Assert
    assertTrue(result.isEmpty());
  }

  @ParameterizedTest
  @CsvSource({"1, BU, true", "2, BU, false", "1, CU, false", "2, CU, false", "1,, false"})
  void testInvertBasketMap_withNullSourceIdsOrFolioWindow(Integer folioWindow, String methodType,
      Boolean expected) {
    // Arrange
    var bookings = List.of(mockBooking("hotel1", "bookingRef1"),
        mockBooking("hotel2", "bookingRef2"));
    var baskets = new HashMap<String, Set<String>>();
    baskets.put("bookingRef1", null);
    baskets.put("bookingRef2", Set.of("sourceIdB", "sourceIdC"));
    when(basketOutPort.getBasketSourceIdsForBookingRefs(any())).thenReturn(baskets);
    var ids = List.of(
        UniqueIdTypeDto.builder().id("sourceIdB").type("Reservation").build(),
        UniqueIdTypeDto.builder().id("sourceIdC").type("Reservation").build()
    );
    var paymentsDto = List.of(
        new ReservationInfoPaymentTypeDto(ids,
            ReservationPaymentCardTypeDto.builder().paymentMethod(methodType).folioView(folioWindow)
                .build())
    );
    when(ohipAdapterClient.getReservationsPaymentTypeByReservationIds(any(), any())).thenReturn(
        paymentsDto);
    // Act
    var result = paymentInfoOutPort.getPibaCpReservations(bookings, true, false);
    // Assert
    assertFalse(result.contains("bookingRef1"));
    if (expected) {
      assertTrue(result.contains("bookingRef2"));
    } else {
      assertFalse(result.contains("bookingRef2"));
    }
  }

  private Booking mockBooking(String hotelCode, String bookingRef) {
    var booking = new Booking();
    booking.setHotelCode(hotelCode);
    booking.setBookingReference(bookingRef);
    return booking;
  }

}
package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import uk.co.whitbread.reservation.domain.model.cache.HotelReservationCache;
import uk.co.whitbread.reservation.domain.model.in.Reservation;
import uk.co.whitbread.reservation.domain.model.in.ReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.RoomRate;

@ExtendWith(MockitoExtension.class)
class CacheReservationResponseHelperTest {

  @Mock
  private CacheManager cacheManager;

  @Mock
  private Cache cache;

  private CacheReservationResponseHelper helper;

  @BeforeEach
  void before() {
    helper = new CacheReservationResponseHelper(cacheManager);
  }

  @Test
  void cacheReservationRequest_shouldPutHotelReservationCacheInCache() {
    // Arrange
    when(cacheManager.getCache("ReservationCache")).thenReturn(cache);

    Reservation reservation = mock(Reservation.class);
    when(reservation.getAdultsNumber()).thenReturn(2);
    when(reservation.getChildrenNumber()).thenReturn(1);
    when(reservation.getArrival()).thenReturn("2024-06-01");
    when(reservation.getDeparture()).thenReturn("2024-06-05");
    when(reservation.getRoomRates()).thenReturn(mock(RoomRate.class));
    when(reservation.getRoomRates().getRatePlanCode()).thenReturn("FLEXRATE");

    ReservationRequest request = mock(ReservationRequest.class);
    when(request.getReservations()).thenReturn(List.of(reservation));
    when(request.getBookingFlowId()).thenReturn("booking-a1");

    String basketReference = "AQN-bbb26a76-6489-485e-b981-b5498035e34e";
    String hotelId = "LONEUS";

    // Act
    helper.cacheReservationRequest(request, basketReference, hotelId);

    // Assert
    verify(cache, times(1)).put(eq(basketReference), any(HotelReservationCache.class));
  }

  @Test
  void cacheReservationRequest_shouldThrowExceptionForEmptyReservations() {
    ReservationRequest request = mock(ReservationRequest.class);
    when(request.getReservations()).thenReturn(List.of());

    assertThrows(IllegalArgumentException.class, () ->
        helper.cacheReservationRequest(request, "AQN-bbb26a76-6489-485e-b981-b5498035e34e", "LONEUS")
    );
  }

}

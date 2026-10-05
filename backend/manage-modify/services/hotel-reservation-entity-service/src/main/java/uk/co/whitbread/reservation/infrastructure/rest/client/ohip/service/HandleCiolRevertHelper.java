package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationAlertsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationByBasketRefResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationByIdDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto.StatusEnum;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ChangeBasketStatusDto;
import uk.co.whitbread.reservation.domain.model.in.Alert;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationAlertsRequest;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.service.BasketClient;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@RequiredArgsConstructor
@Slf4j
@Component
public class HandleCiolRevertHelper {

  public static final String CIOL_RC_FAILED = "CIOL_RC_FAILED";
  public static final String ALERT_CIOL_CODE = "CIOL";
  private final OhipAdapterClient ohipAdapterClient;
  private final BasketClient basketClient;
  private final ConcurrentTracer concurrentTracer;


  public void initiateCiolRevert(List<String> reservationIds, String hotelId) {
    CompletableFuture.runAsync(concurrentTracer.wrap(
        () -> revertCiol(reservationIds, hotelId))
    );
  }

  protected void revertCiol(List<String> reservationIds, String hotelId) {

    var booking = ohipAdapterClient.sendGetReservationsByIds(hotelId,
        reservationIds, false, false, false);
    if (Objects.nonNull(booking) && Objects.nonNull(booking.getReservationByIdList())
        && Objects.nonNull(booking.getBookingReference())) {

      var updatedBasket = updateBasketStatus(booking.getBookingReference());
      var isBasketStatusUpdated = StatusEnum.CIOL_RC_FAILED.equals(updatedBasket.getStatus());
      log.warn("CIOL revert booking:{}, basket status updated:{}",
          booking.getBookingReference(),
          isBasketStatusUpdated);
      deleteCiolAlerts(updatedBasket, booking, hotelId);
    } else {
      log.warn("CIOL revert incomplete booking data for reservation ids:{}", reservationIds);
    }
  }

  private BasketDto updateBasketStatus(String bookingRef) {

    var basket = basketClient.sendGetBasketByReference(bookingRef);
    var response = Objects.nonNull(basket) ? basket.getBody() : null;

    if (Objects.nonNull(response) && StatusEnum.PRE_CHECKED_IN.equals(response.getStatus())) {
      log.warn("CIOL revert updating basket status:{}", bookingRef);
      return basketClient.changeStatus(bookingRef,
          createCiolFailedBaksetRequest());
    }

    return new BasketDto();
  }

  private ChangeBasketStatusDto createCiolFailedBaksetRequest() {
    var request = new ChangeBasketStatusDto();
    request.setStatus(CIOL_RC_FAILED);
    return request;
  }

  private void deleteCiolAlerts(BasketDto basketDto, ReservationByBasketRefResponseDto booking,
      String hotelId) {
    if (StatusEnum.CIOL_RC_FAILED.equals(basketDto.getStatus())) {

      var bookingRef = booking.getBookingReference();

      var rooms = booking.getReservationByIdList()
          .stream()
          .toList();

      var ciolAlerts = rooms.stream()
          .filter(room -> Objects.nonNull(room.getAlerts()))
          .flatMap(room -> room.getAlerts().stream())
          .filter(alert -> ALERT_CIOL_CODE.equals(alert.getCode()))
          .map(ReservationAlertsDto::getId)
          .filter(Objects::nonNull)
          .map(alertId -> {
            var alertDto = new Alert();
            alertDto.setId(alertId);
            return alertDto;
          })
          .toList();

      var request = createDeleteAlertRequest(
          hotelId,
          rooms.stream().map(ReservationByIdDto::getReservationId).collect(Collectors.toSet()),
          ciolAlerts);
      ohipAdapterClient.updateReservationAlerts(request);
      log.warn("CIOL revert booking:{} deleted checkin alerts:{}", bookingRef, ciolAlerts);
    }
  }

  private UpdateReservationAlertsRequest createDeleteAlertRequest(String hotelId,
      Set<String> roomsIds,
      List<Alert> alertDtos) {
    var deleteCiolAlerts = new UpdateReservationAlertsRequest();
    deleteCiolAlerts.setHotelId(hotelId);
    deleteCiolAlerts.setReservationIds(roomsIds);
    deleteCiolAlerts.setAlerts(alertDtos);
    return deleteCiolAlerts;
  }
}

package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service;

import java.util.List;
import java.util.Objects;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationAlertsDto;


public final class OhipAlertUtils {

  public static final String DEREG_CARD_COMPLETED_DESC =
      "Do not print registration card, Pre-Check-In completed";

  private OhipAlertUtils() {}

  public static boolean isDeRegCardCompleted(List<ReservationAlertsDto> alerts) {
    return alerts != null
        && alerts.stream()
        .map(ReservationAlertsDto::getDescription)
        .filter(Objects::nonNull)
        .anyMatch(desc -> desc.equals(DEREG_CARD_COMPLETED_DESC));
  }
}
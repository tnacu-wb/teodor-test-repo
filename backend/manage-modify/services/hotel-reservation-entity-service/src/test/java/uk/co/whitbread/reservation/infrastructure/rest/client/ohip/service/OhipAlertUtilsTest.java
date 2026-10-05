package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service;


import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationAlertsDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OhipAlertUtilsTest {

  @Test
  void shouldReturnTrueWhenAlertMatchesDescription() {
    ReservationAlertsDto alert = new ReservationAlertsDto();
    alert.setDescription(OhipAlertUtils.DEREG_CARD_COMPLETED_DESC);

    boolean result = OhipAlertUtils.isDeRegCardCompleted(List.of(alert));

    assertTrue(result);
  }

  @Test
  void shouldReturnFalseWhenAlertDoesNotMatchDescription() {
    ReservationAlertsDto alert = new ReservationAlertsDto();
    alert.setDescription("Some other alert");

    boolean result = OhipAlertUtils.isDeRegCardCompleted(List.of(alert));

    assertFalse(result);
  }

  @Test
  void shouldReturnFalseWhenAlertsListIsNull() {
    boolean result = OhipAlertUtils.isDeRegCardCompleted(null);

    assertFalse(result);
  }

  @Test
  void shouldReturnFalseWhenDescriptionsAreNull() {
    ReservationAlertsDto alert = new ReservationAlertsDto();
    alert.setDescription(null);

    boolean result = OhipAlertUtils.isDeRegCardCompleted(List.of(alert));

    assertFalse(result);
  }

  @Test
  void shouldReturnFalseWhenAlertsListIsEmpty() {
    boolean result = OhipAlertUtils.isDeRegCardCompleted(List.of());

    assertFalse(result);
  }
}


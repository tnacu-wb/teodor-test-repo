package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in;

import uk.co.whitbread.hotel.ohip.adapter.generated.models.Status;

public class ReservationStatusOhipDto extends Status {

  public String getUniqueIdReservation() {
    final var href = this.getLinks().get(0).getHref();
    return href.substring(href.lastIndexOf("/") + 1);
  }
}

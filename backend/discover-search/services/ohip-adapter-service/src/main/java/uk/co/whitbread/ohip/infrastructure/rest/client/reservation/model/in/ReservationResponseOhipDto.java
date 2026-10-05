package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationResponseOhipDto {

  private String confirmationId;
}

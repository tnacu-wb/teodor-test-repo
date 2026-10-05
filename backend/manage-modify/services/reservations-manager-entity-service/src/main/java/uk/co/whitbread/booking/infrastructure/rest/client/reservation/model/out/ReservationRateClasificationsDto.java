package uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRateClasificationsDto {

  private String rateClassification;
  private String rateNotes;

}

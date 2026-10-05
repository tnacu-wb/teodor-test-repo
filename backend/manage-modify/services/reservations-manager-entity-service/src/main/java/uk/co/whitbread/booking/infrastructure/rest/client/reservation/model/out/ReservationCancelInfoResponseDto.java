package uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationCancelInfoResponseDto {

  private Boolean isCancellable;
  private Boolean isAmendable;
  private Boolean isRuleCompliant;
  private String aemLabelKey;
}

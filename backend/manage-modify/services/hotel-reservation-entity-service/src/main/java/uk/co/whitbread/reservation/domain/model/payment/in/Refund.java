package uk.co.whitbread.reservation.domain.model.payment.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Refund {

  private Amount amount;
  private Card card;
  private ReasonEnum reason;
  private TypeEnum type;

}

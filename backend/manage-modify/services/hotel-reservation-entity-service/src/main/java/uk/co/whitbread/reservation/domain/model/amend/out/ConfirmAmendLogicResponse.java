package uk.co.whitbread.reservation.domain.model.amend.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.domain.model.payment.out.InitiatePaymentResponse;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmAmendLogicResponse {

  private InitiatePaymentResponse payment;
}

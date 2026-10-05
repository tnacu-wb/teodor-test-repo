package uk.co.whitbread.promo.domain.model.promobatch.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.promo.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedeemPromoCodeRequest  implements SelfValidation<RedeemPromoCodeRequest>  {

  private String promoCode;
  private String bookingReference;
}

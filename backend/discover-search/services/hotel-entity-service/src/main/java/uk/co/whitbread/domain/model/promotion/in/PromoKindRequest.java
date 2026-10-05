package uk.co.whitbread.domain.model.promotion.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoKindRequest {

  private String promoCode;
  private String country;
  private String channel;
  private String subChannel;

}

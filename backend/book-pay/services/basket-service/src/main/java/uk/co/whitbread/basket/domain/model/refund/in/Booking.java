package uk.co.whitbread.basket.domain.model.refund.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

  private String type;
  private String journey;
  private String channel;
  private BusinessSite businessSite;
}

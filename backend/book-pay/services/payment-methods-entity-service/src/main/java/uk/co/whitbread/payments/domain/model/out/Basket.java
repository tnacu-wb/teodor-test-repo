package uk.co.whitbread.payments.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Basket {

  private String bookingReference;
  private String channel;
  private String hotelId;
  private String idContext;
  private String basketStatus;
}

package uk.co.whitbread.payments.infrastructure.rest.client.basket.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BasketDto {

  private String bookingReference;
  private String channel;
  private String hotelId;
  private String idContext;
  private BasketStatusEnum status;

}

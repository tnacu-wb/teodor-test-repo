package uk.co.whitbread.basket.domain.model.ohip.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BillingAddressResponse {

  private String status;
}

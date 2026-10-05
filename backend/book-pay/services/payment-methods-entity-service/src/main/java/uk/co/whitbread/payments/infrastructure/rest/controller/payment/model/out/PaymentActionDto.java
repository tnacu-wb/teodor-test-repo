package uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentActionDto {

  @JsonProperty("chargeType")
  private String chargeType;

  @JsonProperty("price")
  private PriceDto price;

  @JsonProperty("order")
  private Integer order;
}


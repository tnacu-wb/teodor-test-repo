package uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out;

import jakarta.validation.constraints.DecimalMax;
import lombok.Data;

@Data
public class PriceDto {

  @DecimalMax(value = "999.99", message = "Maximum Dinner Spend is 999.99")
  private int amount;
  private String currency;
}

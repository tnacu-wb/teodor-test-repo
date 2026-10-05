package uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PriceCapLocationsDto {

  @JsonProperty("uKWide")
  private PriceDto ukWide;

  private PriceDto greaterLondon;

  private PriceDto ireland;

}

package uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PriceCapLocationsDto {

  @Schema(description = "Price data for UK Wide")
  private PriceDto ukWide;

  @Schema(description = "Price data for Greater London")
  private PriceDto greaterLondon;

  @Schema(description = "Price data for Ireland")
  private PriceDto ireland;

}

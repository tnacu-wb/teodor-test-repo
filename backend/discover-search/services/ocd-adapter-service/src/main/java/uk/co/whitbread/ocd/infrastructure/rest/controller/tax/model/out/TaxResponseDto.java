package uk.co.whitbread.ocd.infrastructure.rest.controller.tax.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxResponseDto {

  private String roomType;

  private String ratePlanCode;

  private PriceInfoDto total;

}

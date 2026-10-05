package uk.co.whitbread.content.infrastructure.rest.client.booking.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatesConfigCommonDto {
  private String defaultRates;
  private String promotionalRates;
  private String promotionalDiscountRates;
  private String promotionalPackage;
} 
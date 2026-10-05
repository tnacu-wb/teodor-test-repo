package uk.co.whitbread.infrastructure.rest.controller.availability.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateDto {

  private CorporateRateDto corporateRates;
  private List<String> ratePlanCodes;
}

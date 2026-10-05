package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in;

import java.util.List;
import lombok.Data;

@Data
public class RateV3Dto {

  private List<CorporateRateDto> corporateRates;
  private List<String> ratePlanCodes;
}

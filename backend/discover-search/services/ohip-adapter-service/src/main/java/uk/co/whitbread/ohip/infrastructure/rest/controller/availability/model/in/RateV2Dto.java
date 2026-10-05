package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in;

import java.util.List;
import lombok.Data;

@Data
public class RateV2Dto {

  private CorporateRateDto corporateRates;
  private List<String> ratePlanCodes;
}

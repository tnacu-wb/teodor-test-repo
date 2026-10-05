package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RateDto {

  private CorporateRateDto corporateRates;
  private List<String> ratePlanCodes;
}

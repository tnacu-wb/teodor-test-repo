package uk.co.whitbread.reservation.infrastructure.rest.client.hotel.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model.CorporateRateDto;

@Data
@Builder
public class RateDto {

  private CorporateRateDto corporateRates;
  private List<String> ratePlanCodes;
}

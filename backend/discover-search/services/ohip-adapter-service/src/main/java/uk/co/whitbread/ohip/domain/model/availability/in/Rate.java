package uk.co.whitbread.ohip.domain.model.availability.in;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Rate {

  private CorporateRate corporateRates;
  private List<String> ratePlanCodes;
}

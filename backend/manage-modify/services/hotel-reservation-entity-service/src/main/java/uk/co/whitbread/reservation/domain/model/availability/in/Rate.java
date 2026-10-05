package uk.co.whitbread.reservation.domain.model.availability.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Rate {

  private CorporateRate corporateRates;
  private List<String> ratePlanCodes;
}

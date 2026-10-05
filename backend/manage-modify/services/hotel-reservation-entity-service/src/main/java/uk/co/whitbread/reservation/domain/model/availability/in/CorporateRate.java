package uk.co.whitbread.reservation.domain.model.availability.in;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CorporateRate {

  private String corporateId;
  private List<String> ratePlanSets;
}

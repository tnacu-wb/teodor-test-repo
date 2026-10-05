package uk.co.whitbread.ohip.domain.model.availability.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorporateRate {

  private String corporateId;
  private List<String> ratePlanSets;
}

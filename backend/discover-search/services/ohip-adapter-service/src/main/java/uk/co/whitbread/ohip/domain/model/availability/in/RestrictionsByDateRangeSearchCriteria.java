package uk.co.whitbread.ohip.domain.model.availability.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Value
@Builder
@EqualsAndHashCode
public class RestrictionsByDateRangeSearchCriteria implements SelfValidation<RestrictionsByDateRangeSearchCriteria> {

  @NotEmpty
  String hotelId;
  @NotEmpty
  String startDate;
  @NotEmpty
  String endDate;
}

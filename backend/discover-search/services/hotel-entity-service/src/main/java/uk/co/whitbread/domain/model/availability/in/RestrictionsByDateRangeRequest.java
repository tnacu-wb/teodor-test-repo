package uk.co.whitbread.domain.model.availability.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestrictionsByDateRangeRequest {

  @NotNull
  String hotelId;
  @NotEmpty
  String startDate;
  @NotEmpty
  String endDate;
}

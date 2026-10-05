package uk.co.whitbread.domain.model.availability.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MultiHotelRestrictionsByDateRangeRequest {

  @NotNull
  List<String> hotelIds;
  @NotEmpty
  String startDate;
  @NotEmpty
  String endDate;
}

package uk.co.whitbread.domain.model.availability.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualTimeSpan {
  private String startDate;
  private String endDate;
}

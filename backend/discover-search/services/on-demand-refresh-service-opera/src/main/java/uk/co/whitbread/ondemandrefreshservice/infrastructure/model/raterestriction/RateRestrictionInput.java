package uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RateRestrictionInput {

  private String hotelId;
  private String startDate;
  private String endDate;
}

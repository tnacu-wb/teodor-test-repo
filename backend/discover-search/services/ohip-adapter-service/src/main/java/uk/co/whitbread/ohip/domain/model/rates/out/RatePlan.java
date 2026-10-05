package uk.co.whitbread.ohip.domain.model.rates.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class RatePlan {

  private String ratePlanCode;
  private String hotelId;
  private PrimaryDetails primaryDetails;
  private Classifications classifications;
}

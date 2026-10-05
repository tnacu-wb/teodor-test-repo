package uk.co.whitbread.ohip.domain.model.availability.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityRoomRate {

  private String ratePlanCode;
  private String promotionCode;
  @Singular
  private List<AvailabilityRoomType> roomTypes;
}

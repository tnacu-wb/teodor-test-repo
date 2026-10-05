package uk.co.whitbread.domain.model.availability.out;

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
public class RoomRate {

  private String ratePlanCode;
  private String rateDisplaySet;
  private String cellCode;
  private boolean twinRoomTypeAvailability;
  @Singular
  private List<RoomTypeInfo> roomTypes;
  private String globalCompanyId;
  private String promotionCode;

}

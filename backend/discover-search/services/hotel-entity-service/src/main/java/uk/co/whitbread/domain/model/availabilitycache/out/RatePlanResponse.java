package uk.co.whitbread.domain.model.availabilitycache.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatePlanResponse {

  private String code;
  private String name;
  private String description;
  private String classification;
  private String order;
  private PriceResponse totalCost;
  private List<RoomResponse> rooms;
}

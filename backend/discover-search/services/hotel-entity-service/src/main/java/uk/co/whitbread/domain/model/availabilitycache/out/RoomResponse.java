package uk.co.whitbread.domain.model.availabilitycache.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomResponse {

  private String type;
  private Integer adults;
  private Integer children;
  private PriceResponse totalCost;
}

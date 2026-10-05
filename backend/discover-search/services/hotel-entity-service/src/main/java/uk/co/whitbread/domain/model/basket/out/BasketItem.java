package uk.co.whitbread.domain.model.basket.out;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketItem {

  private Map<String, String> details;
  private Boolean hasOccupancySup;
  private String sourceId;
  private String type;
}

package uk.co.whitbread.basket.domain.model.basket.out;

import jakarta.validation.constraints.NotEmpty;
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
  @NotEmpty
  private String type;
  @NotEmpty
  private String sourceId;
  private String reqAction;
  private Integer ack;
  private Map<String, String> details;
  private Boolean hasOccupancySup;
}

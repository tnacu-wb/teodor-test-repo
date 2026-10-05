package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out;

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
public class BasketItemDto {
  @NotEmpty
  private String type;
  @NotEmpty
  private String sourceId;
  private Map<String, String> details;
  private Boolean hasOccupancySup;
}

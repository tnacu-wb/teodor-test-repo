package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class AddBasketItemDto implements SelfValidation<AddBasketItemDto> {
  @NotEmpty
  private String type;
  @NotEmpty
  private String sourceId;
  private Map<String, String> details;
  private Boolean hasOccupancySup;

  public AddBasketItemDto(String type, String sourceId, Map<String, String> details, Boolean hasOccupancySup) {
    this.type = type;
    this.sourceId = sourceId;
    this.details = details;
    this.hasOccupancySup = hasOccupancySup;
    this.validateSelf();
  }
}

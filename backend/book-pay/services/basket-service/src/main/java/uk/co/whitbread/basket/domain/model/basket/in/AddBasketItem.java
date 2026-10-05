package uk.co.whitbread.basket.domain.model.basket.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class AddBasketItem implements SelfValidation<AddBasketItem> {
  @NotEmpty
  private String type;
  @NotEmpty
  private String sourceId;
  private Map<String, String> details;
  private Boolean hasOccupancySup;

  public AddBasketItem(String type, String sourceId, Map<String, String> details,
      Boolean hasOccupancySup) {
    this.type = type;
    this.sourceId = sourceId;
    this.details = details;
    this.hasOccupancySup = hasOccupancySup;
    this.validateSelf();
  }
}

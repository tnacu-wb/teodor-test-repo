package uk.co.whitbread.basket.domain.model.basket.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class UpdateBasketItemSupplement implements SelfValidation<UpdateBasketItemSupplement> {
  @NotEmpty
  private String sourceId;
  @NotNull
  private Boolean hasOccupancySup;

  public UpdateBasketItemSupplement(String sourceId,
                                    Boolean hasOccupancySup) {
    this.sourceId = sourceId;
    this.hasOccupancySup = hasOccupancySup;
    this.validateSelf();
  }
}

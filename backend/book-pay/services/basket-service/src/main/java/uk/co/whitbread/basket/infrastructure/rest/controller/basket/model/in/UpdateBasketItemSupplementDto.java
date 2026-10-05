package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class UpdateBasketItemSupplementDto implements SelfValidation<UpdateBasketItemSupplementDto> {
  @NotEmpty
  private String sourceId;
  @NotNull
  private Boolean hasOccupancySup;

  public UpdateBasketItemSupplementDto(String sourceId, Boolean hasOccupancySup) {
    this.sourceId = sourceId;
    this.hasOccupancySup = hasOccupancySup;
    this.validateSelf();
  }
}

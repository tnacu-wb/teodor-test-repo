package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
@Jacksonized
public class UpdateBasketItemOccupancyRequestDto implements SelfValidation<UpdateBasketItemOccupancyRequestDto> {
  @NotEmpty
  private List<UpdateBasketItemSupplementDto> items;

  public UpdateBasketItemOccupancyRequestDto(List<UpdateBasketItemSupplementDto> items) {
    this.items = items;
    this.validateSelf();
  }
}

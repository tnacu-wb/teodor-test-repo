package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class AddBasketItemTypeDto implements SelfValidation<AddBasketItemTypeDto> {
  private String type;
  private List<String> confirmationData;

  public AddBasketItemTypeDto(String type, List<String> confirmationData) {
    this.type = type;
    this.confirmationData = confirmationData;
    this.validateSelf();
  }
}

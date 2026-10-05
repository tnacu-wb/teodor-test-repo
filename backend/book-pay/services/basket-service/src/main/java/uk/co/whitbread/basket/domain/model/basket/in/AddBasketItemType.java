package uk.co.whitbread.basket.domain.model.basket.in;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class AddBasketItemType implements SelfValidation<AddBasketItemType> {
  private String type;
  private List<String> confirmationData;

  public AddBasketItemType(String type, List<String> confirmationData) {
    this.type = type;
    this.confirmationData = confirmationData;
    this.validateSelf();
  }
}

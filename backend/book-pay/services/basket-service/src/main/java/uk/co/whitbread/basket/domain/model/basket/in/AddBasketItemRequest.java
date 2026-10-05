package uk.co.whitbread.basket.domain.model.basket.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class AddBasketItemRequest implements SelfValidation<AddBasketItemRequest> {
  @NotNull
  private String reference;
  private String lockingTime;
  private boolean migratedReservation;
  @NotEmpty
  private List<AddBasketItemType> itemTypes;
  @NotEmpty
  private List<AddBasketItem> items;

  public AddBasketItemRequest(String reference, String lockingTime,
      boolean migratedReservation, List<AddBasketItemType> itemTypes, List<AddBasketItem> items) {
    this.reference = reference;
    this.lockingTime = lockingTime;
    this.migratedReservation = migratedReservation;
    this.itemTypes = itemTypes;
    this.items = items;
    this.validateSelf();
  }
}

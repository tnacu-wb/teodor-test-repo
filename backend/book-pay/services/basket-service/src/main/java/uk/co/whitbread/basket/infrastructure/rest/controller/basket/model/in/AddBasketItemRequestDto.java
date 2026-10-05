package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class AddBasketItemRequestDto implements SelfValidation<AddBasketItemRequestDto> {
  @NotNull
  private String reference;
  private String lockingTime;
  private boolean migratedReservation;
  @NotEmpty
  List<AddBasketItemTypeDto> itemTypes;
  @NotEmpty
  private List<AddBasketItemDto> items;

  public AddBasketItemRequestDto(String reference, String lockingTime,
      boolean migratedReservation, List<AddBasketItemTypeDto> itemTypes, List<AddBasketItemDto> items) {
    this.reference = reference;
    this.lockingTime = lockingTime;
    this.migratedReservation = migratedReservation;
    this.itemTypes = itemTypes;
    this.items = items;
    this.validateSelf();
  }
}

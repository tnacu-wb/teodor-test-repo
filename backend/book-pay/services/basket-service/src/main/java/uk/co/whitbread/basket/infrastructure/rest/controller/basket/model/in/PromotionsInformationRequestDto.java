package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.basket.out.PromoKind;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionsInformationRequestDto {

  @NotBlank
  private String promotionCode;
  @NotNull
  private PromoKind promoKind;
}
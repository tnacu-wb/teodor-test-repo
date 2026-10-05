package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class BackgroundChargeRequestDto implements SelfValidation<BackgroundChargeRequestDto> {

  @NotNull
  private String basketReference;

  @NotNull
  private String token;

  public BackgroundChargeRequestDto(String basketReference, String token) {
    this.basketReference = basketReference;
    this.token = token;
    this.validateSelf();
  }
}

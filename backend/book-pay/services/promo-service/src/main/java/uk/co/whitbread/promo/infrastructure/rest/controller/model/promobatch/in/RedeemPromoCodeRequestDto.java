package uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedeemPromoCodeRequestDto {

  @Schema(description = "Unique promo code", example = "27CR7HCV49")
  @NotBlank
  private String promoCode;

  @Schema(description = "Booking reference", example = "AQN3269618")
  @NotBlank
  private String bookingReference;
}

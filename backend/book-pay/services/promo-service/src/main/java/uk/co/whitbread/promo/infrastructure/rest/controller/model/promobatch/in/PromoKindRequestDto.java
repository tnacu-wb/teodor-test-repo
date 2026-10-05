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
public class PromoKindRequestDto {
  @Schema(description = "Promo code to validate", example = "27CR7HCV49")
  @NotBlank
  private String promoCode;

  @Schema(description = "country (Region)", example = "GB")
  private String country;

  @Schema(description = "Channel", example = "PI")
  private String channel;

  @Schema(description = "Sub channel (platform)", example = "WEB")
  private String subChannel;
}

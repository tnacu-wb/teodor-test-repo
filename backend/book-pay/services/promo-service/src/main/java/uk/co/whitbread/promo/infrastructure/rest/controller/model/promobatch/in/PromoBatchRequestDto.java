package uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromoBatchRequestDto {

  @Schema(description = "Hotel identifier requesting the promo batch", example = "HOTELID")
  @NotBlank
  private String hotelId;

  @Schema(description = "Opera promo code to which the generated codes will be linked", example = "PROMO")
  @NotBlank
  private String operaPromoCode;

  @NotBlank
  @Size(max = 20)
  @Pattern(regexp = "^[a-zA-Z0-9]+$")
  @Schema(
      description = "prefix (1–20 characters).",
      example = "ABC",
      nullable = false
  )
  private String prefix;

  @Schema(description = "Number of promo codes to generate in this batch", example = "500")
  @Min(1)
  @Max(1000000)
  private Integer batchCount;

  @Schema(description = "Length of each generated promo code ", example = "10")
  @NotNull
  @Min(8)
  private Integer codeLength;

  @Schema(description = "Expiry date for the generated promo codes", example = "2026-12-25")
  @FutureOrPresent(message = "Expiry date must be a valid date")
  private LocalDate expiryDate;

  @Schema(description = "Add notes for reference")
  private String notes;

  @Schema(description = "Requested by user")
  private String requestedBy;

  @Schema(description = "Campaign name")
  private String campaignName;

  @Schema(description = "Batch eligibility configuration")
  @Valid
  private List<BatchEligibilityRequestDto> batchEligibilities;

  @Schema(description = "Indicates whether the promo code is multi-use")
  private Boolean isMultiple;

  @Schema(description = "Maximum allowed redemptions")
  private Integer maxRedemptionLimit;
}

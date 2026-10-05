package uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingAllowancesDto {

  @Schema(example = "true", description = "flag to allow alcohol")
  @NotNull
  private boolean allowAlcohol;

  @Schema(example = "true", description = "flag to allow car parking")
  @NotNull
  private boolean allowCarParking;

  @Schema(example = "true", description = "flag to allow additional costs")
  @NotNull
  private boolean allowAdditionalCosts;

  @Schema(example = "true", description = "flag to allow premier saver rates")
  @NotNull
  private boolean allowPremierSaverRates;

  @Schema(example = "true", description = "flag to allow individual cards")
  @NotNull
  private boolean allowIndividualCards;

  @Schema(example = "14", description = "max number of nights")
  @NotNull
  private int maxNumberOfNights;

  @Schema(description = "PriceCapLocationsDto data")
  @NotNull
  private PriceCapLocationsDto maxDinnerBudgets;

}

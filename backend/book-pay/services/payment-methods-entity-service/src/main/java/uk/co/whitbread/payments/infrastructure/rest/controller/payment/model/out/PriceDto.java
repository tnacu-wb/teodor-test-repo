package uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PriceDto {

  @Schema(example = "25", description = "authorised amount per person per night")
  @NotNull
  private BigDecimal amount;

  @Schema(example = "GBP", description = "the amount currency")
  @NotNull
  private String currency;

}

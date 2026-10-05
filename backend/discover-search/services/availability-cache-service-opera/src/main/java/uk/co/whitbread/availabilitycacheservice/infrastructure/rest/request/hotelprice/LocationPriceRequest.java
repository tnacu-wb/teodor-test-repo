package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.DateFormat;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.MaxDateRangeConstraint;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MaxDateRangeConstraint(arrival = "startDate", departure = "endDate")
@Builder
public class LocationPriceRequest {

  @NotNull
  protected BigDecimal priceThreshold;
  @NotNull
  protected BigDecimal altPriceThreshold;
  @NotNull
  @NotBlank
  @DateFormat
  private String startDate;
  @NotNull
  @NotBlank
  @DateFormat
  private String endDate;

}

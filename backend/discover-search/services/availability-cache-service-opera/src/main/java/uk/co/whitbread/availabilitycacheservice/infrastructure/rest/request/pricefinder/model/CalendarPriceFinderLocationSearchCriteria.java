package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarPriceFinderLocationSearchCriteria {

  @NotNull
  @Parameter(in = ParameterIn.QUERY, name = "locationId", example = "ChIJdd4hrwug2EcRmSrV3Vo6llI", required = true,
      schema = @Schema(type = "string"))
  private String locationId;

  @NotNull
  @Parameter(in = ParameterIn.QUERY, name = "milesRadius", example = "50", required = true,
      schema = @Schema(type = "integer"))
  private Integer milesRadius = 50;

  @Min(1)
  @Max(12)
  @Parameter(in = ParameterIn.QUERY, name = "month", example = "10", required = true,
      schema = @Schema(type = "integer", defaultValue = "Current Month"))
  private Integer month = LocalDate.now().getMonthValue();

}

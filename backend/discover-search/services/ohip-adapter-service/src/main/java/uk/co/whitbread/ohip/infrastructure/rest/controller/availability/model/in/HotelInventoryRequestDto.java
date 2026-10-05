package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.infrastructure.rest.controller.validation.DateFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelInventoryRequestDto {

  @DateFormat
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "dateRangeStart", example = "2022-10-05",
      required = true, schema = @Schema(type = "string"))
  private String dateRangeStart;

  @DateFormat
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "dateRangeEnd", example = "2022-10-07",
      required = true, schema = @Schema(type = "string"))
  private String dateRangeEnd;

}

package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
public class ItemInventoryRequestDto {

  @DateFormat
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "startDate", example = "2022-10-05",
      required = true, schema = @Schema(type = "string"))
  private String startDate;

  @DateFormat
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "endDate", example = "2022-10-07",
      required = true, schema = @Schema(type = "string"))
  private String endDate;

  @ArraySchema(arraySchema = @Schema(
          name = "itemCodes",
          example = "[\"ECI\", \"LC2\"]"))
  private String[] itemCodes;

}

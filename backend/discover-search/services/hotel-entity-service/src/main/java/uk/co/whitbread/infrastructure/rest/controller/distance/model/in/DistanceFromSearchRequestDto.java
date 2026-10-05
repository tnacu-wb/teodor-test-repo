package uk.co.whitbread.infrastructure.rest.controller.distance.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DistanceFromSearchRequestDto {

  @NotEmpty
  @Parameter(in = ParameterIn.PATH, name = "location", example = "ChIJdd4hrwug2EcRmSrV3Vo6llI",
      required = true, schema = @Schema(type = "string"))
  private String location;

  @NotEmpty
  @Parameter(in = ParameterIn.PATH, name = "locationFormat", example = "placeId", required = true,
      schema = @Schema(type = "string"))
  private String locationFormat;

  @Parameter(in = ParameterIn.PATH, name = "radius", example = "50", required = true,
      schema = @Schema(type = "string"))
  private Integer radius;

  @Parameter(in = ParameterIn.PATH, name = "radiusUnit", example = "mi", required = true,
      schema = @Schema(type = "string"))
  private String radiusUnit;
}

package uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PriceFinderGlobalConfigRequestDto {

  @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb",
      schema = @Schema(type = "string"))
  private String country;

  @Parameter(in = ParameterIn.QUERY, name = "language", example = "en",
      schema = @Schema(type = "string"))
  private String language;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "channelId", example = "DISTR",
      schema = @Schema(type = "string"))
  private String channelId;

  @Parameter(in = ParameterIn.QUERY, name = "brand", example = "PI",
      required = true, schema = @Schema(type = "string"))
  private String brand;

  @Parameter(in = ParameterIn.QUERY, name = "path", example = "path/to/content",
      required = false, schema = @Schema(type = "string"))
  private String path;
}

package uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IndexHeaderDataRequestDto {

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb",
      required = true, schema = @Schema(type = "string"))
  private String country;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "language", example = "en",
      required = true, schema = @Schema(type = "string"))
  private String language;

  @Parameter(in = ParameterIn.QUERY, name = "businessBooker", example = "true", schema = @Schema(type = "boolean"))
  private Boolean businessBooker;

}

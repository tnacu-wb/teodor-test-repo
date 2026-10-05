package uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppsHomepageRequestDto {

  @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb",
      schema = @Schema(type = "string"))
  @NotEmpty
  private String country;

  @Parameter(in = ParameterIn.QUERY, name = "language", example = "en",
      schema = @Schema(type = "string"))
  @NotEmpty
  private String language;

  @Parameter(in = ParameterIn.QUERY, name = "channel", example = "PI",
      required = true, schema = @Schema(type = "string"))
  @NotEmpty
  private String channel;

  @Parameter(in = ParameterIn.QUERY, name = "subchannel", example = "WEB",
      required = true, schema = @Schema(type = "string"))
  @NotEmpty
  private String subchannel;
}
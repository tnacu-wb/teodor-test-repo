package uk.co.whitbread.content.infrastructure.rest.controller.dlp.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DlpInformationRequestDto {

  @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb",
      schema = @Schema(type = "string"))
  private String country;

  @Parameter(in = ParameterIn.QUERY, name = "language", example = "en",
      schema = @Schema(type = "string"))
  private String language;

  @Parameter(in = ParameterIn.QUERY, name = "dlpPath", example = "/england/bedfordshire/luton",
      required = true, schema = @Schema(type = "string"))
  private String dlpPath;
}

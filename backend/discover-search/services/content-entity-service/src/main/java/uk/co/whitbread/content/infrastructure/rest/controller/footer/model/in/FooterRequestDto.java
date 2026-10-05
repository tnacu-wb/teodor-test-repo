package uk.co.whitbread.content.infrastructure.rest.controller.footer.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FooterRequestDto {

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb",
      required = true, schema = @Schema(type = "string"))
  private String country;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "language", example = "en",
      required = true, schema = @Schema(type = "string"))
  private String language;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "site", example = "leisure",
      required = true, schema = @Schema(type = "string"))
  private String site;

  public FooterRequestDto(String country, String language, String site) {
    this.country = country;
    this.language = language;
    this.site = site;
  }
}

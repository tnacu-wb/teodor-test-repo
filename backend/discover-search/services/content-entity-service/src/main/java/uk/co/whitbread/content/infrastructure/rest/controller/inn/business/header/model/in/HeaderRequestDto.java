package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HeaderRequestDto {
  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb",
      required = true, schema = @Schema(type = "string"))
  private String country;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "language", example = "en",
      required = true, schema = @Schema(type = "string"))
  private String language;


  public HeaderRequestDto(String country, String language) {
    this.country = country;
    this.language = language;
  }
}

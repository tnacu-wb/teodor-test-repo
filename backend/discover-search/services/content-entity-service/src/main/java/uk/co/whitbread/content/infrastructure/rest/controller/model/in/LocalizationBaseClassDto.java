package uk.co.whitbread.content.infrastructure.rest.controller.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public abstract class LocalizationBaseClassDto {

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb",
      required = true, schema = @Schema(type = "string"))
  private String country;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "language", example = "en",
      required = true, schema = @Schema(type = "string"))
  private String language;

  protected LocalizationBaseClassDto(String country, String language) {
    this.country = country;
    this.language = language;
  }
}

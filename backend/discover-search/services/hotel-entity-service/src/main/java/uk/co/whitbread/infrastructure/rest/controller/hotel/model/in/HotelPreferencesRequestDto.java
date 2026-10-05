package uk.co.whitbread.infrastructure.rest.controller.hotel.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record HotelPreferencesRequestDto(
    @NotEmpty
    @Parameter(in = ParameterIn.QUERY, name = "preferenceGroupsCodes", example = "EVENTS", required = true,
        schema = @Schema(type = "string"))
    String preferenceGroupsCodes,

    @Pattern(regexp = "^(en|de)$", message = "Language must be either 'en' or 'de'")
    @Parameter(in = ParameterIn.QUERY, name = "language", example = "en", required = false,
        schema = @Schema(type = "string", allowableValues = {"en", "de"}, defaultValue = "en"))
    String language
) {

  public HotelPreferencesRequestDto {
    language = language == null ? "en" : language;
  }
}

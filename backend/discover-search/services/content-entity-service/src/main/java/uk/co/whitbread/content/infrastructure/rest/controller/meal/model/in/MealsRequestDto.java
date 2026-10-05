package uk.co.whitbread.content.infrastructure.rest.controller.meal.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MealsRequestDto {

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb",
      required = true, schema = @Schema(type = "string"))
  private String country;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "language", example = "en",
      required = true, schema = @Schema(type = "string"))
  private String language;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "hotelId", example = "GLASTA",
      required = true, schema = @Schema(type = "string"))
  private String hotelId;

  public MealsRequestDto(String country, String language, String hotelId) {
    this.country = country;
    this.language = language;
    this.hotelId = hotelId;
  }
}

package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Value;


@Value
@Builder
public class SlugRequestDto {

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "slug", example = "england/greater-london/london/london-kings-cross",
      required = true, schema = @Schema(type = "string"))
  String slug;

  public SlugRequestDto(String slug) {
    this.slug = slug;
  }
}

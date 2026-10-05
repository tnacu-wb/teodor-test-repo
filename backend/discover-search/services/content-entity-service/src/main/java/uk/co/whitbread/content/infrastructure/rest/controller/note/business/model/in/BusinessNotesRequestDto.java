package uk.co.whitbread.content.infrastructure.rest.controller.note.business.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessNotesRequestDto {

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "lang", example = "en",
      required = true, schema = @Schema(type = "string"))
  private String lang;
}

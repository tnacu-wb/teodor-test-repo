package uk.co.whitbread.ohip.infrastructure.rest.controller.amend.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmendSummaryRequestDto {
  @NotNull
  @Parameter(in = ParameterIn.QUERY, name = "hotelId", example = "MANOLD",
      required = true, schema = @Schema(type = "string"))
  private String hotelId;
  @ArraySchema(arraySchema = @Schema(required = true,
      name = "reservationIds", example = "[896423]"))
  private List<String> reservationIds;
}

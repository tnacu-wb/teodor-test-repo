package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositFoliosRequestDto {

  @Parameter(in = ParameterIn.QUERY, name = "reservationIds", example = "123,345,567",
      schema = @Schema(type = "array"))
  @NotEmpty
  private List<String> reservationIds;
}

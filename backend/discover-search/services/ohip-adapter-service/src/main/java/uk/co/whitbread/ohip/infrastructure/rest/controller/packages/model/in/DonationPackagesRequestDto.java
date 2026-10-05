package uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationPackagesRequestDto {

  @NotBlank
  @Parameter(in = ParameterIn.PATH, name = "hotelId", example = "LONEUS",
      required = true, schema = @Schema(type = "string"))
  private String hotelId;

  @Parameter(in = ParameterIn.QUERY, name = "packageCodes", example = "CHRTY1, CHRTY2",
      description = "The donation package codes sent to get the package codes details",
      schema = @Schema(type = "array"), required = true)
  private List<String> packageCodes;

}

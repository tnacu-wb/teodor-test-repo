package uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AddressSearchRequestDto {

  @NotBlank
  @Parameter(in = ParameterIn.QUERY, name = "searchTerm", example = "SG89ES",
      required = true, schema = @Schema(type = "string"))
  private String searchTerm;

  @Parameter(in = ParameterIn.QUERY, name = "countryCode", example = "uk",
      schema = @Schema(type = "string"))
  private String countryCode;

}

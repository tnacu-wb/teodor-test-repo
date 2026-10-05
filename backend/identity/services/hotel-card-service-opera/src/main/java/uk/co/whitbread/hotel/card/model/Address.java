package uk.co.whitbread.hotel.card.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.shared.commons.validation.CompanyName;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address {

  @NotEmpty
  @Schema(requiredMode = RequiredMode.REQUIRED, example = "120 Holborn")
  private String line1;

  @Schema
  private String line2;

  @Schema
  private String line3;

  @Schema
  private String line4;

  @Schema
  private String line5;

  @NotEmpty
  @Schema(requiredMode = RequiredMode.REQUIRED, example = "EC1N 2TD")
  private String postCode;

  @NotEmpty
  @Schema(requiredMode = RequiredMode.REQUIRED, example = "GB")
  private String countryCode;

  @Schema(requiredMode = RequiredMode.REQUIRED, example = "BUSINESS", allowableValues = "HOME, BUSINESS")
  private String type;

  @Schema(requiredMode = RequiredMode.REQUIRED, example = "Whitbread")
  @CompanyName
  private String companyName;

}

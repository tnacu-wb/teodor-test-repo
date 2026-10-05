package uk.co.whitbread.hotel.register.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InnBCompanyAddress {

  @NotEmpty
  private String line1;

  private String line2;

  private String line3;

  private String line4;

  private String line5;

  private String postCode;

  @NotEmpty
  @Pattern(regexp = "^[A-Z]{1,3}$", message = "Invalid country code")
  private String countryCode;

}

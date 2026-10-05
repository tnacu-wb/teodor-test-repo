package uk.co.whitbread.account.domain.model.in;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uk.co.whitbread.account.domain.model.validation.PostcodeConstraint;

@Data
@PostcodeConstraint
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address {
  private String line1;
  private String line2;
  private String line3;
  private String line4;
  private String line5;
  private String postCode;
  private String countryCode;
  @NotNull
  private AddressType type;
  private String companyName;
}

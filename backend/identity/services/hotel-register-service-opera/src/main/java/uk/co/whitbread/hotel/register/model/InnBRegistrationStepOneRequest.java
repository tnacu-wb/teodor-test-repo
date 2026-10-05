package uk.co.whitbread.hotel.register.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class InnBRegistrationStepOneRequest {

  @NotBlank
  @Email
  private String email;

  @NotBlank
  private String companyName;

  @NotNull
  @Valid
  private InnBCompanyAddress address;

  @Pattern(regexp = "en|de", message = "Language must be 'en' or 'de'")
  @NotBlank
  private String language;

  private String socialMediaType;

  private String socialMediaId;

  private String uniqueTaxpayerReference;

  private CompanyType companyType;

  private UpdatePreferencesRequest updatePreferencesRequest;

}

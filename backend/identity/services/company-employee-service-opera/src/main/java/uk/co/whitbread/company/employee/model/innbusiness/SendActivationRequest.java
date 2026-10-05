package uk.co.whitbread.company.employee.model.innbusiness;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendActivationRequest {

  @Email
  @NotBlank
  private String email;
  @NotBlank
  private String companyName;
  @NotNull
  @Pattern(regexp = "en|de", message = "Invalid language code. Allowed values are: en, de.")
  private String language;
}

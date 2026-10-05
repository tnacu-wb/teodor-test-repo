package uk.co.whitbread.digitalkey.domain.model.axp.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpRequest {
  @NotBlank(message = "Email must not be blank")
  @Email(message = "Invalid email format")
  private String email;
}

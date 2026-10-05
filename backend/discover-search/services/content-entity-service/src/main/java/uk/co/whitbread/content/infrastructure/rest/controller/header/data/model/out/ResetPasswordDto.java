package uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ResetPasswordDto {

  private String criteria;
  private String criteriaDescription;
  private String email;
  private String invalidPassword;
  private String invalidPasswordConfirmation;
  private String logInButton;
  private String password;
  private String passwordConfirmation;
  private String passwordMin;
  private String passwordRequired;
  private String passwordRequirementsAllowed;
  private String passwordRequirementsIdentical;
  private String passwordRequirementsMin;
  private String resetPasswordTitle;
  private String submitButton;
  private String successMessage;
}


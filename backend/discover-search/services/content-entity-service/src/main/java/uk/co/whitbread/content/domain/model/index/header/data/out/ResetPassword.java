package uk.co.whitbread.content.domain.model.index.header.data.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPassword {

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

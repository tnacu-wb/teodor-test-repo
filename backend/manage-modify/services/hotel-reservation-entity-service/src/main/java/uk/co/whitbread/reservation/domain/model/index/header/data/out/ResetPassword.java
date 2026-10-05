package uk.co.whitbread.reservation.domain.model.index.header.data.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPassword {

  private String resetPasswordTitle;
  private String passwordConfirmation;
  private String invalidPassword;
  private String email;
  private String invalidPasswordConfirmation;
  private String criteriaDescription;
  private String submitButton;
  private String logInButton;
  private String password;
  private String successMessage;
  private String criteria;
}

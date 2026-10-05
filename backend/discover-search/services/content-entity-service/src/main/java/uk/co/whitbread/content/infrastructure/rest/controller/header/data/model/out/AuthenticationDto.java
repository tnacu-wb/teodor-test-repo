package uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class AuthenticationDto {

  private String accountDescription;
  private LoginDto login;
  private ForgottenPasswordDto forgottenPassword;
  private List<AccountLinkDto> accountLinks;
  private BusinessDto business;
  private ResetPasswordDto resetPassword;
  private String logoutButton;
  private String signUpButton;
  private String goBackButton;
  private String businessAccountCardRedirectPath;
}

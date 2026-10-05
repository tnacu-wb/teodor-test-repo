package uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

  private LeisureDto leisure;
  private BusinessDto business;
  private BusinessAccountCardDto businessAccountCard;
  private String passwordPlaceholder;
  private String forgotPassword;
  private String signupMessage;
  private String signupLink;
  private String rememberMeLabel;
  private String badCredentialsError;
  private String sessionExpired;
  private String invalidEmail;

}

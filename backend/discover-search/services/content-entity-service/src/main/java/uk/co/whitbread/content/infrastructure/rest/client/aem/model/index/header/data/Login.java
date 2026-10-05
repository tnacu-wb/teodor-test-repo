package uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Login {

  private String signupLink;
  private String genericError;
  private String rememberMeLabel;
  private String invalidPassword;
  private String signupMessage;
  private String badCredentialsError;
  private String sessionExpired;
  private String invalidEmail;
  private String forgotPassword;
  private String passwordPlaceholder;
  private Leisure leisure;
  private Business business;
  private BusinessAccountCard businessAccountCard;
}

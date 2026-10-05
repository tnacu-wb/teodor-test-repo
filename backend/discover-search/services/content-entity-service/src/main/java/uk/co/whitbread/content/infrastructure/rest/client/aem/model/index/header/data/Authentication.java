package uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Authentication {
  
  private String accountDescription;
  private String loginButton;
  private String signUpButton;
  private String logoutButton;
  private Login login;
  private ForgottenPassword forgottenPassword;
  private ResetPassword resetPassword;
  private Boolean businessLoginEnabled;
  private String forgottenPasswordRequestUrl;
  private String microserviceUri;
  private Boolean businessForgottenPasswordEnabled;
  private String forgottenPasswordPath;
  private Boolean timeSwitchingTab;
  private String businessAccountCardRedirectPath;
  private String companyPath;
  private Boolean leisureForgottenPasswordEnabled;
  private Boolean rememberMeEnabled;
  private String defaultLoginTab;
  private String businessLoginRedirectPath;
  private List<AccountLink> accountLinks;
  private String iframePath;
  private Leisure leisure;
  private Business business;
  private RememberPreferredBrand rememberPreferredBrand;
  private String goBackButton;
}

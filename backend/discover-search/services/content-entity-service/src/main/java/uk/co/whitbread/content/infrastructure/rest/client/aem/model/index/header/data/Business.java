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
public class Business {

  private List<BusinessAccountLink> businessAccountLinks;
  private String bookingDashboardRedirectPath;
  private String bookingsInvalidEmailMsg;
  private String bookingLoginRequiredText;
  private String bookingEmailMaxLengthMsg;
  private String companyActivateFailBody;
  private String companyActivateFailTitle;
  private String companyActivateSuccessBody;
  private String companyActivateSuccessTitle;
  private String doubleOptInFailMessage;
  private String doubleOptInSuccessMessage;
  private String emailPlaceholder;
  private String employeeActivationSuccessTitle;
  private String forgotPasswordLink;
  private String formLabel;
  private String formTitle;
  private String loginButton;
  private String loginInfoNotification;
  private String signupButton;
  private String signupUrl;
  private String submitButton;
  private String tab;
  private String tabMobile;
  private String businessLogin;
  private String travelForBusiness;
  private String businessDomain;
}
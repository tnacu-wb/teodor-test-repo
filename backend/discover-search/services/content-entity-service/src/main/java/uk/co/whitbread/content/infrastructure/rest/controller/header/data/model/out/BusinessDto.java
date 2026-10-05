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
public class BusinessDto {

  private List<BusinessAccountLinkDto> businessAccountLinks;
  private String bookingDashboardRedirectPath;
  private String bookingEmailMaxLengthMsg;
  private String bookingLoginRequiredText;
  private String bookingsInvalidEmailMsg;
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

package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.BusinessAccountLinkDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BusinessDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BusinessDto {

  private @Nullable String bookingDashboardRedirectPath;

  private @Nullable String bookingEmailMaxLengthMsg;

  private @Nullable String bookingLoginRequiredText;

  private @Nullable String bookingsInvalidEmailMsg;

  @Valid
  private List<@Valid BusinessAccountLinkDto> businessAccountLinks = new ArrayList<>();

  private @Nullable String companyActivateFailBody;

  private @Nullable String companyActivateFailTitle;

  private @Nullable String companyActivateSuccessBody;

  private @Nullable String companyActivateSuccessTitle;

  private @Nullable String doubleOptInFailMessage;

  private @Nullable String doubleOptInSuccessMessage;

  private @Nullable String emailPlaceholder;

  private @Nullable String employeeActivationSuccessTitle;

  private @Nullable String forgotPasswordLink;

  private @Nullable String formLabel;

  private @Nullable String formTitle;

  private @Nullable String loginButton;

  private @Nullable String loginInfoNotification;

  private @Nullable String signupButton;

  private @Nullable String signupUrl;

  private @Nullable String submitButton;

  private @Nullable String tab;

  private @Nullable String tabMobile;

  public BusinessDto bookingDashboardRedirectPath(String bookingDashboardRedirectPath) {
    this.bookingDashboardRedirectPath = bookingDashboardRedirectPath;
    return this;
  }

  /**
   * Get bookingDashboardRedirectPath
   * @return bookingDashboardRedirectPath
   */
  
  @Schema(name = "bookingDashboardRedirectPath", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingDashboardRedirectPath")
  public String getBookingDashboardRedirectPath() {
    return bookingDashboardRedirectPath;
  }

  public void setBookingDashboardRedirectPath(String bookingDashboardRedirectPath) {
    this.bookingDashboardRedirectPath = bookingDashboardRedirectPath;
  }

  public BusinessDto bookingEmailMaxLengthMsg(String bookingEmailMaxLengthMsg) {
    this.bookingEmailMaxLengthMsg = bookingEmailMaxLengthMsg;
    return this;
  }

  /**
   * Get bookingEmailMaxLengthMsg
   * @return bookingEmailMaxLengthMsg
   */
  
  @Schema(name = "bookingEmailMaxLengthMsg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingEmailMaxLengthMsg")
  public String getBookingEmailMaxLengthMsg() {
    return bookingEmailMaxLengthMsg;
  }

  public void setBookingEmailMaxLengthMsg(String bookingEmailMaxLengthMsg) {
    this.bookingEmailMaxLengthMsg = bookingEmailMaxLengthMsg;
  }

  public BusinessDto bookingLoginRequiredText(String bookingLoginRequiredText) {
    this.bookingLoginRequiredText = bookingLoginRequiredText;
    return this;
  }

  /**
   * Get bookingLoginRequiredText
   * @return bookingLoginRequiredText
   */
  
  @Schema(name = "bookingLoginRequiredText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingLoginRequiredText")
  public String getBookingLoginRequiredText() {
    return bookingLoginRequiredText;
  }

  public void setBookingLoginRequiredText(String bookingLoginRequiredText) {
    this.bookingLoginRequiredText = bookingLoginRequiredText;
  }

  public BusinessDto bookingsInvalidEmailMsg(String bookingsInvalidEmailMsg) {
    this.bookingsInvalidEmailMsg = bookingsInvalidEmailMsg;
    return this;
  }

  /**
   * Get bookingsInvalidEmailMsg
   * @return bookingsInvalidEmailMsg
   */
  
  @Schema(name = "bookingsInvalidEmailMsg", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingsInvalidEmailMsg")
  public String getBookingsInvalidEmailMsg() {
    return bookingsInvalidEmailMsg;
  }

  public void setBookingsInvalidEmailMsg(String bookingsInvalidEmailMsg) {
    this.bookingsInvalidEmailMsg = bookingsInvalidEmailMsg;
  }

  public BusinessDto businessAccountLinks(List<@Valid BusinessAccountLinkDto> businessAccountLinks) {
    this.businessAccountLinks = businessAccountLinks;
    return this;
  }

  public BusinessDto addBusinessAccountLinksItem(BusinessAccountLinkDto businessAccountLinksItem) {
    if (this.businessAccountLinks == null) {
      this.businessAccountLinks = new ArrayList<>();
    }
    this.businessAccountLinks.add(businessAccountLinksItem);
    return this;
  }

  /**
   * Get businessAccountLinks
   * @return businessAccountLinks
   */
  @Valid 
  @Schema(name = "businessAccountLinks", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("businessAccountLinks")
  public List<@Valid BusinessAccountLinkDto> getBusinessAccountLinks() {
    return businessAccountLinks;
  }

  public void setBusinessAccountLinks(List<@Valid BusinessAccountLinkDto> businessAccountLinks) {
    this.businessAccountLinks = businessAccountLinks;
  }

  public BusinessDto companyActivateFailBody(String companyActivateFailBody) {
    this.companyActivateFailBody = companyActivateFailBody;
    return this;
  }

  /**
   * Get companyActivateFailBody
   * @return companyActivateFailBody
   */
  
  @Schema(name = "companyActivateFailBody", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyActivateFailBody")
  public String getCompanyActivateFailBody() {
    return companyActivateFailBody;
  }

  public void setCompanyActivateFailBody(String companyActivateFailBody) {
    this.companyActivateFailBody = companyActivateFailBody;
  }

  public BusinessDto companyActivateFailTitle(String companyActivateFailTitle) {
    this.companyActivateFailTitle = companyActivateFailTitle;
    return this;
  }

  /**
   * Get companyActivateFailTitle
   * @return companyActivateFailTitle
   */
  
  @Schema(name = "companyActivateFailTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyActivateFailTitle")
  public String getCompanyActivateFailTitle() {
    return companyActivateFailTitle;
  }

  public void setCompanyActivateFailTitle(String companyActivateFailTitle) {
    this.companyActivateFailTitle = companyActivateFailTitle;
  }

  public BusinessDto companyActivateSuccessBody(String companyActivateSuccessBody) {
    this.companyActivateSuccessBody = companyActivateSuccessBody;
    return this;
  }

  /**
   * Get companyActivateSuccessBody
   * @return companyActivateSuccessBody
   */
  
  @Schema(name = "companyActivateSuccessBody", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyActivateSuccessBody")
  public String getCompanyActivateSuccessBody() {
    return companyActivateSuccessBody;
  }

  public void setCompanyActivateSuccessBody(String companyActivateSuccessBody) {
    this.companyActivateSuccessBody = companyActivateSuccessBody;
  }

  public BusinessDto companyActivateSuccessTitle(String companyActivateSuccessTitle) {
    this.companyActivateSuccessTitle = companyActivateSuccessTitle;
    return this;
  }

  /**
   * Get companyActivateSuccessTitle
   * @return companyActivateSuccessTitle
   */
  
  @Schema(name = "companyActivateSuccessTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyActivateSuccessTitle")
  public String getCompanyActivateSuccessTitle() {
    return companyActivateSuccessTitle;
  }

  public void setCompanyActivateSuccessTitle(String companyActivateSuccessTitle) {
    this.companyActivateSuccessTitle = companyActivateSuccessTitle;
  }

  public BusinessDto doubleOptInFailMessage(String doubleOptInFailMessage) {
    this.doubleOptInFailMessage = doubleOptInFailMessage;
    return this;
  }

  /**
   * Get doubleOptInFailMessage
   * @return doubleOptInFailMessage
   */
  
  @Schema(name = "doubleOptInFailMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("doubleOptInFailMessage")
  public String getDoubleOptInFailMessage() {
    return doubleOptInFailMessage;
  }

  public void setDoubleOptInFailMessage(String doubleOptInFailMessage) {
    this.doubleOptInFailMessage = doubleOptInFailMessage;
  }

  public BusinessDto doubleOptInSuccessMessage(String doubleOptInSuccessMessage) {
    this.doubleOptInSuccessMessage = doubleOptInSuccessMessage;
    return this;
  }

  /**
   * Get doubleOptInSuccessMessage
   * @return doubleOptInSuccessMessage
   */
  
  @Schema(name = "doubleOptInSuccessMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("doubleOptInSuccessMessage")
  public String getDoubleOptInSuccessMessage() {
    return doubleOptInSuccessMessage;
  }

  public void setDoubleOptInSuccessMessage(String doubleOptInSuccessMessage) {
    this.doubleOptInSuccessMessage = doubleOptInSuccessMessage;
  }

  public BusinessDto emailPlaceholder(String emailPlaceholder) {
    this.emailPlaceholder = emailPlaceholder;
    return this;
  }

  /**
   * Get emailPlaceholder
   * @return emailPlaceholder
   */
  
  @Schema(name = "emailPlaceholder", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emailPlaceholder")
  public String getEmailPlaceholder() {
    return emailPlaceholder;
  }

  public void setEmailPlaceholder(String emailPlaceholder) {
    this.emailPlaceholder = emailPlaceholder;
  }

  public BusinessDto employeeActivationSuccessTitle(String employeeActivationSuccessTitle) {
    this.employeeActivationSuccessTitle = employeeActivationSuccessTitle;
    return this;
  }

  /**
   * Get employeeActivationSuccessTitle
   * @return employeeActivationSuccessTitle
   */
  
  @Schema(name = "employeeActivationSuccessTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("employeeActivationSuccessTitle")
  public String getEmployeeActivationSuccessTitle() {
    return employeeActivationSuccessTitle;
  }

  public void setEmployeeActivationSuccessTitle(String employeeActivationSuccessTitle) {
    this.employeeActivationSuccessTitle = employeeActivationSuccessTitle;
  }

  public BusinessDto forgotPasswordLink(String forgotPasswordLink) {
    this.forgotPasswordLink = forgotPasswordLink;
    return this;
  }

  /**
   * Get forgotPasswordLink
   * @return forgotPasswordLink
   */
  
  @Schema(name = "forgotPasswordLink", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("forgotPasswordLink")
  public String getForgotPasswordLink() {
    return forgotPasswordLink;
  }

  public void setForgotPasswordLink(String forgotPasswordLink) {
    this.forgotPasswordLink = forgotPasswordLink;
  }

  public BusinessDto formLabel(String formLabel) {
    this.formLabel = formLabel;
    return this;
  }

  /**
   * Get formLabel
   * @return formLabel
   */
  
  @Schema(name = "formLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("formLabel")
  public String getFormLabel() {
    return formLabel;
  }

  public void setFormLabel(String formLabel) {
    this.formLabel = formLabel;
  }

  public BusinessDto formTitle(String formTitle) {
    this.formTitle = formTitle;
    return this;
  }

  /**
   * Get formTitle
   * @return formTitle
   */
  
  @Schema(name = "formTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("formTitle")
  public String getFormTitle() {
    return formTitle;
  }

  public void setFormTitle(String formTitle) {
    this.formTitle = formTitle;
  }

  public BusinessDto loginButton(String loginButton) {
    this.loginButton = loginButton;
    return this;
  }

  /**
   * Get loginButton
   * @return loginButton
   */
  
  @Schema(name = "loginButton", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("loginButton")
  public String getLoginButton() {
    return loginButton;
  }

  public void setLoginButton(String loginButton) {
    this.loginButton = loginButton;
  }

  public BusinessDto loginInfoNotification(String loginInfoNotification) {
    this.loginInfoNotification = loginInfoNotification;
    return this;
  }

  /**
   * Get loginInfoNotification
   * @return loginInfoNotification
   */
  
  @Schema(name = "loginInfoNotification", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("loginInfoNotification")
  public String getLoginInfoNotification() {
    return loginInfoNotification;
  }

  public void setLoginInfoNotification(String loginInfoNotification) {
    this.loginInfoNotification = loginInfoNotification;
  }

  public BusinessDto signupButton(String signupButton) {
    this.signupButton = signupButton;
    return this;
  }

  /**
   * Get signupButton
   * @return signupButton
   */
  
  @Schema(name = "signupButton", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("signupButton")
  public String getSignupButton() {
    return signupButton;
  }

  public void setSignupButton(String signupButton) {
    this.signupButton = signupButton;
  }

  public BusinessDto signupUrl(String signupUrl) {
    this.signupUrl = signupUrl;
    return this;
  }

  /**
   * Get signupUrl
   * @return signupUrl
   */
  
  @Schema(name = "signupUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("signupUrl")
  public String getSignupUrl() {
    return signupUrl;
  }

  public void setSignupUrl(String signupUrl) {
    this.signupUrl = signupUrl;
  }

  public BusinessDto submitButton(String submitButton) {
    this.submitButton = submitButton;
    return this;
  }

  /**
   * Get submitButton
   * @return submitButton
   */
  
  @Schema(name = "submitButton", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("submitButton")
  public String getSubmitButton() {
    return submitButton;
  }

  public void setSubmitButton(String submitButton) {
    this.submitButton = submitButton;
  }

  public BusinessDto tab(String tab) {
    this.tab = tab;
    return this;
  }

  /**
   * Get tab
   * @return tab
   */
  
  @Schema(name = "tab", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tab")
  public String getTab() {
    return tab;
  }

  public void setTab(String tab) {
    this.tab = tab;
  }

  public BusinessDto tabMobile(String tabMobile) {
    this.tabMobile = tabMobile;
    return this;
  }

  /**
   * Get tabMobile
   * @return tabMobile
   */
  
  @Schema(name = "tabMobile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tabMobile")
  public String getTabMobile() {
    return tabMobile;
  }

  public void setTabMobile(String tabMobile) {
    this.tabMobile = tabMobile;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BusinessDto businessDto = (BusinessDto) o;
    return Objects.equals(this.bookingDashboardRedirectPath, businessDto.bookingDashboardRedirectPath) &&
        Objects.equals(this.bookingEmailMaxLengthMsg, businessDto.bookingEmailMaxLengthMsg) &&
        Objects.equals(this.bookingLoginRequiredText, businessDto.bookingLoginRequiredText) &&
        Objects.equals(this.bookingsInvalidEmailMsg, businessDto.bookingsInvalidEmailMsg) &&
        Objects.equals(this.businessAccountLinks, businessDto.businessAccountLinks) &&
        Objects.equals(this.companyActivateFailBody, businessDto.companyActivateFailBody) &&
        Objects.equals(this.companyActivateFailTitle, businessDto.companyActivateFailTitle) &&
        Objects.equals(this.companyActivateSuccessBody, businessDto.companyActivateSuccessBody) &&
        Objects.equals(this.companyActivateSuccessTitle, businessDto.companyActivateSuccessTitle) &&
        Objects.equals(this.doubleOptInFailMessage, businessDto.doubleOptInFailMessage) &&
        Objects.equals(this.doubleOptInSuccessMessage, businessDto.doubleOptInSuccessMessage) &&
        Objects.equals(this.emailPlaceholder, businessDto.emailPlaceholder) &&
        Objects.equals(this.employeeActivationSuccessTitle, businessDto.employeeActivationSuccessTitle) &&
        Objects.equals(this.forgotPasswordLink, businessDto.forgotPasswordLink) &&
        Objects.equals(this.formLabel, businessDto.formLabel) &&
        Objects.equals(this.formTitle, businessDto.formTitle) &&
        Objects.equals(this.loginButton, businessDto.loginButton) &&
        Objects.equals(this.loginInfoNotification, businessDto.loginInfoNotification) &&
        Objects.equals(this.signupButton, businessDto.signupButton) &&
        Objects.equals(this.signupUrl, businessDto.signupUrl) &&
        Objects.equals(this.submitButton, businessDto.submitButton) &&
        Objects.equals(this.tab, businessDto.tab) &&
        Objects.equals(this.tabMobile, businessDto.tabMobile);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingDashboardRedirectPath, bookingEmailMaxLengthMsg, bookingLoginRequiredText, bookingsInvalidEmailMsg, businessAccountLinks, companyActivateFailBody, companyActivateFailTitle, companyActivateSuccessBody, companyActivateSuccessTitle, doubleOptInFailMessage, doubleOptInSuccessMessage, emailPlaceholder, employeeActivationSuccessTitle, forgotPasswordLink, formLabel, formTitle, loginButton, loginInfoNotification, signupButton, signupUrl, submitButton, tab, tabMobile);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BusinessDto {\n");
    sb.append("    bookingDashboardRedirectPath: ").append(toIndentedString(bookingDashboardRedirectPath)).append("\n");
    sb.append("    bookingEmailMaxLengthMsg: ").append(toIndentedString(bookingEmailMaxLengthMsg)).append("\n");
    sb.append("    bookingLoginRequiredText: ").append(toIndentedString(bookingLoginRequiredText)).append("\n");
    sb.append("    bookingsInvalidEmailMsg: ").append(toIndentedString(bookingsInvalidEmailMsg)).append("\n");
    sb.append("    businessAccountLinks: ").append(toIndentedString(businessAccountLinks)).append("\n");
    sb.append("    companyActivateFailBody: ").append(toIndentedString(companyActivateFailBody)).append("\n");
    sb.append("    companyActivateFailTitle: ").append(toIndentedString(companyActivateFailTitle)).append("\n");
    sb.append("    companyActivateSuccessBody: ").append(toIndentedString(companyActivateSuccessBody)).append("\n");
    sb.append("    companyActivateSuccessTitle: ").append(toIndentedString(companyActivateSuccessTitle)).append("\n");
    sb.append("    doubleOptInFailMessage: ").append(toIndentedString(doubleOptInFailMessage)).append("\n");
    sb.append("    doubleOptInSuccessMessage: ").append(toIndentedString(doubleOptInSuccessMessage)).append("\n");
    sb.append("    emailPlaceholder: ").append(toIndentedString(emailPlaceholder)).append("\n");
    sb.append("    employeeActivationSuccessTitle: ").append(toIndentedString(employeeActivationSuccessTitle)).append("\n");
    sb.append("    forgotPasswordLink: ").append(toIndentedString(forgotPasswordLink)).append("\n");
    sb.append("    formLabel: ").append(toIndentedString(formLabel)).append("\n");
    sb.append("    formTitle: ").append(toIndentedString(formTitle)).append("\n");
    sb.append("    loginButton: ").append(toIndentedString(loginButton)).append("\n");
    sb.append("    loginInfoNotification: ").append(toIndentedString(loginInfoNotification)).append("\n");
    sb.append("    signupButton: ").append(toIndentedString(signupButton)).append("\n");
    sb.append("    signupUrl: ").append(toIndentedString(signupUrl)).append("\n");
    sb.append("    submitButton: ").append(toIndentedString(submitButton)).append("\n");
    sb.append("    tab: ").append(toIndentedString(tab)).append("\n");
    sb.append("    tabMobile: ").append(toIndentedString(tabMobile)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}


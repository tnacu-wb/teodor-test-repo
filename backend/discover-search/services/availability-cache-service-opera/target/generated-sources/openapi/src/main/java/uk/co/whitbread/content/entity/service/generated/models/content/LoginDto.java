package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.BusinessAccountCardDto;
import uk.co.whitbread.content.entity.service.generated.models.content.BusinessDto;
import uk.co.whitbread.content.entity.service.generated.models.content.LeisureDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * LoginDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class LoginDto {

  private @Nullable String badCredentialsError;

  private @Nullable BusinessDto business;

  private @Nullable BusinessAccountCardDto businessAccountCard;

  private @Nullable String forgotPassword;

  private @Nullable String invalidEmail;

  private @Nullable LeisureDto leisure;

  private @Nullable String passwordPlaceholder;

  private @Nullable String rememberMeLabel;

  private @Nullable String sessionExpired;

  private @Nullable String signupLink;

  private @Nullable String signupMessage;

  public LoginDto badCredentialsError(String badCredentialsError) {
    this.badCredentialsError = badCredentialsError;
    return this;
  }

  /**
   * Get badCredentialsError
   * @return badCredentialsError
   */
  
  @Schema(name = "badCredentialsError", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("badCredentialsError")
  public String getBadCredentialsError() {
    return badCredentialsError;
  }

  public void setBadCredentialsError(String badCredentialsError) {
    this.badCredentialsError = badCredentialsError;
  }

  public LoginDto business(BusinessDto business) {
    this.business = business;
    return this;
  }

  /**
   * Get business
   * @return business
   */
  @Valid 
  @Schema(name = "business", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("business")
  public BusinessDto getBusiness() {
    return business;
  }

  public void setBusiness(BusinessDto business) {
    this.business = business;
  }

  public LoginDto businessAccountCard(BusinessAccountCardDto businessAccountCard) {
    this.businessAccountCard = businessAccountCard;
    return this;
  }

  /**
   * Get businessAccountCard
   * @return businessAccountCard
   */
  @Valid 
  @Schema(name = "businessAccountCard", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("businessAccountCard")
  public BusinessAccountCardDto getBusinessAccountCard() {
    return businessAccountCard;
  }

  public void setBusinessAccountCard(BusinessAccountCardDto businessAccountCard) {
    this.businessAccountCard = businessAccountCard;
  }

  public LoginDto forgotPassword(String forgotPassword) {
    this.forgotPassword = forgotPassword;
    return this;
  }

  /**
   * Get forgotPassword
   * @return forgotPassword
   */
  
  @Schema(name = "forgotPassword", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("forgotPassword")
  public String getForgotPassword() {
    return forgotPassword;
  }

  public void setForgotPassword(String forgotPassword) {
    this.forgotPassword = forgotPassword;
  }

  public LoginDto invalidEmail(String invalidEmail) {
    this.invalidEmail = invalidEmail;
    return this;
  }

  /**
   * Get invalidEmail
   * @return invalidEmail
   */
  
  @Schema(name = "invalidEmail", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("invalidEmail")
  public String getInvalidEmail() {
    return invalidEmail;
  }

  public void setInvalidEmail(String invalidEmail) {
    this.invalidEmail = invalidEmail;
  }

  public LoginDto leisure(LeisureDto leisure) {
    this.leisure = leisure;
    return this;
  }

  /**
   * Get leisure
   * @return leisure
   */
  @Valid 
  @Schema(name = "leisure", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("leisure")
  public LeisureDto getLeisure() {
    return leisure;
  }

  public void setLeisure(LeisureDto leisure) {
    this.leisure = leisure;
  }

  public LoginDto passwordPlaceholder(String passwordPlaceholder) {
    this.passwordPlaceholder = passwordPlaceholder;
    return this;
  }

  /**
   * Get passwordPlaceholder
   * @return passwordPlaceholder
   */
  
  @Schema(name = "passwordPlaceholder", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("passwordPlaceholder")
  public String getPasswordPlaceholder() {
    return passwordPlaceholder;
  }

  public void setPasswordPlaceholder(String passwordPlaceholder) {
    this.passwordPlaceholder = passwordPlaceholder;
  }

  public LoginDto rememberMeLabel(String rememberMeLabel) {
    this.rememberMeLabel = rememberMeLabel;
    return this;
  }

  /**
   * Get rememberMeLabel
   * @return rememberMeLabel
   */
  
  @Schema(name = "rememberMeLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rememberMeLabel")
  public String getRememberMeLabel() {
    return rememberMeLabel;
  }

  public void setRememberMeLabel(String rememberMeLabel) {
    this.rememberMeLabel = rememberMeLabel;
  }

  public LoginDto sessionExpired(String sessionExpired) {
    this.sessionExpired = sessionExpired;
    return this;
  }

  /**
   * Get sessionExpired
   * @return sessionExpired
   */
  
  @Schema(name = "sessionExpired", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sessionExpired")
  public String getSessionExpired() {
    return sessionExpired;
  }

  public void setSessionExpired(String sessionExpired) {
    this.sessionExpired = sessionExpired;
  }

  public LoginDto signupLink(String signupLink) {
    this.signupLink = signupLink;
    return this;
  }

  /**
   * Get signupLink
   * @return signupLink
   */
  
  @Schema(name = "signupLink", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("signupLink")
  public String getSignupLink() {
    return signupLink;
  }

  public void setSignupLink(String signupLink) {
    this.signupLink = signupLink;
  }

  public LoginDto signupMessage(String signupMessage) {
    this.signupMessage = signupMessage;
    return this;
  }

  /**
   * Get signupMessage
   * @return signupMessage
   */
  
  @Schema(name = "signupMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("signupMessage")
  public String getSignupMessage() {
    return signupMessage;
  }

  public void setSignupMessage(String signupMessage) {
    this.signupMessage = signupMessage;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LoginDto loginDto = (LoginDto) o;
    return Objects.equals(this.badCredentialsError, loginDto.badCredentialsError) &&
        Objects.equals(this.business, loginDto.business) &&
        Objects.equals(this.businessAccountCard, loginDto.businessAccountCard) &&
        Objects.equals(this.forgotPassword, loginDto.forgotPassword) &&
        Objects.equals(this.invalidEmail, loginDto.invalidEmail) &&
        Objects.equals(this.leisure, loginDto.leisure) &&
        Objects.equals(this.passwordPlaceholder, loginDto.passwordPlaceholder) &&
        Objects.equals(this.rememberMeLabel, loginDto.rememberMeLabel) &&
        Objects.equals(this.sessionExpired, loginDto.sessionExpired) &&
        Objects.equals(this.signupLink, loginDto.signupLink) &&
        Objects.equals(this.signupMessage, loginDto.signupMessage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(badCredentialsError, business, businessAccountCard, forgotPassword, invalidEmail, leisure, passwordPlaceholder, rememberMeLabel, sessionExpired, signupLink, signupMessage);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LoginDto {\n");
    sb.append("    badCredentialsError: ").append(toIndentedString(badCredentialsError)).append("\n");
    sb.append("    business: ").append(toIndentedString(business)).append("\n");
    sb.append("    businessAccountCard: ").append(toIndentedString(businessAccountCard)).append("\n");
    sb.append("    forgotPassword: ").append(toIndentedString(forgotPassword)).append("\n");
    sb.append("    invalidEmail: ").append(toIndentedString(invalidEmail)).append("\n");
    sb.append("    leisure: ").append(toIndentedString(leisure)).append("\n");
    sb.append("    passwordPlaceholder: ").append(toIndentedString(passwordPlaceholder)).append("\n");
    sb.append("    rememberMeLabel: ").append(toIndentedString(rememberMeLabel)).append("\n");
    sb.append("    sessionExpired: ").append(toIndentedString(sessionExpired)).append("\n");
    sb.append("    signupLink: ").append(toIndentedString(signupLink)).append("\n");
    sb.append("    signupMessage: ").append(toIndentedString(signupMessage)).append("\n");
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


package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ResetPasswordDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:46.057591+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ResetPasswordDto {

  private @Nullable String criteria;

  private @Nullable String criteriaDescription;

  private @Nullable String email;

  private @Nullable String invalidPassword;

  private @Nullable String invalidPasswordConfirmation;

  private @Nullable String logInButton;

  private @Nullable String password;

  private @Nullable String passwordConfirmation;

  private @Nullable String passwordMin;

  private @Nullable String passwordRequired;

  private @Nullable String passwordRequirementsAllowed;

  private @Nullable String passwordRequirementsIdentical;

  private @Nullable String passwordRequirementsMin;

  private @Nullable String resetPasswordTitle;

  private @Nullable String submitButton;

  private @Nullable String successMessage;

  public ResetPasswordDto criteria(String criteria) {
    this.criteria = criteria;
    return this;
  }

  /**
   * Get criteria
   * @return criteria
   */
  
  @Schema(name = "criteria", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("criteria")
  public String getCriteria() {
    return criteria;
  }

  public void setCriteria(String criteria) {
    this.criteria = criteria;
  }

  public ResetPasswordDto criteriaDescription(String criteriaDescription) {
    this.criteriaDescription = criteriaDescription;
    return this;
  }

  /**
   * Get criteriaDescription
   * @return criteriaDescription
   */
  
  @Schema(name = "criteriaDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("criteriaDescription")
  public String getCriteriaDescription() {
    return criteriaDescription;
  }

  public void setCriteriaDescription(String criteriaDescription) {
    this.criteriaDescription = criteriaDescription;
  }

  public ResetPasswordDto email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
   */
  
  @Schema(name = "email", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public ResetPasswordDto invalidPassword(String invalidPassword) {
    this.invalidPassword = invalidPassword;
    return this;
  }

  /**
   * Get invalidPassword
   * @return invalidPassword
   */
  
  @Schema(name = "invalidPassword", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("invalidPassword")
  public String getInvalidPassword() {
    return invalidPassword;
  }

  public void setInvalidPassword(String invalidPassword) {
    this.invalidPassword = invalidPassword;
  }

  public ResetPasswordDto invalidPasswordConfirmation(String invalidPasswordConfirmation) {
    this.invalidPasswordConfirmation = invalidPasswordConfirmation;
    return this;
  }

  /**
   * Get invalidPasswordConfirmation
   * @return invalidPasswordConfirmation
   */
  
  @Schema(name = "invalidPasswordConfirmation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("invalidPasswordConfirmation")
  public String getInvalidPasswordConfirmation() {
    return invalidPasswordConfirmation;
  }

  public void setInvalidPasswordConfirmation(String invalidPasswordConfirmation) {
    this.invalidPasswordConfirmation = invalidPasswordConfirmation;
  }

  public ResetPasswordDto logInButton(String logInButton) {
    this.logInButton = logInButton;
    return this;
  }

  /**
   * Get logInButton
   * @return logInButton
   */
  
  @Schema(name = "logInButton", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("logInButton")
  public String getLogInButton() {
    return logInButton;
  }

  public void setLogInButton(String logInButton) {
    this.logInButton = logInButton;
  }

  public ResetPasswordDto password(String password) {
    this.password = password;
    return this;
  }

  /**
   * Get password
   * @return password
   */
  
  @Schema(name = "password", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("password")
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public ResetPasswordDto passwordConfirmation(String passwordConfirmation) {
    this.passwordConfirmation = passwordConfirmation;
    return this;
  }

  /**
   * Get passwordConfirmation
   * @return passwordConfirmation
   */
  
  @Schema(name = "passwordConfirmation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("passwordConfirmation")
  public String getPasswordConfirmation() {
    return passwordConfirmation;
  }

  public void setPasswordConfirmation(String passwordConfirmation) {
    this.passwordConfirmation = passwordConfirmation;
  }

  public ResetPasswordDto passwordMin(String passwordMin) {
    this.passwordMin = passwordMin;
    return this;
  }

  /**
   * Get passwordMin
   * @return passwordMin
   */
  
  @Schema(name = "passwordMin", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("passwordMin")
  public String getPasswordMin() {
    return passwordMin;
  }

  public void setPasswordMin(String passwordMin) {
    this.passwordMin = passwordMin;
  }

  public ResetPasswordDto passwordRequired(String passwordRequired) {
    this.passwordRequired = passwordRequired;
    return this;
  }

  /**
   * Get passwordRequired
   * @return passwordRequired
   */
  
  @Schema(name = "passwordRequired", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("passwordRequired")
  public String getPasswordRequired() {
    return passwordRequired;
  }

  public void setPasswordRequired(String passwordRequired) {
    this.passwordRequired = passwordRequired;
  }

  public ResetPasswordDto passwordRequirementsAllowed(String passwordRequirementsAllowed) {
    this.passwordRequirementsAllowed = passwordRequirementsAllowed;
    return this;
  }

  /**
   * Get passwordRequirementsAllowed
   * @return passwordRequirementsAllowed
   */
  
  @Schema(name = "passwordRequirementsAllowed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("passwordRequirementsAllowed")
  public String getPasswordRequirementsAllowed() {
    return passwordRequirementsAllowed;
  }

  public void setPasswordRequirementsAllowed(String passwordRequirementsAllowed) {
    this.passwordRequirementsAllowed = passwordRequirementsAllowed;
  }

  public ResetPasswordDto passwordRequirementsIdentical(String passwordRequirementsIdentical) {
    this.passwordRequirementsIdentical = passwordRequirementsIdentical;
    return this;
  }

  /**
   * Get passwordRequirementsIdentical
   * @return passwordRequirementsIdentical
   */
  
  @Schema(name = "passwordRequirementsIdentical", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("passwordRequirementsIdentical")
  public String getPasswordRequirementsIdentical() {
    return passwordRequirementsIdentical;
  }

  public void setPasswordRequirementsIdentical(String passwordRequirementsIdentical) {
    this.passwordRequirementsIdentical = passwordRequirementsIdentical;
  }

  public ResetPasswordDto passwordRequirementsMin(String passwordRequirementsMin) {
    this.passwordRequirementsMin = passwordRequirementsMin;
    return this;
  }

  /**
   * Get passwordRequirementsMin
   * @return passwordRequirementsMin
   */
  
  @Schema(name = "passwordRequirementsMin", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("passwordRequirementsMin")
  public String getPasswordRequirementsMin() {
    return passwordRequirementsMin;
  }

  public void setPasswordRequirementsMin(String passwordRequirementsMin) {
    this.passwordRequirementsMin = passwordRequirementsMin;
  }

  public ResetPasswordDto resetPasswordTitle(String resetPasswordTitle) {
    this.resetPasswordTitle = resetPasswordTitle;
    return this;
  }

  /**
   * Get resetPasswordTitle
   * @return resetPasswordTitle
   */
  
  @Schema(name = "resetPasswordTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("resetPasswordTitle")
  public String getResetPasswordTitle() {
    return resetPasswordTitle;
  }

  public void setResetPasswordTitle(String resetPasswordTitle) {
    this.resetPasswordTitle = resetPasswordTitle;
  }

  public ResetPasswordDto submitButton(String submitButton) {
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

  public ResetPasswordDto successMessage(String successMessage) {
    this.successMessage = successMessage;
    return this;
  }

  /**
   * Get successMessage
   * @return successMessage
   */
  
  @Schema(name = "successMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("successMessage")
  public String getSuccessMessage() {
    return successMessage;
  }

  public void setSuccessMessage(String successMessage) {
    this.successMessage = successMessage;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResetPasswordDto resetPasswordDto = (ResetPasswordDto) o;
    return Objects.equals(this.criteria, resetPasswordDto.criteria) &&
        Objects.equals(this.criteriaDescription, resetPasswordDto.criteriaDescription) &&
        Objects.equals(this.email, resetPasswordDto.email) &&
        Objects.equals(this.invalidPassword, resetPasswordDto.invalidPassword) &&
        Objects.equals(this.invalidPasswordConfirmation, resetPasswordDto.invalidPasswordConfirmation) &&
        Objects.equals(this.logInButton, resetPasswordDto.logInButton) &&
        Objects.equals(this.password, resetPasswordDto.password) &&
        Objects.equals(this.passwordConfirmation, resetPasswordDto.passwordConfirmation) &&
        Objects.equals(this.passwordMin, resetPasswordDto.passwordMin) &&
        Objects.equals(this.passwordRequired, resetPasswordDto.passwordRequired) &&
        Objects.equals(this.passwordRequirementsAllowed, resetPasswordDto.passwordRequirementsAllowed) &&
        Objects.equals(this.passwordRequirementsIdentical, resetPasswordDto.passwordRequirementsIdentical) &&
        Objects.equals(this.passwordRequirementsMin, resetPasswordDto.passwordRequirementsMin) &&
        Objects.equals(this.resetPasswordTitle, resetPasswordDto.resetPasswordTitle) &&
        Objects.equals(this.submitButton, resetPasswordDto.submitButton) &&
        Objects.equals(this.successMessage, resetPasswordDto.successMessage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(criteria, criteriaDescription, email, invalidPassword, invalidPasswordConfirmation, logInButton, password, passwordConfirmation, passwordMin, passwordRequired, passwordRequirementsAllowed, passwordRequirementsIdentical, passwordRequirementsMin, resetPasswordTitle, submitButton, successMessage);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResetPasswordDto {\n");
    sb.append("    criteria: ").append(toIndentedString(criteria)).append("\n");
    sb.append("    criteriaDescription: ").append(toIndentedString(criteriaDescription)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    invalidPassword: ").append(toIndentedString(invalidPassword)).append("\n");
    sb.append("    invalidPasswordConfirmation: ").append(toIndentedString(invalidPasswordConfirmation)).append("\n");
    sb.append("    logInButton: ").append(toIndentedString(logInButton)).append("\n");
    sb.append("    password: ").append(toIndentedString(password)).append("\n");
    sb.append("    passwordConfirmation: ").append(toIndentedString(passwordConfirmation)).append("\n");
    sb.append("    passwordMin: ").append(toIndentedString(passwordMin)).append("\n");
    sb.append("    passwordRequired: ").append(toIndentedString(passwordRequired)).append("\n");
    sb.append("    passwordRequirementsAllowed: ").append(toIndentedString(passwordRequirementsAllowed)).append("\n");
    sb.append("    passwordRequirementsIdentical: ").append(toIndentedString(passwordRequirementsIdentical)).append("\n");
    sb.append("    passwordRequirementsMin: ").append(toIndentedString(passwordRequirementsMin)).append("\n");
    sb.append("    resetPasswordTitle: ").append(toIndentedString(resetPasswordTitle)).append("\n");
    sb.append("    submitButton: ").append(toIndentedString(submitButton)).append("\n");
    sb.append("    successMessage: ").append(toIndentedString(successMessage)).append("\n");
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


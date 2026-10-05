package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * NewsletterSignupDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class NewsletterSignupDto {

  private @Nullable String bookStayButtonText;

  private @Nullable String bookStayButtonUrl;

  private @Nullable String confirmationText;

  private @Nullable String countrySelectEmptyFieldError;

  private @Nullable String countrySelectLabel;

  private @Nullable String doubleOptInText;

  private @Nullable String doubleOptInTitle;

  private @Nullable String emailLabel;

  private @Nullable String emailMaxLengthError;

  private @Nullable String emailMinLengthError;

  private @Nullable String emailValidError;

  private @Nullable String firstNameLabel;

  private @Nullable String firstNameMaxLengthError;

  private @Nullable String firstNameMinLengthError;

  private @Nullable String firstNameValidError;

  private @Nullable String introViewText;

  private @Nullable String introViewTitle;

  private @Nullable String lastNameLabel;

  private @Nullable String lastNameMaxLengthError;

  private @Nullable String lastNameMinLengthError;

  private @Nullable String lastNameValidError;

  private @Nullable String newsletterButtonLabel;

  private @Nullable String newsletterText;

  private @Nullable String privacyPolicyText;

  private @Nullable String serverError;

  private @Nullable String signUpButtonText;

  private @Nullable String signupTitle;

  public NewsletterSignupDto bookStayButtonText(String bookStayButtonText) {
    this.bookStayButtonText = bookStayButtonText;
    return this;
  }

  /**
   * Get bookStayButtonText
   * @return bookStayButtonText
   */
  
  @Schema(name = "bookStayButtonText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookStayButtonText")
  public String getBookStayButtonText() {
    return bookStayButtonText;
  }

  public void setBookStayButtonText(String bookStayButtonText) {
    this.bookStayButtonText = bookStayButtonText;
  }

  public NewsletterSignupDto bookStayButtonUrl(String bookStayButtonUrl) {
    this.bookStayButtonUrl = bookStayButtonUrl;
    return this;
  }

  /**
   * Get bookStayButtonUrl
   * @return bookStayButtonUrl
   */
  
  @Schema(name = "bookStayButtonUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookStayButtonUrl")
  public String getBookStayButtonUrl() {
    return bookStayButtonUrl;
  }

  public void setBookStayButtonUrl(String bookStayButtonUrl) {
    this.bookStayButtonUrl = bookStayButtonUrl;
  }

  public NewsletterSignupDto confirmationText(String confirmationText) {
    this.confirmationText = confirmationText;
    return this;
  }

  /**
   * Get confirmationText
   * @return confirmationText
   */
  
  @Schema(name = "confirmationText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("confirmationText")
  public String getConfirmationText() {
    return confirmationText;
  }

  public void setConfirmationText(String confirmationText) {
    this.confirmationText = confirmationText;
  }

  public NewsletterSignupDto countrySelectEmptyFieldError(String countrySelectEmptyFieldError) {
    this.countrySelectEmptyFieldError = countrySelectEmptyFieldError;
    return this;
  }

  /**
   * Get countrySelectEmptyFieldError
   * @return countrySelectEmptyFieldError
   */
  
  @Schema(name = "countrySelectEmptyFieldError", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("countrySelectEmptyFieldError")
  public String getCountrySelectEmptyFieldError() {
    return countrySelectEmptyFieldError;
  }

  public void setCountrySelectEmptyFieldError(String countrySelectEmptyFieldError) {
    this.countrySelectEmptyFieldError = countrySelectEmptyFieldError;
  }

  public NewsletterSignupDto countrySelectLabel(String countrySelectLabel) {
    this.countrySelectLabel = countrySelectLabel;
    return this;
  }

  /**
   * Get countrySelectLabel
   * @return countrySelectLabel
   */
  
  @Schema(name = "countrySelectLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("countrySelectLabel")
  public String getCountrySelectLabel() {
    return countrySelectLabel;
  }

  public void setCountrySelectLabel(String countrySelectLabel) {
    this.countrySelectLabel = countrySelectLabel;
  }

  public NewsletterSignupDto doubleOptInText(String doubleOptInText) {
    this.doubleOptInText = doubleOptInText;
    return this;
  }

  /**
   * Get doubleOptInText
   * @return doubleOptInText
   */
  
  @Schema(name = "doubleOptInText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("doubleOptInText")
  public String getDoubleOptInText() {
    return doubleOptInText;
  }

  public void setDoubleOptInText(String doubleOptInText) {
    this.doubleOptInText = doubleOptInText;
  }

  public NewsletterSignupDto doubleOptInTitle(String doubleOptInTitle) {
    this.doubleOptInTitle = doubleOptInTitle;
    return this;
  }

  /**
   * Get doubleOptInTitle
   * @return doubleOptInTitle
   */
  
  @Schema(name = "doubleOptInTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("doubleOptInTitle")
  public String getDoubleOptInTitle() {
    return doubleOptInTitle;
  }

  public void setDoubleOptInTitle(String doubleOptInTitle) {
    this.doubleOptInTitle = doubleOptInTitle;
  }

  public NewsletterSignupDto emailLabel(String emailLabel) {
    this.emailLabel = emailLabel;
    return this;
  }

  /**
   * Get emailLabel
   * @return emailLabel
   */
  
  @Schema(name = "emailLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emailLabel")
  public String getEmailLabel() {
    return emailLabel;
  }

  public void setEmailLabel(String emailLabel) {
    this.emailLabel = emailLabel;
  }

  public NewsletterSignupDto emailMaxLengthError(String emailMaxLengthError) {
    this.emailMaxLengthError = emailMaxLengthError;
    return this;
  }

  /**
   * Get emailMaxLengthError
   * @return emailMaxLengthError
   */
  
  @Schema(name = "emailMaxLengthError", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emailMaxLengthError")
  public String getEmailMaxLengthError() {
    return emailMaxLengthError;
  }

  public void setEmailMaxLengthError(String emailMaxLengthError) {
    this.emailMaxLengthError = emailMaxLengthError;
  }

  public NewsletterSignupDto emailMinLengthError(String emailMinLengthError) {
    this.emailMinLengthError = emailMinLengthError;
    return this;
  }

  /**
   * Get emailMinLengthError
   * @return emailMinLengthError
   */
  
  @Schema(name = "emailMinLengthError", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emailMinLengthError")
  public String getEmailMinLengthError() {
    return emailMinLengthError;
  }

  public void setEmailMinLengthError(String emailMinLengthError) {
    this.emailMinLengthError = emailMinLengthError;
  }

  public NewsletterSignupDto emailValidError(String emailValidError) {
    this.emailValidError = emailValidError;
    return this;
  }

  /**
   * Get emailValidError
   * @return emailValidError
   */
  
  @Schema(name = "emailValidError", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emailValidError")
  public String getEmailValidError() {
    return emailValidError;
  }

  public void setEmailValidError(String emailValidError) {
    this.emailValidError = emailValidError;
  }

  public NewsletterSignupDto firstNameLabel(String firstNameLabel) {
    this.firstNameLabel = firstNameLabel;
    return this;
  }

  /**
   * Get firstNameLabel
   * @return firstNameLabel
   */
  
  @Schema(name = "firstNameLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("firstNameLabel")
  public String getFirstNameLabel() {
    return firstNameLabel;
  }

  public void setFirstNameLabel(String firstNameLabel) {
    this.firstNameLabel = firstNameLabel;
  }

  public NewsletterSignupDto firstNameMaxLengthError(String firstNameMaxLengthError) {
    this.firstNameMaxLengthError = firstNameMaxLengthError;
    return this;
  }

  /**
   * Get firstNameMaxLengthError
   * @return firstNameMaxLengthError
   */
  
  @Schema(name = "firstNameMaxLengthError", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("firstNameMaxLengthError")
  public String getFirstNameMaxLengthError() {
    return firstNameMaxLengthError;
  }

  public void setFirstNameMaxLengthError(String firstNameMaxLengthError) {
    this.firstNameMaxLengthError = firstNameMaxLengthError;
  }

  public NewsletterSignupDto firstNameMinLengthError(String firstNameMinLengthError) {
    this.firstNameMinLengthError = firstNameMinLengthError;
    return this;
  }

  /**
   * Get firstNameMinLengthError
   * @return firstNameMinLengthError
   */
  
  @Schema(name = "firstNameMinLengthError", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("firstNameMinLengthError")
  public String getFirstNameMinLengthError() {
    return firstNameMinLengthError;
  }

  public void setFirstNameMinLengthError(String firstNameMinLengthError) {
    this.firstNameMinLengthError = firstNameMinLengthError;
  }

  public NewsletterSignupDto firstNameValidError(String firstNameValidError) {
    this.firstNameValidError = firstNameValidError;
    return this;
  }

  /**
   * Get firstNameValidError
   * @return firstNameValidError
   */
  
  @Schema(name = "firstNameValidError", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("firstNameValidError")
  public String getFirstNameValidError() {
    return firstNameValidError;
  }

  public void setFirstNameValidError(String firstNameValidError) {
    this.firstNameValidError = firstNameValidError;
  }

  public NewsletterSignupDto introViewText(String introViewText) {
    this.introViewText = introViewText;
    return this;
  }

  /**
   * Get introViewText
   * @return introViewText
   */
  
  @Schema(name = "introViewText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("introViewText")
  public String getIntroViewText() {
    return introViewText;
  }

  public void setIntroViewText(String introViewText) {
    this.introViewText = introViewText;
  }

  public NewsletterSignupDto introViewTitle(String introViewTitle) {
    this.introViewTitle = introViewTitle;
    return this;
  }

  /**
   * Get introViewTitle
   * @return introViewTitle
   */
  
  @Schema(name = "introViewTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("introViewTitle")
  public String getIntroViewTitle() {
    return introViewTitle;
  }

  public void setIntroViewTitle(String introViewTitle) {
    this.introViewTitle = introViewTitle;
  }

  public NewsletterSignupDto lastNameLabel(String lastNameLabel) {
    this.lastNameLabel = lastNameLabel;
    return this;
  }

  /**
   * Get lastNameLabel
   * @return lastNameLabel
   */
  
  @Schema(name = "lastNameLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastNameLabel")
  public String getLastNameLabel() {
    return lastNameLabel;
  }

  public void setLastNameLabel(String lastNameLabel) {
    this.lastNameLabel = lastNameLabel;
  }

  public NewsletterSignupDto lastNameMaxLengthError(String lastNameMaxLengthError) {
    this.lastNameMaxLengthError = lastNameMaxLengthError;
    return this;
  }

  /**
   * Get lastNameMaxLengthError
   * @return lastNameMaxLengthError
   */
  
  @Schema(name = "lastNameMaxLengthError", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastNameMaxLengthError")
  public String getLastNameMaxLengthError() {
    return lastNameMaxLengthError;
  }

  public void setLastNameMaxLengthError(String lastNameMaxLengthError) {
    this.lastNameMaxLengthError = lastNameMaxLengthError;
  }

  public NewsletterSignupDto lastNameMinLengthError(String lastNameMinLengthError) {
    this.lastNameMinLengthError = lastNameMinLengthError;
    return this;
  }

  /**
   * Get lastNameMinLengthError
   * @return lastNameMinLengthError
   */
  
  @Schema(name = "lastNameMinLengthError", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastNameMinLengthError")
  public String getLastNameMinLengthError() {
    return lastNameMinLengthError;
  }

  public void setLastNameMinLengthError(String lastNameMinLengthError) {
    this.lastNameMinLengthError = lastNameMinLengthError;
  }

  public NewsletterSignupDto lastNameValidError(String lastNameValidError) {
    this.lastNameValidError = lastNameValidError;
    return this;
  }

  /**
   * Get lastNameValidError
   * @return lastNameValidError
   */
  
  @Schema(name = "lastNameValidError", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastNameValidError")
  public String getLastNameValidError() {
    return lastNameValidError;
  }

  public void setLastNameValidError(String lastNameValidError) {
    this.lastNameValidError = lastNameValidError;
  }

  public NewsletterSignupDto newsletterButtonLabel(String newsletterButtonLabel) {
    this.newsletterButtonLabel = newsletterButtonLabel;
    return this;
  }

  /**
   * Get newsletterButtonLabel
   * @return newsletterButtonLabel
   */
  
  @Schema(name = "newsletterButtonLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("newsletterButtonLabel")
  public String getNewsletterButtonLabel() {
    return newsletterButtonLabel;
  }

  public void setNewsletterButtonLabel(String newsletterButtonLabel) {
    this.newsletterButtonLabel = newsletterButtonLabel;
  }

  public NewsletterSignupDto newsletterText(String newsletterText) {
    this.newsletterText = newsletterText;
    return this;
  }

  /**
   * Get newsletterText
   * @return newsletterText
   */
  
  @Schema(name = "newsletterText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("newsletterText")
  public String getNewsletterText() {
    return newsletterText;
  }

  public void setNewsletterText(String newsletterText) {
    this.newsletterText = newsletterText;
  }

  public NewsletterSignupDto privacyPolicyText(String privacyPolicyText) {
    this.privacyPolicyText = privacyPolicyText;
    return this;
  }

  /**
   * Get privacyPolicyText
   * @return privacyPolicyText
   */
  
  @Schema(name = "privacyPolicyText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("privacyPolicyText")
  public String getPrivacyPolicyText() {
    return privacyPolicyText;
  }

  public void setPrivacyPolicyText(String privacyPolicyText) {
    this.privacyPolicyText = privacyPolicyText;
  }

  public NewsletterSignupDto serverError(String serverError) {
    this.serverError = serverError;
    return this;
  }

  /**
   * Get serverError
   * @return serverError
   */
  
  @Schema(name = "serverError", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("serverError")
  public String getServerError() {
    return serverError;
  }

  public void setServerError(String serverError) {
    this.serverError = serverError;
  }

  public NewsletterSignupDto signUpButtonText(String signUpButtonText) {
    this.signUpButtonText = signUpButtonText;
    return this;
  }

  /**
   * Get signUpButtonText
   * @return signUpButtonText
   */
  
  @Schema(name = "signUpButtonText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("signUpButtonText")
  public String getSignUpButtonText() {
    return signUpButtonText;
  }

  public void setSignUpButtonText(String signUpButtonText) {
    this.signUpButtonText = signUpButtonText;
  }

  public NewsletterSignupDto signupTitle(String signupTitle) {
    this.signupTitle = signupTitle;
    return this;
  }

  /**
   * Get signupTitle
   * @return signupTitle
   */
  
  @Schema(name = "signupTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("signupTitle")
  public String getSignupTitle() {
    return signupTitle;
  }

  public void setSignupTitle(String signupTitle) {
    this.signupTitle = signupTitle;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NewsletterSignupDto newsletterSignupDto = (NewsletterSignupDto) o;
    return Objects.equals(this.bookStayButtonText, newsletterSignupDto.bookStayButtonText) &&
        Objects.equals(this.bookStayButtonUrl, newsletterSignupDto.bookStayButtonUrl) &&
        Objects.equals(this.confirmationText, newsletterSignupDto.confirmationText) &&
        Objects.equals(this.countrySelectEmptyFieldError, newsletterSignupDto.countrySelectEmptyFieldError) &&
        Objects.equals(this.countrySelectLabel, newsletterSignupDto.countrySelectLabel) &&
        Objects.equals(this.doubleOptInText, newsletterSignupDto.doubleOptInText) &&
        Objects.equals(this.doubleOptInTitle, newsletterSignupDto.doubleOptInTitle) &&
        Objects.equals(this.emailLabel, newsletterSignupDto.emailLabel) &&
        Objects.equals(this.emailMaxLengthError, newsletterSignupDto.emailMaxLengthError) &&
        Objects.equals(this.emailMinLengthError, newsletterSignupDto.emailMinLengthError) &&
        Objects.equals(this.emailValidError, newsletterSignupDto.emailValidError) &&
        Objects.equals(this.firstNameLabel, newsletterSignupDto.firstNameLabel) &&
        Objects.equals(this.firstNameMaxLengthError, newsletterSignupDto.firstNameMaxLengthError) &&
        Objects.equals(this.firstNameMinLengthError, newsletterSignupDto.firstNameMinLengthError) &&
        Objects.equals(this.firstNameValidError, newsletterSignupDto.firstNameValidError) &&
        Objects.equals(this.introViewText, newsletterSignupDto.introViewText) &&
        Objects.equals(this.introViewTitle, newsletterSignupDto.introViewTitle) &&
        Objects.equals(this.lastNameLabel, newsletterSignupDto.lastNameLabel) &&
        Objects.equals(this.lastNameMaxLengthError, newsletterSignupDto.lastNameMaxLengthError) &&
        Objects.equals(this.lastNameMinLengthError, newsletterSignupDto.lastNameMinLengthError) &&
        Objects.equals(this.lastNameValidError, newsletterSignupDto.lastNameValidError) &&
        Objects.equals(this.newsletterButtonLabel, newsletterSignupDto.newsletterButtonLabel) &&
        Objects.equals(this.newsletterText, newsletterSignupDto.newsletterText) &&
        Objects.equals(this.privacyPolicyText, newsletterSignupDto.privacyPolicyText) &&
        Objects.equals(this.serverError, newsletterSignupDto.serverError) &&
        Objects.equals(this.signUpButtonText, newsletterSignupDto.signUpButtonText) &&
        Objects.equals(this.signupTitle, newsletterSignupDto.signupTitle);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookStayButtonText, bookStayButtonUrl, confirmationText, countrySelectEmptyFieldError, countrySelectLabel, doubleOptInText, doubleOptInTitle, emailLabel, emailMaxLengthError, emailMinLengthError, emailValidError, firstNameLabel, firstNameMaxLengthError, firstNameMinLengthError, firstNameValidError, introViewText, introViewTitle, lastNameLabel, lastNameMaxLengthError, lastNameMinLengthError, lastNameValidError, newsletterButtonLabel, newsletterText, privacyPolicyText, serverError, signUpButtonText, signupTitle);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NewsletterSignupDto {\n");
    sb.append("    bookStayButtonText: ").append(toIndentedString(bookStayButtonText)).append("\n");
    sb.append("    bookStayButtonUrl: ").append(toIndentedString(bookStayButtonUrl)).append("\n");
    sb.append("    confirmationText: ").append(toIndentedString(confirmationText)).append("\n");
    sb.append("    countrySelectEmptyFieldError: ").append(toIndentedString(countrySelectEmptyFieldError)).append("\n");
    sb.append("    countrySelectLabel: ").append(toIndentedString(countrySelectLabel)).append("\n");
    sb.append("    doubleOptInText: ").append(toIndentedString(doubleOptInText)).append("\n");
    sb.append("    doubleOptInTitle: ").append(toIndentedString(doubleOptInTitle)).append("\n");
    sb.append("    emailLabel: ").append(toIndentedString(emailLabel)).append("\n");
    sb.append("    emailMaxLengthError: ").append(toIndentedString(emailMaxLengthError)).append("\n");
    sb.append("    emailMinLengthError: ").append(toIndentedString(emailMinLengthError)).append("\n");
    sb.append("    emailValidError: ").append(toIndentedString(emailValidError)).append("\n");
    sb.append("    firstNameLabel: ").append(toIndentedString(firstNameLabel)).append("\n");
    sb.append("    firstNameMaxLengthError: ").append(toIndentedString(firstNameMaxLengthError)).append("\n");
    sb.append("    firstNameMinLengthError: ").append(toIndentedString(firstNameMinLengthError)).append("\n");
    sb.append("    firstNameValidError: ").append(toIndentedString(firstNameValidError)).append("\n");
    sb.append("    introViewText: ").append(toIndentedString(introViewText)).append("\n");
    sb.append("    introViewTitle: ").append(toIndentedString(introViewTitle)).append("\n");
    sb.append("    lastNameLabel: ").append(toIndentedString(lastNameLabel)).append("\n");
    sb.append("    lastNameMaxLengthError: ").append(toIndentedString(lastNameMaxLengthError)).append("\n");
    sb.append("    lastNameMinLengthError: ").append(toIndentedString(lastNameMinLengthError)).append("\n");
    sb.append("    lastNameValidError: ").append(toIndentedString(lastNameValidError)).append("\n");
    sb.append("    newsletterButtonLabel: ").append(toIndentedString(newsletterButtonLabel)).append("\n");
    sb.append("    newsletterText: ").append(toIndentedString(newsletterText)).append("\n");
    sb.append("    privacyPolicyText: ").append(toIndentedString(privacyPolicyText)).append("\n");
    sb.append("    serverError: ").append(toIndentedString(serverError)).append("\n");
    sb.append("    signUpButtonText: ").append(toIndentedString(signUpButtonText)).append("\n");
    sb.append("    signupTitle: ").append(toIndentedString(signupTitle)).append("\n");
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


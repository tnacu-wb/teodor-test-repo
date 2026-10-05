package uk.co.whitbread.basket.generated.models.content;

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
 * LeisureDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:24.587145+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class LeisureDto {

  private @Nullable String emailPlaceholder;

  private @Nullable String formLabel;

  private @Nullable String formTitle;

  private @Nullable String loginButton;

  private @Nullable String submitButton;

  public LeisureDto emailPlaceholder(String emailPlaceholder) {
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

  public LeisureDto formLabel(String formLabel) {
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

  public LeisureDto formTitle(String formTitle) {
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

  public LeisureDto loginButton(String loginButton) {
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

  public LeisureDto submitButton(String submitButton) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LeisureDto leisureDto = (LeisureDto) o;
    return Objects.equals(this.emailPlaceholder, leisureDto.emailPlaceholder) &&
        Objects.equals(this.formLabel, leisureDto.formLabel) &&
        Objects.equals(this.formTitle, leisureDto.formTitle) &&
        Objects.equals(this.loginButton, leisureDto.loginButton) &&
        Objects.equals(this.submitButton, leisureDto.submitButton);
  }

  @Override
  public int hashCode() {
    return Objects.hash(emailPlaceholder, formLabel, formTitle, loginButton, submitButton);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LeisureDto {\n");
    sb.append("    emailPlaceholder: ").append(toIndentedString(emailPlaceholder)).append("\n");
    sb.append("    formLabel: ").append(toIndentedString(formLabel)).append("\n");
    sb.append("    formTitle: ").append(toIndentedString(formTitle)).append("\n");
    sb.append("    loginButton: ").append(toIndentedString(loginButton)).append("\n");
    sb.append("    submitButton: ").append(toIndentedString(submitButton)).append("\n");
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


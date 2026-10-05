package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.content.BusinessDto;
import uk.co.whitbread.basket.generated.models.content.LeisureDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * LoginDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:24.587145+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class LoginDto {

  private @Nullable BusinessDto business;

  private @Nullable String forgotPassword;

  private @Nullable LeisureDto leisure;

  private @Nullable String passwordPlaceholder;

  private @Nullable String signupLink;

  private @Nullable String signupMessage;

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
    return Objects.equals(this.business, loginDto.business) &&
        Objects.equals(this.forgotPassword, loginDto.forgotPassword) &&
        Objects.equals(this.leisure, loginDto.leisure) &&
        Objects.equals(this.passwordPlaceholder, loginDto.passwordPlaceholder) &&
        Objects.equals(this.signupLink, loginDto.signupLink) &&
        Objects.equals(this.signupMessage, loginDto.signupMessage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(business, forgotPassword, leisure, passwordPlaceholder, signupLink, signupMessage);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LoginDto {\n");
    sb.append("    business: ").append(toIndentedString(business)).append("\n");
    sb.append("    forgotPassword: ").append(toIndentedString(forgotPassword)).append("\n");
    sb.append("    leisure: ").append(toIndentedString(leisure)).append("\n");
    sb.append("    passwordPlaceholder: ").append(toIndentedString(passwordPlaceholder)).append("\n");
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


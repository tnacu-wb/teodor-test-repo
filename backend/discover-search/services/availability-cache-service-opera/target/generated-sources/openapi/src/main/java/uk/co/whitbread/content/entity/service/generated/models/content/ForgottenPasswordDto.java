package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
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
 * ForgottenPasswordDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ForgottenPasswordDto {

  private @Nullable String backToLogin;

  private @Nullable String backToYourDetails;

  private @Nullable BusinessDto business;

  private @Nullable String cancel;

  private @Nullable String emailSentHeader;

  private @Nullable String emailSentMessage;

  private @Nullable String genericError;

  private @Nullable LeisureDto leisure;

  private @Nullable String notRegisteredError;

  public ForgottenPasswordDto backToLogin(String backToLogin) {
    this.backToLogin = backToLogin;
    return this;
  }

  /**
   * Get backToLogin
   * @return backToLogin
   */
  
  @Schema(name = "backToLogin", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("backToLogin")
  public String getBackToLogin() {
    return backToLogin;
  }

  public void setBackToLogin(String backToLogin) {
    this.backToLogin = backToLogin;
  }

  public ForgottenPasswordDto backToYourDetails(String backToYourDetails) {
    this.backToYourDetails = backToYourDetails;
    return this;
  }

  /**
   * Get backToYourDetails
   * @return backToYourDetails
   */
  
  @Schema(name = "backToYourDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("backToYourDetails")
  public String getBackToYourDetails() {
    return backToYourDetails;
  }

  public void setBackToYourDetails(String backToYourDetails) {
    this.backToYourDetails = backToYourDetails;
  }

  public ForgottenPasswordDto business(BusinessDto business) {
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

  public ForgottenPasswordDto cancel(String cancel) {
    this.cancel = cancel;
    return this;
  }

  /**
   * Get cancel
   * @return cancel
   */
  
  @Schema(name = "cancel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cancel")
  public String getCancel() {
    return cancel;
  }

  public void setCancel(String cancel) {
    this.cancel = cancel;
  }

  public ForgottenPasswordDto emailSentHeader(String emailSentHeader) {
    this.emailSentHeader = emailSentHeader;
    return this;
  }

  /**
   * Get emailSentHeader
   * @return emailSentHeader
   */
  
  @Schema(name = "emailSentHeader", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emailSentHeader")
  public String getEmailSentHeader() {
    return emailSentHeader;
  }

  public void setEmailSentHeader(String emailSentHeader) {
    this.emailSentHeader = emailSentHeader;
  }

  public ForgottenPasswordDto emailSentMessage(String emailSentMessage) {
    this.emailSentMessage = emailSentMessage;
    return this;
  }

  /**
   * Get emailSentMessage
   * @return emailSentMessage
   */
  
  @Schema(name = "emailSentMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emailSentMessage")
  public String getEmailSentMessage() {
    return emailSentMessage;
  }

  public void setEmailSentMessage(String emailSentMessage) {
    this.emailSentMessage = emailSentMessage;
  }

  public ForgottenPasswordDto genericError(String genericError) {
    this.genericError = genericError;
    return this;
  }

  /**
   * Get genericError
   * @return genericError
   */
  
  @Schema(name = "genericError", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("genericError")
  public String getGenericError() {
    return genericError;
  }

  public void setGenericError(String genericError) {
    this.genericError = genericError;
  }

  public ForgottenPasswordDto leisure(LeisureDto leisure) {
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

  public ForgottenPasswordDto notRegisteredError(String notRegisteredError) {
    this.notRegisteredError = notRegisteredError;
    return this;
  }

  /**
   * Get notRegisteredError
   * @return notRegisteredError
   */
  
  @Schema(name = "notRegisteredError", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("notRegisteredError")
  public String getNotRegisteredError() {
    return notRegisteredError;
  }

  public void setNotRegisteredError(String notRegisteredError) {
    this.notRegisteredError = notRegisteredError;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ForgottenPasswordDto forgottenPasswordDto = (ForgottenPasswordDto) o;
    return Objects.equals(this.backToLogin, forgottenPasswordDto.backToLogin) &&
        Objects.equals(this.backToYourDetails, forgottenPasswordDto.backToYourDetails) &&
        Objects.equals(this.business, forgottenPasswordDto.business) &&
        Objects.equals(this.cancel, forgottenPasswordDto.cancel) &&
        Objects.equals(this.emailSentHeader, forgottenPasswordDto.emailSentHeader) &&
        Objects.equals(this.emailSentMessage, forgottenPasswordDto.emailSentMessage) &&
        Objects.equals(this.genericError, forgottenPasswordDto.genericError) &&
        Objects.equals(this.leisure, forgottenPasswordDto.leisure) &&
        Objects.equals(this.notRegisteredError, forgottenPasswordDto.notRegisteredError);
  }

  @Override
  public int hashCode() {
    return Objects.hash(backToLogin, backToYourDetails, business, cancel, emailSentHeader, emailSentMessage, genericError, leisure, notRegisteredError);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ForgottenPasswordDto {\n");
    sb.append("    backToLogin: ").append(toIndentedString(backToLogin)).append("\n");
    sb.append("    backToYourDetails: ").append(toIndentedString(backToYourDetails)).append("\n");
    sb.append("    business: ").append(toIndentedString(business)).append("\n");
    sb.append("    cancel: ").append(toIndentedString(cancel)).append("\n");
    sb.append("    emailSentHeader: ").append(toIndentedString(emailSentHeader)).append("\n");
    sb.append("    emailSentMessage: ").append(toIndentedString(emailSentMessage)).append("\n");
    sb.append("    genericError: ").append(toIndentedString(genericError)).append("\n");
    sb.append("    leisure: ").append(toIndentedString(leisure)).append("\n");
    sb.append("    notRegisteredError: ").append(toIndentedString(notRegisteredError)).append("\n");
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


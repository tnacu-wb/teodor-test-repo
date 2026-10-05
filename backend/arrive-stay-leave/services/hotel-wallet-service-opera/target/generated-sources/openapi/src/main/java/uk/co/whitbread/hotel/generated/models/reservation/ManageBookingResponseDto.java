package uk.co.whitbread.hotel.generated.models.reservation;

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
 * ManageBookingResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ManageBookingResponseDto {

  private @Nullable String aemLabelKey;

  private @Nullable String ciolErrorLabelKey;

  private @Nullable Boolean isAmendable;

  private @Nullable Boolean isCancellable;

  private @Nullable Boolean isCheckInOnlineAvailable;

  private @Nullable Boolean isCheckOutOnlineAvailable;

  private @Nullable Boolean isDigitalKey;

  private @Nullable Boolean isRuleCompliant;

  public ManageBookingResponseDto aemLabelKey(String aemLabelKey) {
    this.aemLabelKey = aemLabelKey;
    return this;
  }

  /**
   * Get aemLabelKey
   * @return aemLabelKey
   */
  
  @Schema(name = "aemLabelKey", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("aemLabelKey")
  public String getAemLabelKey() {
    return aemLabelKey;
  }

  public void setAemLabelKey(String aemLabelKey) {
    this.aemLabelKey = aemLabelKey;
  }

  public ManageBookingResponseDto ciolErrorLabelKey(String ciolErrorLabelKey) {
    this.ciolErrorLabelKey = ciolErrorLabelKey;
    return this;
  }

  /**
   * Get ciolErrorLabelKey
   * @return ciolErrorLabelKey
   */
  
  @Schema(name = "ciolErrorLabelKey", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ciolErrorLabelKey")
  public String getCiolErrorLabelKey() {
    return ciolErrorLabelKey;
  }

  public void setCiolErrorLabelKey(String ciolErrorLabelKey) {
    this.ciolErrorLabelKey = ciolErrorLabelKey;
  }

  public ManageBookingResponseDto isAmendable(Boolean isAmendable) {
    this.isAmendable = isAmendable;
    return this;
  }

  /**
   * Get isAmendable
   * @return isAmendable
   */
  
  @Schema(name = "isAmendable", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isAmendable")
  public Boolean getIsAmendable() {
    return isAmendable;
  }

  public void setIsAmendable(Boolean isAmendable) {
    this.isAmendable = isAmendable;
  }

  public ManageBookingResponseDto isCancellable(Boolean isCancellable) {
    this.isCancellable = isCancellable;
    return this;
  }

  /**
   * Get isCancellable
   * @return isCancellable
   */
  
  @Schema(name = "isCancellable", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isCancellable")
  public Boolean getIsCancellable() {
    return isCancellable;
  }

  public void setIsCancellable(Boolean isCancellable) {
    this.isCancellable = isCancellable;
  }

  public ManageBookingResponseDto isCheckInOnlineAvailable(Boolean isCheckInOnlineAvailable) {
    this.isCheckInOnlineAvailable = isCheckInOnlineAvailable;
    return this;
  }

  /**
   * Get isCheckInOnlineAvailable
   * @return isCheckInOnlineAvailable
   */
  
  @Schema(name = "isCheckInOnlineAvailable", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isCheckInOnlineAvailable")
  public Boolean getIsCheckInOnlineAvailable() {
    return isCheckInOnlineAvailable;
  }

  public void setIsCheckInOnlineAvailable(Boolean isCheckInOnlineAvailable) {
    this.isCheckInOnlineAvailable = isCheckInOnlineAvailable;
  }

  public ManageBookingResponseDto isCheckOutOnlineAvailable(Boolean isCheckOutOnlineAvailable) {
    this.isCheckOutOnlineAvailable = isCheckOutOnlineAvailable;
    return this;
  }

  /**
   * Get isCheckOutOnlineAvailable
   * @return isCheckOutOnlineAvailable
   */
  
  @Schema(name = "isCheckOutOnlineAvailable", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isCheckOutOnlineAvailable")
  public Boolean getIsCheckOutOnlineAvailable() {
    return isCheckOutOnlineAvailable;
  }

  public void setIsCheckOutOnlineAvailable(Boolean isCheckOutOnlineAvailable) {
    this.isCheckOutOnlineAvailable = isCheckOutOnlineAvailable;
  }

  public ManageBookingResponseDto isDigitalKey(Boolean isDigitalKey) {
    this.isDigitalKey = isDigitalKey;
    return this;
  }

  /**
   * Get isDigitalKey
   * @return isDigitalKey
   */
  
  @Schema(name = "isDigitalKey", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isDigitalKey")
  public Boolean getIsDigitalKey() {
    return isDigitalKey;
  }

  public void setIsDigitalKey(Boolean isDigitalKey) {
    this.isDigitalKey = isDigitalKey;
  }

  public ManageBookingResponseDto isRuleCompliant(Boolean isRuleCompliant) {
    this.isRuleCompliant = isRuleCompliant;
    return this;
  }

  /**
   * Get isRuleCompliant
   * @return isRuleCompliant
   */
  
  @Schema(name = "isRuleCompliant", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isRuleCompliant")
  public Boolean getIsRuleCompliant() {
    return isRuleCompliant;
  }

  public void setIsRuleCompliant(Boolean isRuleCompliant) {
    this.isRuleCompliant = isRuleCompliant;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ManageBookingResponseDto manageBookingResponseDto = (ManageBookingResponseDto) o;
    return Objects.equals(this.aemLabelKey, manageBookingResponseDto.aemLabelKey) &&
        Objects.equals(this.ciolErrorLabelKey, manageBookingResponseDto.ciolErrorLabelKey) &&
        Objects.equals(this.isAmendable, manageBookingResponseDto.isAmendable) &&
        Objects.equals(this.isCancellable, manageBookingResponseDto.isCancellable) &&
        Objects.equals(this.isCheckInOnlineAvailable, manageBookingResponseDto.isCheckInOnlineAvailable) &&
        Objects.equals(this.isCheckOutOnlineAvailable, manageBookingResponseDto.isCheckOutOnlineAvailable) &&
        Objects.equals(this.isDigitalKey, manageBookingResponseDto.isDigitalKey) &&
        Objects.equals(this.isRuleCompliant, manageBookingResponseDto.isRuleCompliant);
  }

  @Override
  public int hashCode() {
    return Objects.hash(aemLabelKey, ciolErrorLabelKey, isAmendable, isCancellable, isCheckInOnlineAvailable, isCheckOutOnlineAvailable, isDigitalKey, isRuleCompliant);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ManageBookingResponseDto {\n");
    sb.append("    aemLabelKey: ").append(toIndentedString(aemLabelKey)).append("\n");
    sb.append("    ciolErrorLabelKey: ").append(toIndentedString(ciolErrorLabelKey)).append("\n");
    sb.append("    isAmendable: ").append(toIndentedString(isAmendable)).append("\n");
    sb.append("    isCancellable: ").append(toIndentedString(isCancellable)).append("\n");
    sb.append("    isCheckInOnlineAvailable: ").append(toIndentedString(isCheckInOnlineAvailable)).append("\n");
    sb.append("    isCheckOutOnlineAvailable: ").append(toIndentedString(isCheckOutOnlineAvailable)).append("\n");
    sb.append("    isDigitalKey: ").append(toIndentedString(isDigitalKey)).append("\n");
    sb.append("    isRuleCompliant: ").append(toIndentedString(isRuleCompliant)).append("\n");
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


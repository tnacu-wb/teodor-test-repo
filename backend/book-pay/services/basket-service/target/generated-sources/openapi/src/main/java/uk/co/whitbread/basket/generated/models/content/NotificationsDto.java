package uk.co.whitbread.basket.generated.models.content;

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
 * NotificationsDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:06.327224+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class NotificationsDto {

  private @Nullable String availabilitiesErrorMessage;

  private @Nullable String ccuiGroupBookingMessage;

  private @Nullable String errorTitle;

  private @Nullable String groupBookingHeader;

  private @Nullable String groupBookingMessage;

  private @Nullable String noResults;

  public NotificationsDto availabilitiesErrorMessage(String availabilitiesErrorMessage) {
    this.availabilitiesErrorMessage = availabilitiesErrorMessage;
    return this;
  }

  /**
   * Get availabilitiesErrorMessage
   * @return availabilitiesErrorMessage
   */
  
  @Schema(name = "availabilitiesErrorMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("availabilitiesErrorMessage")
  public String getAvailabilitiesErrorMessage() {
    return availabilitiesErrorMessage;
  }

  public void setAvailabilitiesErrorMessage(String availabilitiesErrorMessage) {
    this.availabilitiesErrorMessage = availabilitiesErrorMessage;
  }

  public NotificationsDto ccuiGroupBookingMessage(String ccuiGroupBookingMessage) {
    this.ccuiGroupBookingMessage = ccuiGroupBookingMessage;
    return this;
  }

  /**
   * Get ccuiGroupBookingMessage
   * @return ccuiGroupBookingMessage
   */
  
  @Schema(name = "ccuiGroupBookingMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ccuiGroupBookingMessage")
  public String getCcuiGroupBookingMessage() {
    return ccuiGroupBookingMessage;
  }

  public void setCcuiGroupBookingMessage(String ccuiGroupBookingMessage) {
    this.ccuiGroupBookingMessage = ccuiGroupBookingMessage;
  }

  public NotificationsDto errorTitle(String errorTitle) {
    this.errorTitle = errorTitle;
    return this;
  }

  /**
   * Get errorTitle
   * @return errorTitle
   */
  
  @Schema(name = "errorTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("errorTitle")
  public String getErrorTitle() {
    return errorTitle;
  }

  public void setErrorTitle(String errorTitle) {
    this.errorTitle = errorTitle;
  }

  public NotificationsDto groupBookingHeader(String groupBookingHeader) {
    this.groupBookingHeader = groupBookingHeader;
    return this;
  }

  /**
   * Get groupBookingHeader
   * @return groupBookingHeader
   */
  
  @Schema(name = "groupBookingHeader", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("groupBookingHeader")
  public String getGroupBookingHeader() {
    return groupBookingHeader;
  }

  public void setGroupBookingHeader(String groupBookingHeader) {
    this.groupBookingHeader = groupBookingHeader;
  }

  public NotificationsDto groupBookingMessage(String groupBookingMessage) {
    this.groupBookingMessage = groupBookingMessage;
    return this;
  }

  /**
   * Get groupBookingMessage
   * @return groupBookingMessage
   */
  
  @Schema(name = "groupBookingMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("groupBookingMessage")
  public String getGroupBookingMessage() {
    return groupBookingMessage;
  }

  public void setGroupBookingMessage(String groupBookingMessage) {
    this.groupBookingMessage = groupBookingMessage;
  }

  public NotificationsDto noResults(String noResults) {
    this.noResults = noResults;
    return this;
  }

  /**
   * Get noResults
   * @return noResults
   */
  
  @Schema(name = "noResults", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("noResults")
  public String getNoResults() {
    return noResults;
  }

  public void setNoResults(String noResults) {
    this.noResults = noResults;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NotificationsDto notificationsDto = (NotificationsDto) o;
    return Objects.equals(this.availabilitiesErrorMessage, notificationsDto.availabilitiesErrorMessage) &&
        Objects.equals(this.ccuiGroupBookingMessage, notificationsDto.ccuiGroupBookingMessage) &&
        Objects.equals(this.errorTitle, notificationsDto.errorTitle) &&
        Objects.equals(this.groupBookingHeader, notificationsDto.groupBookingHeader) &&
        Objects.equals(this.groupBookingMessage, notificationsDto.groupBookingMessage) &&
        Objects.equals(this.noResults, notificationsDto.noResults);
  }

  @Override
  public int hashCode() {
    return Objects.hash(availabilitiesErrorMessage, ccuiGroupBookingMessage, errorTitle, groupBookingHeader, groupBookingMessage, noResults);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NotificationsDto {\n");
    sb.append("    availabilitiesErrorMessage: ").append(toIndentedString(availabilitiesErrorMessage)).append("\n");
    sb.append("    ccuiGroupBookingMessage: ").append(toIndentedString(ccuiGroupBookingMessage)).append("\n");
    sb.append("    errorTitle: ").append(toIndentedString(errorTitle)).append("\n");
    sb.append("    groupBookingHeader: ").append(toIndentedString(groupBookingHeader)).append("\n");
    sb.append("    groupBookingMessage: ").append(toIndentedString(groupBookingMessage)).append("\n");
    sb.append("    noResults: ").append(toIndentedString(noResults)).append("\n");
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


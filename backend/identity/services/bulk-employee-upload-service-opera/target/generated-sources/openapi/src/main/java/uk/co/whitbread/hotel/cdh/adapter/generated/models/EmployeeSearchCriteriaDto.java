package uk.co.whitbread.hotel.cdh.adapter.generated.models;

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
 * EmployeeSearchCriteriaDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:35.320601+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class EmployeeSearchCriteriaDto {

  private String accessContext;

  private String accessedBy;

  private @Nullable String activationKey;

  private @Nullable String bartEmployeeId;

  private @Nullable String bartGuestHistoryNumber;

  private @Nullable String emailAddress;

  private @Nullable String globalCompanyId;

  private @Nullable String pageSize;

  private @Nullable String pageToken;

  public EmployeeSearchCriteriaDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EmployeeSearchCriteriaDto(String accessContext, String accessedBy) {
    this.accessContext = accessContext;
    this.accessedBy = accessedBy;
  }

  public EmployeeSearchCriteriaDto accessContext(String accessContext) {
    this.accessContext = accessContext;
    return this;
  }

  /**
   * Get accessContext
   * @return accessContext
   */
  @NotNull 
  @Schema(name = "accessContext", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("accessContext")
  public String getAccessContext() {
    return accessContext;
  }

  public void setAccessContext(String accessContext) {
    this.accessContext = accessContext;
  }

  public EmployeeSearchCriteriaDto accessedBy(String accessedBy) {
    this.accessedBy = accessedBy;
    return this;
  }

  /**
   * Get accessedBy
   * @return accessedBy
   */
  @NotNull 
  @Schema(name = "accessedBy", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("accessedBy")
  public String getAccessedBy() {
    return accessedBy;
  }

  public void setAccessedBy(String accessedBy) {
    this.accessedBy = accessedBy;
  }

  public EmployeeSearchCriteriaDto activationKey(String activationKey) {
    this.activationKey = activationKey;
    return this;
  }

  /**
   * Get activationKey
   * @return activationKey
   */
  
  @Schema(name = "activationKey", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("activationKey")
  public String getActivationKey() {
    return activationKey;
  }

  public void setActivationKey(String activationKey) {
    this.activationKey = activationKey;
  }

  public EmployeeSearchCriteriaDto bartEmployeeId(String bartEmployeeId) {
    this.bartEmployeeId = bartEmployeeId;
    return this;
  }

  /**
   * Get bartEmployeeId
   * @return bartEmployeeId
   */
  
  @Schema(name = "bartEmployeeId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bartEmployeeId")
  public String getBartEmployeeId() {
    return bartEmployeeId;
  }

  public void setBartEmployeeId(String bartEmployeeId) {
    this.bartEmployeeId = bartEmployeeId;
  }

  public EmployeeSearchCriteriaDto bartGuestHistoryNumber(String bartGuestHistoryNumber) {
    this.bartGuestHistoryNumber = bartGuestHistoryNumber;
    return this;
  }

  /**
   * Get bartGuestHistoryNumber
   * @return bartGuestHistoryNumber
   */
  
  @Schema(name = "bartGuestHistoryNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bartGuestHistoryNumber")
  public String getBartGuestHistoryNumber() {
    return bartGuestHistoryNumber;
  }

  public void setBartGuestHistoryNumber(String bartGuestHistoryNumber) {
    this.bartGuestHistoryNumber = bartGuestHistoryNumber;
  }

  public EmployeeSearchCriteriaDto emailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
    return this;
  }

  /**
   * Get emailAddress
   * @return emailAddress
   */
  
  @Schema(name = "emailAddress", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emailAddress")
  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public EmployeeSearchCriteriaDto globalCompanyId(String globalCompanyId) {
    this.globalCompanyId = globalCompanyId;
    return this;
  }

  /**
   * Get globalCompanyId
   * @return globalCompanyId
   */
  
  @Schema(name = "globalCompanyId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("globalCompanyId")
  public String getGlobalCompanyId() {
    return globalCompanyId;
  }

  public void setGlobalCompanyId(String globalCompanyId) {
    this.globalCompanyId = globalCompanyId;
  }

  public EmployeeSearchCriteriaDto pageSize(String pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  /**
   * Get pageSize
   * @return pageSize
   */
  
  @Schema(name = "pageSize", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageSize")
  public String getPageSize() {
    return pageSize;
  }

  public void setPageSize(String pageSize) {
    this.pageSize = pageSize;
  }

  public EmployeeSearchCriteriaDto pageToken(String pageToken) {
    this.pageToken = pageToken;
    return this;
  }

  /**
   * Get pageToken
   * @return pageToken
   */
  
  @Schema(name = "pageToken", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageToken")
  public String getPageToken() {
    return pageToken;
  }

  public void setPageToken(String pageToken) {
    this.pageToken = pageToken;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EmployeeSearchCriteriaDto employeeSearchCriteriaDto = (EmployeeSearchCriteriaDto) o;
    return Objects.equals(this.accessContext, employeeSearchCriteriaDto.accessContext) &&
        Objects.equals(this.accessedBy, employeeSearchCriteriaDto.accessedBy) &&
        Objects.equals(this.activationKey, employeeSearchCriteriaDto.activationKey) &&
        Objects.equals(this.bartEmployeeId, employeeSearchCriteriaDto.bartEmployeeId) &&
        Objects.equals(this.bartGuestHistoryNumber, employeeSearchCriteriaDto.bartGuestHistoryNumber) &&
        Objects.equals(this.emailAddress, employeeSearchCriteriaDto.emailAddress) &&
        Objects.equals(this.globalCompanyId, employeeSearchCriteriaDto.globalCompanyId) &&
        Objects.equals(this.pageSize, employeeSearchCriteriaDto.pageSize) &&
        Objects.equals(this.pageToken, employeeSearchCriteriaDto.pageToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessContext, accessedBy, activationKey, bartEmployeeId, bartGuestHistoryNumber, emailAddress, globalCompanyId, pageSize, pageToken);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EmployeeSearchCriteriaDto {\n");
    sb.append("    accessContext: ").append(toIndentedString(accessContext)).append("\n");
    sb.append("    accessedBy: ").append(toIndentedString(accessedBy)).append("\n");
    sb.append("    activationKey: ").append(toIndentedString(activationKey)).append("\n");
    sb.append("    bartEmployeeId: ").append(toIndentedString(bartEmployeeId)).append("\n");
    sb.append("    bartGuestHistoryNumber: ").append(toIndentedString(bartGuestHistoryNumber)).append("\n");
    sb.append("    emailAddress: ").append(toIndentedString(emailAddress)).append("\n");
    sb.append("    globalCompanyId: ").append(toIndentedString(globalCompanyId)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    pageToken: ").append(toIndentedString(pageToken)).append("\n");
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


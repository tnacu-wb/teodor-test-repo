package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models;

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
 * GetEmployeeRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:45.778333+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class GetEmployeeRequestDto {

  private String accessContext;

  private String accessedBy;

  private String companyAccountId;

  private String employeeAccountId;

  public GetEmployeeRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public GetEmployeeRequestDto(String accessContext, String accessedBy, String companyAccountId, String employeeAccountId) {
    this.accessContext = accessContext;
    this.accessedBy = accessedBy;
    this.companyAccountId = companyAccountId;
    this.employeeAccountId = employeeAccountId;
  }

  public GetEmployeeRequestDto accessContext(String accessContext) {
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

  public GetEmployeeRequestDto accessedBy(String accessedBy) {
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

  public GetEmployeeRequestDto companyAccountId(String companyAccountId) {
    this.companyAccountId = companyAccountId;
    return this;
  }

  /**
   * Get companyAccountId
   * @return companyAccountId
   */
  @NotNull 
  @Schema(name = "companyAccountId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("companyAccountId")
  public String getCompanyAccountId() {
    return companyAccountId;
  }

  public void setCompanyAccountId(String companyAccountId) {
    this.companyAccountId = companyAccountId;
  }

  public GetEmployeeRequestDto employeeAccountId(String employeeAccountId) {
    this.employeeAccountId = employeeAccountId;
    return this;
  }

  /**
   * Get employeeAccountId
   * @return employeeAccountId
   */
  @NotNull 
  @Schema(name = "employeeAccountId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("employeeAccountId")
  public String getEmployeeAccountId() {
    return employeeAccountId;
  }

  public void setEmployeeAccountId(String employeeAccountId) {
    this.employeeAccountId = employeeAccountId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GetEmployeeRequestDto getEmployeeRequestDto = (GetEmployeeRequestDto) o;
    return Objects.equals(this.accessContext, getEmployeeRequestDto.accessContext) &&
        Objects.equals(this.accessedBy, getEmployeeRequestDto.accessedBy) &&
        Objects.equals(this.companyAccountId, getEmployeeRequestDto.companyAccountId) &&
        Objects.equals(this.employeeAccountId, getEmployeeRequestDto.employeeAccountId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessContext, accessedBy, companyAccountId, employeeAccountId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetEmployeeRequestDto {\n");
    sb.append("    accessContext: ").append(toIndentedString(accessContext)).append("\n");
    sb.append("    accessedBy: ").append(toIndentedString(accessedBy)).append("\n");
    sb.append("    companyAccountId: ").append(toIndentedString(companyAccountId)).append("\n");
    sb.append("    employeeAccountId: ").append(toIndentedString(employeeAccountId)).append("\n");
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


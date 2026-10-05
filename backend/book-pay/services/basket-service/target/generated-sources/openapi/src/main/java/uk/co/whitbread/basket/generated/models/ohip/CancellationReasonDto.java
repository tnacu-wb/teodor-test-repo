package uk.co.whitbread.basket.generated.models.ohip;

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
 * CancellationReasonDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CancellationReasonDto {

  private @Nullable Boolean active;

  private @Nullable String code;

  private @Nullable String description;

  private @Nullable Boolean managerApprovalNeeded;

  private @Nullable String name;

  public CancellationReasonDto active(Boolean active) {
    this.active = active;
    return this;
  }

  /**
   * Get active
   * @return active
   */
  
  @Schema(name = "active", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("active")
  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public CancellationReasonDto code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
   */
  
  @Schema(name = "code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public CancellationReasonDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public CancellationReasonDto managerApprovalNeeded(Boolean managerApprovalNeeded) {
    this.managerApprovalNeeded = managerApprovalNeeded;
    return this;
  }

  /**
   * Get managerApprovalNeeded
   * @return managerApprovalNeeded
   */
  
  @Schema(name = "managerApprovalNeeded", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("managerApprovalNeeded")
  public Boolean getManagerApprovalNeeded() {
    return managerApprovalNeeded;
  }

  public void setManagerApprovalNeeded(Boolean managerApprovalNeeded) {
    this.managerApprovalNeeded = managerApprovalNeeded;
  }

  public CancellationReasonDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  
  @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CancellationReasonDto cancellationReasonDto = (CancellationReasonDto) o;
    return Objects.equals(this.active, cancellationReasonDto.active) &&
        Objects.equals(this.code, cancellationReasonDto.code) &&
        Objects.equals(this.description, cancellationReasonDto.description) &&
        Objects.equals(this.managerApprovalNeeded, cancellationReasonDto.managerApprovalNeeded) &&
        Objects.equals(this.name, cancellationReasonDto.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(active, code, description, managerApprovalNeeded, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CancellationReasonDto {\n");
    sb.append("    active: ").append(toIndentedString(active)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    managerApprovalNeeded: ").append(toIndentedString(managerApprovalNeeded)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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


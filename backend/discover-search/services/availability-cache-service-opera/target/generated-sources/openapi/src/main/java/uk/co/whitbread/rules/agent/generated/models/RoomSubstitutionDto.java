package uk.co.whitbread.rules.agent.generated.models;

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
 * RoomSubstitutionDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:44.384518+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomSubstitutionDto {

  private @Nullable String accessibleSpecialRequest;

  private @Nullable String codePackage;

  private @Nullable Boolean silent;

  private @Nullable String specialRequest;

  private @Nullable String type;

  public RoomSubstitutionDto accessibleSpecialRequest(String accessibleSpecialRequest) {
    this.accessibleSpecialRequest = accessibleSpecialRequest;
    return this;
  }

  /**
   * Get accessibleSpecialRequest
   * @return accessibleSpecialRequest
   */
  
  @Schema(name = "accessibleSpecialRequest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accessibleSpecialRequest")
  public String getAccessibleSpecialRequest() {
    return accessibleSpecialRequest;
  }

  public void setAccessibleSpecialRequest(String accessibleSpecialRequest) {
    this.accessibleSpecialRequest = accessibleSpecialRequest;
  }

  public RoomSubstitutionDto codePackage(String codePackage) {
    this.codePackage = codePackage;
    return this;
  }

  /**
   * Get codePackage
   * @return codePackage
   */
  
  @Schema(name = "codePackage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("codePackage")
  public String getCodePackage() {
    return codePackage;
  }

  public void setCodePackage(String codePackage) {
    this.codePackage = codePackage;
  }

  public RoomSubstitutionDto silent(Boolean silent) {
    this.silent = silent;
    return this;
  }

  /**
   * Get silent
   * @return silent
   */
  
  @Schema(name = "silent", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("silent")
  public Boolean getSilent() {
    return silent;
  }

  public void setSilent(Boolean silent) {
    this.silent = silent;
  }

  public RoomSubstitutionDto specialRequest(String specialRequest) {
    this.specialRequest = specialRequest;
    return this;
  }

  /**
   * Get specialRequest
   * @return specialRequest
   */
  
  @Schema(name = "specialRequest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("specialRequest")
  public String getSpecialRequest() {
    return specialRequest;
  }

  public void setSpecialRequest(String specialRequest) {
    this.specialRequest = specialRequest;
  }

  public RoomSubstitutionDto type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   */
  
  @Schema(name = "type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomSubstitutionDto roomSubstitutionDto = (RoomSubstitutionDto) o;
    return Objects.equals(this.accessibleSpecialRequest, roomSubstitutionDto.accessibleSpecialRequest) &&
        Objects.equals(this.codePackage, roomSubstitutionDto.codePackage) &&
        Objects.equals(this.silent, roomSubstitutionDto.silent) &&
        Objects.equals(this.specialRequest, roomSubstitutionDto.specialRequest) &&
        Objects.equals(this.type, roomSubstitutionDto.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessibleSpecialRequest, codePackage, silent, specialRequest, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomSubstitutionDto {\n");
    sb.append("    accessibleSpecialRequest: ").append(toIndentedString(accessibleSpecialRequest)).append("\n");
    sb.append("    codePackage: ").append(toIndentedString(codePackage)).append("\n");
    sb.append("    silent: ").append(toIndentedString(silent)).append("\n");
    sb.append("    specialRequest: ").append(toIndentedString(specialRequest)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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


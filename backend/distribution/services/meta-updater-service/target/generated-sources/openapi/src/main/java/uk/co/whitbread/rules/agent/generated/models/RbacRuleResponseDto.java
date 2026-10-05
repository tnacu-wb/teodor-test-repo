package uk.co.whitbread.rules.agent.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RbacRuleResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:31.460652+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RbacRuleResponseDto {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable Date generatedAt;

  private @Nullable Boolean hasAccess;

  public RbacRuleResponseDto generatedAt(Date generatedAt) {
    this.generatedAt = generatedAt;
    return this;
  }

  /**
   * Get generatedAt
   * @return generatedAt
   */
  @Valid 
  @Schema(name = "generatedAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("generatedAt")
  public Date getGeneratedAt() {
    return generatedAt;
  }

  public void setGeneratedAt(Date generatedAt) {
    this.generatedAt = generatedAt;
  }

  public RbacRuleResponseDto hasAccess(Boolean hasAccess) {
    this.hasAccess = hasAccess;
    return this;
  }

  /**
   * Get hasAccess
   * @return hasAccess
   */
  
  @Schema(name = "hasAccess", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hasAccess")
  public Boolean getHasAccess() {
    return hasAccess;
  }

  public void setHasAccess(Boolean hasAccess) {
    this.hasAccess = hasAccess;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RbacRuleResponseDto rbacRuleResponseDto = (RbacRuleResponseDto) o;
    return Objects.equals(this.generatedAt, rbacRuleResponseDto.generatedAt) &&
        Objects.equals(this.hasAccess, rbacRuleResponseDto.hasAccess);
  }

  @Override
  public int hashCode() {
    return Objects.hash(generatedAt, hasAccess);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RbacRuleResponseDto {\n");
    sb.append("    generatedAt: ").append(toIndentedString(generatedAt)).append("\n");
    sb.append("    hasAccess: ").append(toIndentedString(hasAccess)).append("\n");
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


package uk.co.whitbread.ohip.generated.models;

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
 * KioskPreferenceDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class KioskPreferenceDto {

  private @Nullable String description;

  private @Nullable String preferenceValue;

  public KioskPreferenceDto description(String description) {
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

  public KioskPreferenceDto preferenceValue(String preferenceValue) {
    this.preferenceValue = preferenceValue;
    return this;
  }

  /**
   * Get preferenceValue
   * @return preferenceValue
   */
  
  @Schema(name = "preferenceValue", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("preferenceValue")
  public String getPreferenceValue() {
    return preferenceValue;
  }

  public void setPreferenceValue(String preferenceValue) {
    this.preferenceValue = preferenceValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    KioskPreferenceDto kioskPreferenceDto = (KioskPreferenceDto) o;
    return Objects.equals(this.description, kioskPreferenceDto.description) &&
        Objects.equals(this.preferenceValue, kioskPreferenceDto.preferenceValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, preferenceValue);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class KioskPreferenceDto {\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    preferenceValue: ").append(toIndentedString(preferenceValue)).append("\n");
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


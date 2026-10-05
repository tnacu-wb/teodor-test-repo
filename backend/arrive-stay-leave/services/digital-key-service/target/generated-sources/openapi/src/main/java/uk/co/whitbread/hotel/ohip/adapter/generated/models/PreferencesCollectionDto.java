package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PreferencesCollectionDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PreferencesCollectionDto {

  private String preferenceType;

  @Valid
  private List<String> preferences;

  public PreferencesCollectionDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PreferencesCollectionDto(String preferenceType, List<String> preferences) {
    this.preferenceType = preferenceType;
    this.preferences = preferences;
  }

  public PreferencesCollectionDto preferenceType(String preferenceType) {
    this.preferenceType = preferenceType;
    return this;
  }

  /**
   * Get preferenceType
   * @return preferenceType
   */
  @NotNull 
  @Schema(name = "preferenceType", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("preferenceType")
  public String getPreferenceType() {
    return preferenceType;
  }

  public void setPreferenceType(String preferenceType) {
    this.preferenceType = preferenceType;
  }

  public PreferencesCollectionDto preferences(List<String> preferences) {
    this.preferences = preferences;
    return this;
  }

  public PreferencesCollectionDto addPreferencesItem(String preferencesItem) {
    if (this.preferences == null) {
      this.preferences = new ArrayList<>();
    }
    this.preferences.add(preferencesItem);
    return this;
  }

  /**
   * Get preferences
   * @return preferences
   */
  @NotNull 
  @Schema(name = "preferences", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("preferences")
  public List<String> getPreferences() {
    return preferences;
  }

  public void setPreferences(List<String> preferences) {
    this.preferences = preferences;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PreferencesCollectionDto preferencesCollectionDto = (PreferencesCollectionDto) o;
    return Objects.equals(this.preferenceType, preferencesCollectionDto.preferenceType) &&
        Objects.equals(this.preferences, preferencesCollectionDto.preferences);
  }

  @Override
  public int hashCode() {
    return Objects.hash(preferenceType, preferences);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PreferencesCollectionDto {\n");
    sb.append("    preferenceType: ").append(toIndentedString(preferenceType)).append("\n");
    sb.append("    preferences: ").append(toIndentedString(preferences)).append("\n");
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


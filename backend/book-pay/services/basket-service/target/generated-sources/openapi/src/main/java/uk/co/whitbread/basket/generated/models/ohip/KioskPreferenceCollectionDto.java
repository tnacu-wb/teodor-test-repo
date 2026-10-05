package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.KioskPreferenceDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * KioskPreferenceCollectionDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class KioskPreferenceCollectionDto {

  @Valid
  private List<@Valid KioskPreferenceDto> kioskPreference = new ArrayList<>();

  private @Nullable String preferenceType;

  private @Nullable String preferenceTypeDescription;

  public KioskPreferenceCollectionDto kioskPreference(List<@Valid KioskPreferenceDto> kioskPreference) {
    this.kioskPreference = kioskPreference;
    return this;
  }

  public KioskPreferenceCollectionDto addKioskPreferenceItem(KioskPreferenceDto kioskPreferenceItem) {
    if (this.kioskPreference == null) {
      this.kioskPreference = new ArrayList<>();
    }
    this.kioskPreference.add(kioskPreferenceItem);
    return this;
  }

  /**
   * Get kioskPreference
   * @return kioskPreference
   */
  @Valid 
  @Schema(name = "kioskPreference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("kioskPreference")
  public List<@Valid KioskPreferenceDto> getKioskPreference() {
    return kioskPreference;
  }

  public void setKioskPreference(List<@Valid KioskPreferenceDto> kioskPreference) {
    this.kioskPreference = kioskPreference;
  }

  public KioskPreferenceCollectionDto preferenceType(String preferenceType) {
    this.preferenceType = preferenceType;
    return this;
  }

  /**
   * Get preferenceType
   * @return preferenceType
   */
  
  @Schema(name = "preferenceType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("preferenceType")
  public String getPreferenceType() {
    return preferenceType;
  }

  public void setPreferenceType(String preferenceType) {
    this.preferenceType = preferenceType;
  }

  public KioskPreferenceCollectionDto preferenceTypeDescription(String preferenceTypeDescription) {
    this.preferenceTypeDescription = preferenceTypeDescription;
    return this;
  }

  /**
   * Get preferenceTypeDescription
   * @return preferenceTypeDescription
   */
  
  @Schema(name = "preferenceTypeDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("preferenceTypeDescription")
  public String getPreferenceTypeDescription() {
    return preferenceTypeDescription;
  }

  public void setPreferenceTypeDescription(String preferenceTypeDescription) {
    this.preferenceTypeDescription = preferenceTypeDescription;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    KioskPreferenceCollectionDto kioskPreferenceCollectionDto = (KioskPreferenceCollectionDto) o;
    return Objects.equals(this.kioskPreference, kioskPreferenceCollectionDto.kioskPreference) &&
        Objects.equals(this.preferenceType, kioskPreferenceCollectionDto.preferenceType) &&
        Objects.equals(this.preferenceTypeDescription, kioskPreferenceCollectionDto.preferenceTypeDescription);
  }

  @Override
  public int hashCode() {
    return Objects.hash(kioskPreference, preferenceType, preferenceTypeDescription);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class KioskPreferenceCollectionDto {\n");
    sb.append("    kioskPreference: ").append(toIndentedString(kioskPreference)).append("\n");
    sb.append("    preferenceType: ").append(toIndentedString(preferenceType)).append("\n");
    sb.append("    preferenceTypeDescription: ").append(toIndentedString(preferenceTypeDescription)).append("\n");
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


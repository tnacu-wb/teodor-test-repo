package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.KioskPreferenceCollectionDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * KioskReservationPreferencesDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class KioskReservationPreferencesDto {

  @Valid
  private List<@Valid KioskPreferenceCollectionDto> kioskPreferenceCollection = new ArrayList<>();

  public KioskReservationPreferencesDto kioskPreferenceCollection(List<@Valid KioskPreferenceCollectionDto> kioskPreferenceCollection) {
    this.kioskPreferenceCollection = kioskPreferenceCollection;
    return this;
  }

  public KioskReservationPreferencesDto addKioskPreferenceCollectionItem(KioskPreferenceCollectionDto kioskPreferenceCollectionItem) {
    if (this.kioskPreferenceCollection == null) {
      this.kioskPreferenceCollection = new ArrayList<>();
    }
    this.kioskPreferenceCollection.add(kioskPreferenceCollectionItem);
    return this;
  }

  /**
   * Get kioskPreferenceCollection
   * @return kioskPreferenceCollection
   */
  @Valid 
  @Schema(name = "kioskPreferenceCollection", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("kioskPreferenceCollection")
  public List<@Valid KioskPreferenceCollectionDto> getKioskPreferenceCollection() {
    return kioskPreferenceCollection;
  }

  public void setKioskPreferenceCollection(List<@Valid KioskPreferenceCollectionDto> kioskPreferenceCollection) {
    this.kioskPreferenceCollection = kioskPreferenceCollection;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    KioskReservationPreferencesDto kioskReservationPreferencesDto = (KioskReservationPreferencesDto) o;
    return Objects.equals(this.kioskPreferenceCollection, kioskReservationPreferencesDto.kioskPreferenceCollection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(kioskPreferenceCollection);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class KioskReservationPreferencesDto {\n");
    sb.append("    kioskPreferenceCollection: ").append(toIndentedString(kioskPreferenceCollection)).append("\n");
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


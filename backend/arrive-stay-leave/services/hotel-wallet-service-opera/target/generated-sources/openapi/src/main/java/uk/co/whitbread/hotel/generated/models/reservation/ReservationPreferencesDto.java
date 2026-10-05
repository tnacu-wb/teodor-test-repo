package uk.co.whitbread.hotel.generated.models.reservation;

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
 * ReservationPreferencesDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationPreferencesDto {

  private @Nullable String code;

  private @Nullable String preferenceType;

  public ReservationPreferencesDto code(String code) {
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

  public ReservationPreferencesDto preferenceType(String preferenceType) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationPreferencesDto reservationPreferencesDto = (ReservationPreferencesDto) o;
    return Objects.equals(this.code, reservationPreferencesDto.code) &&
        Objects.equals(this.preferenceType, reservationPreferencesDto.preferenceType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, preferenceType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationPreferencesDto {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    preferenceType: ").append(toIndentedString(preferenceType)).append("\n");
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


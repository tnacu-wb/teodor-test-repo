package uk.co.whitbread.hotel.ocd.adapter.generated.models;

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
 * General information about the property.
 */

@Schema(name = "GeneralInformation", description = "General information about the property.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class GeneralInformation {

  private @Nullable String checkInTime;

  private @Nullable String checkOutTime;

  public GeneralInformation checkInTime(String checkInTime) {
    this.checkInTime = checkInTime;
    return this;
  }

  /**
   * The property's check-in time.
   * @return checkInTime
   */
  @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$") 
  @Schema(name = "checkInTime", example = "04:30", description = "The property's check-in time.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("checkInTime")
  public String getCheckInTime() {
    return checkInTime;
  }

  public void setCheckInTime(String checkInTime) {
    this.checkInTime = checkInTime;
  }

  public GeneralInformation checkOutTime(String checkOutTime) {
    this.checkOutTime = checkOutTime;
    return this;
  }

  /**
   * The property's check-out time.
   * @return checkOutTime
   */
  @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$") 
  @Schema(name = "checkOutTime", example = "14:30", description = "The property's check-out time.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("checkOutTime")
  public String getCheckOutTime() {
    return checkOutTime;
  }

  public void setCheckOutTime(String checkOutTime) {
    this.checkOutTime = checkOutTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GeneralInformation generalInformation = (GeneralInformation) o;
    return Objects.equals(this.checkInTime, generalInformation.checkInTime) &&
        Objects.equals(this.checkOutTime, generalInformation.checkOutTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(checkInTime, checkOutTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GeneralInformation {\n");
    sb.append("    checkInTime: ").append(toIndentedString(checkInTime)).append("\n");
    sb.append("    checkOutTime: ").append(toIndentedString(checkOutTime)).append("\n");
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


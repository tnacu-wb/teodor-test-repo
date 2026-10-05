package uk.co.whitbread.hotel.ocd.adapter.generated.models;

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
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * The deadline associated to the cancellation rule.
 */

@Schema(name = "CancelPolicyDeadlineType", description = "The deadline associated to the cancellation rule.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CancelPolicyDeadlineType {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable Date absoluteDeadline;

  private @Nullable Integer offsetFromArrival;

  private @Nullable Integer offsetFromBookingDate;

  public CancelPolicyDeadlineType absoluteDeadline(Date absoluteDeadline) {
    this.absoluteDeadline = absoluteDeadline;
    return this;
  }

  /**
   * The date and time the cancellation rule will take effect.
   * @return absoluteDeadline
   */
  @Valid 
  @Schema(name = "absoluteDeadline", example = "2021-04-01T15:46:40.134Z", description = "The date and time the cancellation rule will take effect.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("absoluteDeadline")
  public Date getAbsoluteDeadline() {
    return absoluteDeadline;
  }

  public void setAbsoluteDeadline(Date absoluteDeadline) {
    this.absoluteDeadline = absoluteDeadline;
  }

  public CancelPolicyDeadlineType offsetFromArrival(Integer offsetFromArrival) {
    this.offsetFromArrival = offsetFromArrival;
    return this;
  }

  /**
   * The number of days before the arrival date up to which the rate plan may be cancelled without penalty.
   * @return offsetFromArrival
   */
  
  @Schema(name = "offsetFromArrival", example = "5", description = "The number of days before the arrival date up to which the rate plan may be cancelled without penalty.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("offsetFromArrival")
  public Integer getOffsetFromArrival() {
    return offsetFromArrival;
  }

  public void setOffsetFromArrival(Integer offsetFromArrival) {
    this.offsetFromArrival = offsetFromArrival;
  }

  public CancelPolicyDeadlineType offsetFromBookingDate(Integer offsetFromBookingDate) {
    this.offsetFromBookingDate = offsetFromBookingDate;
    return this;
  }

  /**
   * The number of days after the booking date up to which the rate plan may be cancelled without penalty.
   * @return offsetFromBookingDate
   */
  
  @Schema(name = "offsetFromBookingDate", example = "5", description = "The number of days after the booking date up to which the rate plan may be cancelled without penalty.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("offsetFromBookingDate")
  public Integer getOffsetFromBookingDate() {
    return offsetFromBookingDate;
  }

  public void setOffsetFromBookingDate(Integer offsetFromBookingDate) {
    this.offsetFromBookingDate = offsetFromBookingDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CancelPolicyDeadlineType cancelPolicyDeadlineType = (CancelPolicyDeadlineType) o;
    return Objects.equals(this.absoluteDeadline, cancelPolicyDeadlineType.absoluteDeadline) &&
        Objects.equals(this.offsetFromArrival, cancelPolicyDeadlineType.offsetFromArrival) &&
        Objects.equals(this.offsetFromBookingDate, cancelPolicyDeadlineType.offsetFromBookingDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(absoluteDeadline, offsetFromArrival, offsetFromBookingDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CancelPolicyDeadlineType {\n");
    sb.append("    absoluteDeadline: ").append(toIndentedString(absoluteDeadline)).append("\n");
    sb.append("    offsetFromArrival: ").append(toIndentedString(offsetFromArrival)).append("\n");
    sb.append("    offsetFromBookingDate: ").append(toIndentedString(offsetFromBookingDate)).append("\n");
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


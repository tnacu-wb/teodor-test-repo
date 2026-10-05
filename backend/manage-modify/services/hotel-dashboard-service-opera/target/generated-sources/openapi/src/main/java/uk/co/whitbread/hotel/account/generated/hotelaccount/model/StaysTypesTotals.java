package uk.co.whitbread.hotel.account.generated.hotelaccount.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.jspecify.annotations.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * StaysTypesTotals
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:33.863804+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class StaysTypesTotals {

  private @Nullable Integer cancelled;

  private @Nullable Integer checkedIn;

  private @Nullable Integer past;

  private @Nullable Integer upcoming;

  public StaysTypesTotals cancelled(Integer cancelled) {
    this.cancelled = cancelled;
    return this;
  }

  /**
   * Get cancelled
   * @return cancelled
   */
  
  @Schema(name = "cancelled", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cancelled")
  public Integer getCancelled() {
    return cancelled;
  }

  public void setCancelled(Integer cancelled) {
    this.cancelled = cancelled;
  }

  public StaysTypesTotals checkedIn(Integer checkedIn) {
    this.checkedIn = checkedIn;
    return this;
  }

  /**
   * Get checkedIn
   * @return checkedIn
   */
  
  @Schema(name = "checkedIn", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("checkedIn")
  public Integer getCheckedIn() {
    return checkedIn;
  }

  public void setCheckedIn(Integer checkedIn) {
    this.checkedIn = checkedIn;
  }

  public StaysTypesTotals past(Integer past) {
    this.past = past;
    return this;
  }

  /**
   * Get past
   * @return past
   */
  
  @Schema(name = "past", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("past")
  public Integer getPast() {
    return past;
  }

  public void setPast(Integer past) {
    this.past = past;
  }

  public StaysTypesTotals upcoming(Integer upcoming) {
    this.upcoming = upcoming;
    return this;
  }

  /**
   * Get upcoming
   * @return upcoming
   */
  
  @Schema(name = "upcoming", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("upcoming")
  public Integer getUpcoming() {
    return upcoming;
  }

  public void setUpcoming(Integer upcoming) {
    this.upcoming = upcoming;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StaysTypesTotals staysTypesTotals = (StaysTypesTotals) o;
    return Objects.equals(this.cancelled, staysTypesTotals.cancelled) &&
        Objects.equals(this.checkedIn, staysTypesTotals.checkedIn) &&
        Objects.equals(this.past, staysTypesTotals.past) &&
        Objects.equals(this.upcoming, staysTypesTotals.upcoming);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cancelled, checkedIn, past, upcoming);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StaysTypesTotals {\n");
    sb.append("    cancelled: ").append(toIndentedString(cancelled)).append("\n");
    sb.append("    checkedIn: ").append(toIndentedString(checkedIn)).append("\n");
    sb.append("    past: ").append(toIndentedString(past)).append("\n");
    sb.append("    upcoming: ").append(toIndentedString(upcoming)).append("\n");
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


package uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch;

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
 * TotalsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-09T08:37:59.335673+03:00[Europe/Bucharest]", comments = "Generator version: 7.14.0")
public class TotalsDto {

  private @Nullable Integer cancelled;

  private @Nullable Integer checkedIn;

  private @Nullable Integer past;

  private @Nullable Integer upcoming;

  public TotalsDto cancelled(@Nullable Integer cancelled) {
    this.cancelled = cancelled;
    return this;
  }

  /**
   * Get cancelled
   * @return cancelled
   */
  
  @Schema(name = "cancelled", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cancelled")
  public @Nullable Integer getCancelled() {
    return cancelled;
  }

  public void setCancelled(@Nullable Integer cancelled) {
    this.cancelled = cancelled;
  }

  public TotalsDto checkedIn(@Nullable Integer checkedIn) {
    this.checkedIn = checkedIn;
    return this;
  }

  /**
   * Get checkedIn
   * @return checkedIn
   */
  
  @Schema(name = "checkedIn", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("checkedIn")
  public @Nullable Integer getCheckedIn() {
    return checkedIn;
  }

  public void setCheckedIn(@Nullable Integer checkedIn) {
    this.checkedIn = checkedIn;
  }

  public TotalsDto past(@Nullable Integer past) {
    this.past = past;
    return this;
  }

  /**
   * Get past
   * @return past
   */
  
  @Schema(name = "past", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("past")
  public @Nullable Integer getPast() {
    return past;
  }

  public void setPast(@Nullable Integer past) {
    this.past = past;
  }

  public TotalsDto upcoming(@Nullable Integer upcoming) {
    this.upcoming = upcoming;
    return this;
  }

  /**
   * Get upcoming
   * @return upcoming
   */
  
  @Schema(name = "upcoming", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("upcoming")
  public @Nullable Integer getUpcoming() {
    return upcoming;
  }

  public void setUpcoming(@Nullable Integer upcoming) {
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
    TotalsDto totalsDto = (TotalsDto) o;
    return Objects.equals(this.cancelled, totalsDto.cancelled) &&
        Objects.equals(this.checkedIn, totalsDto.checkedIn) &&
        Objects.equals(this.past, totalsDto.past) &&
        Objects.equals(this.upcoming, totalsDto.upcoming);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cancelled, checkedIn, past, upcoming);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TotalsDto {\n");
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


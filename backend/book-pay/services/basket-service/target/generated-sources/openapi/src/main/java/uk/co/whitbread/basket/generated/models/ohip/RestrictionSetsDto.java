package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.ActualTimeSpanDto;
import uk.co.whitbread.basket.generated.models.ohip.RestrictionControlDto;
import uk.co.whitbread.basket.generated.models.ohip.RestrictionStatusDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RestrictionSetsDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RestrictionSetsDto {

  private @Nullable ActualTimeSpanDto actualTimeSpan;

  private @Nullable String end;

  private @Nullable Boolean friday;

  private @Nullable Boolean monday;

  private @Nullable Boolean onRequest;

  private @Nullable RestrictionControlDto restrictionControl;

  private @Nullable RestrictionStatusDto restrictionStatus;

  private @Nullable Boolean saturday;

  private @Nullable String start;

  private @Nullable Boolean sunday;

  private @Nullable Boolean thursday;

  private @Nullable Boolean tuesday;

  private @Nullable Boolean wednesday;

  public RestrictionSetsDto actualTimeSpan(ActualTimeSpanDto actualTimeSpan) {
    this.actualTimeSpan = actualTimeSpan;
    return this;
  }

  /**
   * Get actualTimeSpan
   * @return actualTimeSpan
   */
  @Valid 
  @Schema(name = "actualTimeSpan", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("actualTimeSpan")
  public ActualTimeSpanDto getActualTimeSpan() {
    return actualTimeSpan;
  }

  public void setActualTimeSpan(ActualTimeSpanDto actualTimeSpan) {
    this.actualTimeSpan = actualTimeSpan;
  }

  public RestrictionSetsDto end(String end) {
    this.end = end;
    return this;
  }

  /**
   * Get end
   * @return end
   */
  
  @Schema(name = "end", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("end")
  public String getEnd() {
    return end;
  }

  public void setEnd(String end) {
    this.end = end;
  }

  public RestrictionSetsDto friday(Boolean friday) {
    this.friday = friday;
    return this;
  }

  /**
   * Get friday
   * @return friday
   */
  
  @Schema(name = "friday", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("friday")
  public Boolean getFriday() {
    return friday;
  }

  public void setFriday(Boolean friday) {
    this.friday = friday;
  }

  public RestrictionSetsDto monday(Boolean monday) {
    this.monday = monday;
    return this;
  }

  /**
   * Get monday
   * @return monday
   */
  
  @Schema(name = "monday", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("monday")
  public Boolean getMonday() {
    return monday;
  }

  public void setMonday(Boolean monday) {
    this.monday = monday;
  }

  public RestrictionSetsDto onRequest(Boolean onRequest) {
    this.onRequest = onRequest;
    return this;
  }

  /**
   * Get onRequest
   * @return onRequest
   */
  
  @Schema(name = "onRequest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("onRequest")
  public Boolean getOnRequest() {
    return onRequest;
  }

  public void setOnRequest(Boolean onRequest) {
    this.onRequest = onRequest;
  }

  public RestrictionSetsDto restrictionControl(RestrictionControlDto restrictionControl) {
    this.restrictionControl = restrictionControl;
    return this;
  }

  /**
   * Get restrictionControl
   * @return restrictionControl
   */
  @Valid 
  @Schema(name = "restrictionControl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("restrictionControl")
  public RestrictionControlDto getRestrictionControl() {
    return restrictionControl;
  }

  public void setRestrictionControl(RestrictionControlDto restrictionControl) {
    this.restrictionControl = restrictionControl;
  }

  public RestrictionSetsDto restrictionStatus(RestrictionStatusDto restrictionStatus) {
    this.restrictionStatus = restrictionStatus;
    return this;
  }

  /**
   * Get restrictionStatus
   * @return restrictionStatus
   */
  @Valid 
  @Schema(name = "restrictionStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("restrictionStatus")
  public RestrictionStatusDto getRestrictionStatus() {
    return restrictionStatus;
  }

  public void setRestrictionStatus(RestrictionStatusDto restrictionStatus) {
    this.restrictionStatus = restrictionStatus;
  }

  public RestrictionSetsDto saturday(Boolean saturday) {
    this.saturday = saturday;
    return this;
  }

  /**
   * Get saturday
   * @return saturday
   */
  
  @Schema(name = "saturday", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("saturday")
  public Boolean getSaturday() {
    return saturday;
  }

  public void setSaturday(Boolean saturday) {
    this.saturday = saturday;
  }

  public RestrictionSetsDto start(String start) {
    this.start = start;
    return this;
  }

  /**
   * Get start
   * @return start
   */
  
  @Schema(name = "start", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("start")
  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public RestrictionSetsDto sunday(Boolean sunday) {
    this.sunday = sunday;
    return this;
  }

  /**
   * Get sunday
   * @return sunday
   */
  
  @Schema(name = "sunday", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sunday")
  public Boolean getSunday() {
    return sunday;
  }

  public void setSunday(Boolean sunday) {
    this.sunday = sunday;
  }

  public RestrictionSetsDto thursday(Boolean thursday) {
    this.thursday = thursday;
    return this;
  }

  /**
   * Get thursday
   * @return thursday
   */
  
  @Schema(name = "thursday", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("thursday")
  public Boolean getThursday() {
    return thursday;
  }

  public void setThursday(Boolean thursday) {
    this.thursday = thursday;
  }

  public RestrictionSetsDto tuesday(Boolean tuesday) {
    this.tuesday = tuesday;
    return this;
  }

  /**
   * Get tuesday
   * @return tuesday
   */
  
  @Schema(name = "tuesday", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tuesday")
  public Boolean getTuesday() {
    return tuesday;
  }

  public void setTuesday(Boolean tuesday) {
    this.tuesday = tuesday;
  }

  public RestrictionSetsDto wednesday(Boolean wednesday) {
    this.wednesday = wednesday;
    return this;
  }

  /**
   * Get wednesday
   * @return wednesday
   */
  
  @Schema(name = "wednesday", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("wednesday")
  public Boolean getWednesday() {
    return wednesday;
  }

  public void setWednesday(Boolean wednesday) {
    this.wednesday = wednesday;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RestrictionSetsDto restrictionSetsDto = (RestrictionSetsDto) o;
    return Objects.equals(this.actualTimeSpan, restrictionSetsDto.actualTimeSpan) &&
        Objects.equals(this.end, restrictionSetsDto.end) &&
        Objects.equals(this.friday, restrictionSetsDto.friday) &&
        Objects.equals(this.monday, restrictionSetsDto.monday) &&
        Objects.equals(this.onRequest, restrictionSetsDto.onRequest) &&
        Objects.equals(this.restrictionControl, restrictionSetsDto.restrictionControl) &&
        Objects.equals(this.restrictionStatus, restrictionSetsDto.restrictionStatus) &&
        Objects.equals(this.saturday, restrictionSetsDto.saturday) &&
        Objects.equals(this.start, restrictionSetsDto.start) &&
        Objects.equals(this.sunday, restrictionSetsDto.sunday) &&
        Objects.equals(this.thursday, restrictionSetsDto.thursday) &&
        Objects.equals(this.tuesday, restrictionSetsDto.tuesday) &&
        Objects.equals(this.wednesday, restrictionSetsDto.wednesday);
  }

  @Override
  public int hashCode() {
    return Objects.hash(actualTimeSpan, end, friday, monday, onRequest, restrictionControl, restrictionStatus, saturday, start, sunday, thursday, tuesday, wednesday);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RestrictionSetsDto {\n");
    sb.append("    actualTimeSpan: ").append(toIndentedString(actualTimeSpan)).append("\n");
    sb.append("    end: ").append(toIndentedString(end)).append("\n");
    sb.append("    friday: ").append(toIndentedString(friday)).append("\n");
    sb.append("    monday: ").append(toIndentedString(monday)).append("\n");
    sb.append("    onRequest: ").append(toIndentedString(onRequest)).append("\n");
    sb.append("    restrictionControl: ").append(toIndentedString(restrictionControl)).append("\n");
    sb.append("    restrictionStatus: ").append(toIndentedString(restrictionStatus)).append("\n");
    sb.append("    saturday: ").append(toIndentedString(saturday)).append("\n");
    sb.append("    start: ").append(toIndentedString(start)).append("\n");
    sb.append("    sunday: ").append(toIndentedString(sunday)).append("\n");
    sb.append("    thursday: ").append(toIndentedString(thursday)).append("\n");
    sb.append("    tuesday: ").append(toIndentedString(tuesday)).append("\n");
    sb.append("    wednesday: ").append(toIndentedString(wednesday)).append("\n");
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


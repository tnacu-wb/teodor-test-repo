package uk.co.whitbread.hotel.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PackagesSelectionScheduledDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PackagesSelectionScheduledDto {

  private @Nullable String id;

  private @Nullable Integer noOfSelections;

  @Valid
  private List<LocalDate> scheduledDates = new ArrayList<>();

  public PackagesSelectionScheduledDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public PackagesSelectionScheduledDto noOfSelections(Integer noOfSelections) {
    this.noOfSelections = noOfSelections;
    return this;
  }

  /**
   * Get noOfSelections
   * @return noOfSelections
   */
  
  @Schema(name = "noOfSelections", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("noOfSelections")
  public Integer getNoOfSelections() {
    return noOfSelections;
  }

  public void setNoOfSelections(Integer noOfSelections) {
    this.noOfSelections = noOfSelections;
  }

  public PackagesSelectionScheduledDto scheduledDates(List<LocalDate> scheduledDates) {
    this.scheduledDates = scheduledDates;
    return this;
  }

  public PackagesSelectionScheduledDto addScheduledDatesItem(LocalDate scheduledDatesItem) {
    if (this.scheduledDates == null) {
      this.scheduledDates = new ArrayList<>();
    }
    this.scheduledDates.add(scheduledDatesItem);
    return this;
  }

  /**
   * Get scheduledDates
   * @return scheduledDates
   */
  @Valid 
  @Schema(name = "scheduledDates", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("scheduledDates")
  public List<LocalDate> getScheduledDates() {
    return scheduledDates;
  }

  public void setScheduledDates(List<LocalDate> scheduledDates) {
    this.scheduledDates = scheduledDates;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PackagesSelectionScheduledDto packagesSelectionScheduledDto = (PackagesSelectionScheduledDto) o;
    return Objects.equals(this.id, packagesSelectionScheduledDto.id) &&
        Objects.equals(this.noOfSelections, packagesSelectionScheduledDto.noOfSelections) &&
        Objects.equals(this.scheduledDates, packagesSelectionScheduledDto.scheduledDates);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, noOfSelections, scheduledDates);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PackagesSelectionScheduledDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    noOfSelections: ").append(toIndentedString(noOfSelections)).append("\n");
    sb.append("    scheduledDates: ").append(toIndentedString(scheduledDates)).append("\n");
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


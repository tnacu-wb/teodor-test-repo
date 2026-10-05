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
 * PackagesSelectionDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PackagesSelectionDto {

  private @Nullable String id;

  private @Nullable Integer noSelections;

  private @Nullable String packageGroup;

  @Valid
  private @Nullable List<String> scheduledList;

  public PackagesSelectionDto id(String id) {
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

  public PackagesSelectionDto noSelections(Integer noSelections) {
    this.noSelections = noSelections;
    return this;
  }

  /**
   * Get noSelections
   * @return noSelections
   */
  
  @Schema(name = "noSelections", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("noSelections")
  public Integer getNoSelections() {
    return noSelections;
  }

  public void setNoSelections(Integer noSelections) {
    this.noSelections = noSelections;
  }

  public PackagesSelectionDto packageGroup(String packageGroup) {
    this.packageGroup = packageGroup;
    return this;
  }

  /**
   * Get packageGroup
   * @return packageGroup
   */
  
  @Schema(name = "packageGroup", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("packageGroup")
  public String getPackageGroup() {
    return packageGroup;
  }

  public void setPackageGroup(String packageGroup) {
    this.packageGroup = packageGroup;
  }

  public PackagesSelectionDto scheduledList(List<String> scheduledList) {
    this.scheduledList = scheduledList;
    return this;
  }

  public PackagesSelectionDto addScheduledListItem(String scheduledListItem) {
    if (this.scheduledList == null) {
      this.scheduledList = new ArrayList<>();
    }
    this.scheduledList.add(scheduledListItem);
    return this;
  }

  /**
   * Get scheduledList
   * @return scheduledList
   */
  
  @Schema(name = "scheduledList", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("scheduledList")
  public List<String> getScheduledList() {
    return scheduledList;
  }

  public void setScheduledList(List<String> scheduledList) {
    this.scheduledList = scheduledList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PackagesSelectionDto packagesSelectionDto = (PackagesSelectionDto) o;
    return Objects.equals(this.id, packagesSelectionDto.id) &&
        Objects.equals(this.noSelections, packagesSelectionDto.noSelections) &&
        Objects.equals(this.packageGroup, packagesSelectionDto.packageGroup) &&
        Objects.equals(this.scheduledList, packagesSelectionDto.scheduledList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, noSelections, packageGroup, scheduledList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PackagesSelectionDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    noSelections: ").append(toIndentedString(noSelections)).append("\n");
    sb.append("    packageGroup: ").append(toIndentedString(packageGroup)).append("\n");
    sb.append("    scheduledList: ").append(toIndentedString(scheduledList)).append("\n");
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


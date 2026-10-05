package uk.co.whitbread.hotel.ohip.adapter.generated.models;

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
 * PackagesSelection
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PackagesSelection {

  private @Nullable String id;

  private @Nullable Integer noSelections;

  private @Nullable String packageGroup;

  public PackagesSelection id(String id) {
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

  public PackagesSelection noSelections(Integer noSelections) {
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

  public PackagesSelection packageGroup(String packageGroup) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PackagesSelection packagesSelection = (PackagesSelection) o;
    return Objects.equals(this.id, packagesSelection.id) &&
        Objects.equals(this.noSelections, packagesSelection.noSelections) &&
        Objects.equals(this.packageGroup, packagesSelection.packageGroup);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, noSelections, packageGroup);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PackagesSelection {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    noSelections: ").append(toIndentedString(noSelections)).append("\n");
    sb.append("    packageGroup: ").append(toIndentedString(packageGroup)).append("\n");
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


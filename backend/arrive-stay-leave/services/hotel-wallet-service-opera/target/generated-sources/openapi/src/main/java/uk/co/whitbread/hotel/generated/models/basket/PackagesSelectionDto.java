package uk.co.whitbread.hotel.generated.models.basket;

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
 * PackagesSelectionDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:22.312200+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PackagesSelectionDto {

  private @Nullable String id;

  private @Nullable Integer noOfSelections;

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

  public PackagesSelectionDto noOfSelections(Integer noOfSelections) {
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
        Objects.equals(this.noOfSelections, packagesSelectionDto.noOfSelections);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, noOfSelections);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PackagesSelectionDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    noOfSelections: ").append(toIndentedString(noOfSelections)).append("\n");
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


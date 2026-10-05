package uk.co.whitbread.ohip.generated.models;

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
 * PackagesSelectionDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PackagesSelectionDto {

  private @Nullable String id;

  private @Nullable Integer noSelections;

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
        Objects.equals(this.noSelections, packagesSelectionDto.noSelections);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, noSelections);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PackagesSelectionDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    noSelections: ").append(toIndentedString(noSelections)).append("\n");
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


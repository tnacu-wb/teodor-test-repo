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
 * Multi line description of the rate plan.
 */

@Schema(name = "Description", description = "Multi line description of the rate plan.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Description {

  private @Nullable String line1;

  private @Nullable String line2;

  private @Nullable String line3;

  private @Nullable String detailedDescription;

  public Description line1(String line1) {
    this.line1 = line1;
    return this;
  }

  /**
   * First line of rate plan description.
   * @return line1
   */
  
  @Schema(name = "line1", description = "First line of rate plan description.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("line1")
  public String getLine1() {
    return line1;
  }

  public void setLine1(String line1) {
    this.line1 = line1;
  }

  public Description line2(String line2) {
    this.line2 = line2;
    return this;
  }

  /**
   * Second line of rate plan description.
   * @return line2
   */
  
  @Schema(name = "line2", description = "Second line of rate plan description.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("line2")
  public String getLine2() {
    return line2;
  }

  public void setLine2(String line2) {
    this.line2 = line2;
  }

  public Description line3(String line3) {
    this.line3 = line3;
    return this;
  }

  /**
   * Third line of rate plan description.
   * @return line3
   */
  
  @Schema(name = "line3", description = "Third line of rate plan description.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("line3")
  public String getLine3() {
    return line3;
  }

  public void setLine3(String line3) {
    this.line3 = line3;
  }

  public Description detailedDescription(String detailedDescription) {
    this.detailedDescription = detailedDescription;
    return this;
  }

  /**
   * Detailed information about the rate plan.
   * @return detailedDescription
   */
  
  @Schema(name = "detailedDescription", description = "Detailed information about the rate plan.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("detailedDescription")
  public String getDetailedDescription() {
    return detailedDescription;
  }

  public void setDetailedDescription(String detailedDescription) {
    this.detailedDescription = detailedDescription;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Description description = (Description) o;
    return Objects.equals(this.line1, description.line1) &&
        Objects.equals(this.line2, description.line2) &&
        Objects.equals(this.line3, description.line3) &&
        Objects.equals(this.detailedDescription, description.detailedDescription);
  }

  @Override
  public int hashCode() {
    return Objects.hash(line1, line2, line3, detailedDescription);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Description {\n");
    sb.append("    line1: ").append(toIndentedString(line1)).append("\n");
    sb.append("    line2: ").append(toIndentedString(line2)).append("\n");
    sb.append("    line3: ").append(toIndentedString(line3)).append("\n");
    sb.append("    detailedDescription: ").append(toIndentedString(detailedDescription)).append("\n");
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


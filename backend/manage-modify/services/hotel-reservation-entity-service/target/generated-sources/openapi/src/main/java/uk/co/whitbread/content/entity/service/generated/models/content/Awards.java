package uk.co.whitbread.content.entity.service.generated.models.content;

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
 * Awards
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Awards {

  private String awardType;

  private String image;

  private String year;

  public Awards() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Awards(String awardType, String image, String year) {
    this.awardType = awardType;
    this.image = image;
    this.year = year;
  }

  public Awards awardType(String awardType) {
    this.awardType = awardType;
    return this;
  }

  /**
   * Get awardType
   * @return awardType
   */
  @NotNull 
  @Schema(name = "awardType", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("awardType")
  public String getAwardType() {
    return awardType;
  }

  public void setAwardType(String awardType) {
    this.awardType = awardType;
  }

  public Awards image(String image) {
    this.image = image;
    return this;
  }

  /**
   * Get image
   * @return image
   */
  @NotNull 
  @Schema(name = "image", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("image")
  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public Awards year(String year) {
    this.year = year;
    return this;
  }

  /**
   * Get year
   * @return year
   */
  @NotNull 
  @Schema(name = "year", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("year")
  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Awards awards = (Awards) o;
    return Objects.equals(this.awardType, awards.awardType) &&
        Objects.equals(this.image, awards.image) &&
        Objects.equals(this.year, awards.year);
  }

  @Override
  public int hashCode() {
    return Objects.hash(awardType, image, year);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Awards {\n");
    sb.append("    awardType: ").append(toIndentedString(awardType)).append("\n");
    sb.append("    image: ").append(toIndentedString(image)).append("\n");
    sb.append("    year: ").append(toIndentedString(year)).append("\n");
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


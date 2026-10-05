package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * SubRatings
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class SubRatings {

  private String localisedName;

  private String ratingImageUrl;

  private BigDecimal value;

  public SubRatings() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SubRatings(String localisedName, String ratingImageUrl, BigDecimal value) {
    this.localisedName = localisedName;
    this.ratingImageUrl = ratingImageUrl;
    this.value = value;
  }

  public SubRatings localisedName(String localisedName) {
    this.localisedName = localisedName;
    return this;
  }

  /**
   * Get localisedName
   * @return localisedName
   */
  @NotNull 
  @Schema(name = "localisedName", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("localisedName")
  public String getLocalisedName() {
    return localisedName;
  }

  public void setLocalisedName(String localisedName) {
    this.localisedName = localisedName;
  }

  public SubRatings ratingImageUrl(String ratingImageUrl) {
    this.ratingImageUrl = ratingImageUrl;
    return this;
  }

  /**
   * Get ratingImageUrl
   * @return ratingImageUrl
   */
  @NotNull 
  @Schema(name = "ratingImageUrl", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ratingImageUrl")
  public String getRatingImageUrl() {
    return ratingImageUrl;
  }

  public void setRatingImageUrl(String ratingImageUrl) {
    this.ratingImageUrl = ratingImageUrl;
  }

  public SubRatings value(BigDecimal value) {
    this.value = value;
    return this;
  }

  /**
   * Get value
   * @return value
   */
  @NotNull @Valid 
  @Schema(name = "value", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("value")
  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SubRatings subRatings = (SubRatings) o;
    return Objects.equals(this.localisedName, subRatings.localisedName) &&
        Objects.equals(this.ratingImageUrl, subRatings.ratingImageUrl) &&
        Objects.equals(this.value, subRatings.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(localisedName, ratingImageUrl, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SubRatings {\n");
    sb.append("    localisedName: ").append(toIndentedString(localisedName)).append("\n");
    sb.append("    ratingImageUrl: ").append(toIndentedString(ratingImageUrl)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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


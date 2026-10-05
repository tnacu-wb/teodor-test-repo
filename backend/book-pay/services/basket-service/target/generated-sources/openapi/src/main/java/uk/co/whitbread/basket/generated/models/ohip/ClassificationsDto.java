package uk.co.whitbread.basket.generated.models.ohip;

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
 * ClassificationsDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ClassificationsDto {

  private @Nullable String displaySet;

  private @Nullable String marketCode;

  private @Nullable String rateCategory;

  public ClassificationsDto displaySet(String displaySet) {
    this.displaySet = displaySet;
    return this;
  }

  /**
   * Get displaySet
   * @return displaySet
   */
  
  @Schema(name = "displaySet", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("displaySet")
  public String getDisplaySet() {
    return displaySet;
  }

  public void setDisplaySet(String displaySet) {
    this.displaySet = displaySet;
  }

  public ClassificationsDto marketCode(String marketCode) {
    this.marketCode = marketCode;
    return this;
  }

  /**
   * Get marketCode
   * @return marketCode
   */
  
  @Schema(name = "marketCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("marketCode")
  public String getMarketCode() {
    return marketCode;
  }

  public void setMarketCode(String marketCode) {
    this.marketCode = marketCode;
  }

  public ClassificationsDto rateCategory(String rateCategory) {
    this.rateCategory = rateCategory;
    return this;
  }

  /**
   * Get rateCategory
   * @return rateCategory
   */
  
  @Schema(name = "rateCategory", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateCategory")
  public String getRateCategory() {
    return rateCategory;
  }

  public void setRateCategory(String rateCategory) {
    this.rateCategory = rateCategory;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClassificationsDto classificationsDto = (ClassificationsDto) o;
    return Objects.equals(this.displaySet, classificationsDto.displaySet) &&
        Objects.equals(this.marketCode, classificationsDto.marketCode) &&
        Objects.equals(this.rateCategory, classificationsDto.rateCategory);
  }

  @Override
  public int hashCode() {
    return Objects.hash(displaySet, marketCode, rateCategory);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClassificationsDto {\n");
    sb.append("    displaySet: ").append(toIndentedString(displaySet)).append("\n");
    sb.append("    marketCode: ").append(toIndentedString(marketCode)).append("\n");
    sb.append("    rateCategory: ").append(toIndentedString(rateCategory)).append("\n");
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


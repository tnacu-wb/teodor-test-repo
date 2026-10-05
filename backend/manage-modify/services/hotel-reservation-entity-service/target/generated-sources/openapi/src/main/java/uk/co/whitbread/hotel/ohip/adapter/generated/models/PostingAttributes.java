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
 * PostingAttributes
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PostingAttributes {

  private @Nullable Boolean addToRate;

  private @Nullable Boolean forecastNextDay;

  private @Nullable Boolean postNextDay;

  private @Nullable Boolean printSeparateLine;

  public PostingAttributes addToRate(Boolean addToRate) {
    this.addToRate = addToRate;
    return this;
  }

  /**
   * Get addToRate
   * @return addToRate
   */
  
  @Schema(name = "addToRate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addToRate")
  public Boolean getAddToRate() {
    return addToRate;
  }

  public void setAddToRate(Boolean addToRate) {
    this.addToRate = addToRate;
  }

  public PostingAttributes forecastNextDay(Boolean forecastNextDay) {
    this.forecastNextDay = forecastNextDay;
    return this;
  }

  /**
   * Get forecastNextDay
   * @return forecastNextDay
   */
  
  @Schema(name = "forecastNextDay", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("forecastNextDay")
  public Boolean getForecastNextDay() {
    return forecastNextDay;
  }

  public void setForecastNextDay(Boolean forecastNextDay) {
    this.forecastNextDay = forecastNextDay;
  }

  public PostingAttributes postNextDay(Boolean postNextDay) {
    this.postNextDay = postNextDay;
    return this;
  }

  /**
   * Get postNextDay
   * @return postNextDay
   */
  
  @Schema(name = "postNextDay", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("postNextDay")
  public Boolean getPostNextDay() {
    return postNextDay;
  }

  public void setPostNextDay(Boolean postNextDay) {
    this.postNextDay = postNextDay;
  }

  public PostingAttributes printSeparateLine(Boolean printSeparateLine) {
    this.printSeparateLine = printSeparateLine;
    return this;
  }

  /**
   * Get printSeparateLine
   * @return printSeparateLine
   */
  
  @Schema(name = "printSeparateLine", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("printSeparateLine")
  public Boolean getPrintSeparateLine() {
    return printSeparateLine;
  }

  public void setPrintSeparateLine(Boolean printSeparateLine) {
    this.printSeparateLine = printSeparateLine;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PostingAttributes postingAttributes = (PostingAttributes) o;
    return Objects.equals(this.addToRate, postingAttributes.addToRate) &&
        Objects.equals(this.forecastNextDay, postingAttributes.forecastNextDay) &&
        Objects.equals(this.postNextDay, postingAttributes.postNextDay) &&
        Objects.equals(this.printSeparateLine, postingAttributes.printSeparateLine);
  }

  @Override
  public int hashCode() {
    return Objects.hash(addToRate, forecastNextDay, postNextDay, printSeparateLine);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PostingAttributes {\n");
    sb.append("    addToRate: ").append(toIndentedString(addToRate)).append("\n");
    sb.append("    forecastNextDay: ").append(toIndentedString(forecastNextDay)).append("\n");
    sb.append("    postNextDay: ").append(toIndentedString(postNextDay)).append("\n");
    sb.append("    printSeparateLine: ").append(toIndentedString(printSeparateLine)).append("\n");
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


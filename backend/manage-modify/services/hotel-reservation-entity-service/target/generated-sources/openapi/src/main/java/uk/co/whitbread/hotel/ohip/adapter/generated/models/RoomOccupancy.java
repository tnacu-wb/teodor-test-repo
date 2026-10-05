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
 * RoomOccupancy
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomOccupancy {

  private @Nullable Integer adultCount;

  private @Nullable Integer childCount;

  public RoomOccupancy adultCount(Integer adultCount) {
    this.adultCount = adultCount;
    return this;
  }

  /**
   * Get adultCount
   * @return adultCount
   */
  
  @Schema(name = "adultCount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adultCount")
  public Integer getAdultCount() {
    return adultCount;
  }

  public void setAdultCount(Integer adultCount) {
    this.adultCount = adultCount;
  }

  public RoomOccupancy childCount(Integer childCount) {
    this.childCount = childCount;
    return this;
  }

  /**
   * Get childCount
   * @return childCount
   */
  
  @Schema(name = "childCount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("childCount")
  public Integer getChildCount() {
    return childCount;
  }

  public void setChildCount(Integer childCount) {
    this.childCount = childCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomOccupancy roomOccupancy = (RoomOccupancy) o;
    return Objects.equals(this.adultCount, roomOccupancy.adultCount) &&
        Objects.equals(this.childCount, roomOccupancy.childCount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adultCount, childCount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomOccupancy {\n");
    sb.append("    adultCount: ").append(toIndentedString(adultCount)).append("\n");
    sb.append("    childCount: ").append(toIndentedString(childCount)).append("\n");
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


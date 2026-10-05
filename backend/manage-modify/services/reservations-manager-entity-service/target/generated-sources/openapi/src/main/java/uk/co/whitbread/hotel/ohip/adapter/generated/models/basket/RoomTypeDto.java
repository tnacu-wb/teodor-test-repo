package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

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
 * RoomTypeDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:44.119190+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomTypeDto {

  private @Nullable Integer adultsNumber;

  private @Nullable String rate;

  private @Nullable String type;

  public RoomTypeDto adultsNumber(Integer adultsNumber) {
    this.adultsNumber = adultsNumber;
    return this;
  }

  /**
   * Get adultsNumber
   * @return adultsNumber
   */
  
  @Schema(name = "adultsNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adultsNumber")
  public Integer getAdultsNumber() {
    return adultsNumber;
  }

  public void setAdultsNumber(Integer adultsNumber) {
    this.adultsNumber = adultsNumber;
  }

  public RoomTypeDto rate(String rate) {
    this.rate = rate;
    return this;
  }

  /**
   * Get rate
   * @return rate
   */
  
  @Schema(name = "rate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rate")
  public String getRate() {
    return rate;
  }

  public void setRate(String rate) {
    this.rate = rate;
  }

  public RoomTypeDto type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   */
  
  @Schema(name = "type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomTypeDto roomTypeDto = (RoomTypeDto) o;
    return Objects.equals(this.adultsNumber, roomTypeDto.adultsNumber) &&
        Objects.equals(this.rate, roomTypeDto.rate) &&
        Objects.equals(this.type, roomTypeDto.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adultsNumber, rate, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomTypeDto {\n");
    sb.append("    adultsNumber: ").append(toIndentedString(adultsNumber)).append("\n");
    sb.append("    rate: ").append(toIndentedString(rate)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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


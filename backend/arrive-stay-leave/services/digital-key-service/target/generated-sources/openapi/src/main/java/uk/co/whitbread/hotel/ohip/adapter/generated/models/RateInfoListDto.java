package uk.co.whitbread.hotel.ohip.adapter.generated.models;

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
 * RateInfoListDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RateInfoListDto {

  private @Nullable String end;

  private @Nullable Integer negotiatedRateOrder;

  private @Nullable String start;

  public RateInfoListDto end(String end) {
    this.end = end;
    return this;
  }

  /**
   * Get end
   * @return end
   */
  
  @Schema(name = "end", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("end")
  public String getEnd() {
    return end;
  }

  public void setEnd(String end) {
    this.end = end;
  }

  public RateInfoListDto negotiatedRateOrder(Integer negotiatedRateOrder) {
    this.negotiatedRateOrder = negotiatedRateOrder;
    return this;
  }

  /**
   * Get negotiatedRateOrder
   * @return negotiatedRateOrder
   */
  
  @Schema(name = "negotiatedRateOrder", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("negotiatedRateOrder")
  public Integer getNegotiatedRateOrder() {
    return negotiatedRateOrder;
  }

  public void setNegotiatedRateOrder(Integer negotiatedRateOrder) {
    this.negotiatedRateOrder = negotiatedRateOrder;
  }

  public RateInfoListDto start(String start) {
    this.start = start;
    return this;
  }

  /**
   * Get start
   * @return start
   */
  
  @Schema(name = "start", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("start")
  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RateInfoListDto rateInfoListDto = (RateInfoListDto) o;
    return Objects.equals(this.end, rateInfoListDto.end) &&
        Objects.equals(this.negotiatedRateOrder, rateInfoListDto.negotiatedRateOrder) &&
        Objects.equals(this.start, rateInfoListDto.start);
  }

  @Override
  public int hashCode() {
    return Objects.hash(end, negotiatedRateOrder, start);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateInfoListDto {\n");
    sb.append("    end: ").append(toIndentedString(end)).append("\n");
    sb.append("    negotiatedRateOrder: ").append(toIndentedString(negotiatedRateOrder)).append("\n");
    sb.append("    start: ").append(toIndentedString(start)).append("\n");
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


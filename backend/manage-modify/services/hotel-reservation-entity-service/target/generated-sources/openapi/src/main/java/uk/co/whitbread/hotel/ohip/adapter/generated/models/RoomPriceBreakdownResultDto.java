package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomPriceBreakdownDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * RoomPriceBreakdownResultDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomPriceBreakdownResultDto {

  @Valid
  private List<@Valid RoomPriceBreakdownDto> priceBreakdown = new ArrayList<>();

  public RoomPriceBreakdownResultDto priceBreakdown(List<@Valid RoomPriceBreakdownDto> priceBreakdown) {
    this.priceBreakdown = priceBreakdown;
    return this;
  }

  public RoomPriceBreakdownResultDto addPriceBreakdownItem(RoomPriceBreakdownDto priceBreakdownItem) {
    if (this.priceBreakdown == null) {
      this.priceBreakdown = new ArrayList<>();
    }
    this.priceBreakdown.add(priceBreakdownItem);
    return this;
  }

  /**
   * Get priceBreakdown
   * @return priceBreakdown
   */
  @Valid 
  @Schema(name = "priceBreakdown", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("priceBreakdown")
  public List<@Valid RoomPriceBreakdownDto> getPriceBreakdown() {
    return priceBreakdown;
  }

  public void setPriceBreakdown(List<@Valid RoomPriceBreakdownDto> priceBreakdown) {
    this.priceBreakdown = priceBreakdown;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomPriceBreakdownResultDto roomPriceBreakdownResultDto = (RoomPriceBreakdownResultDto) o;
    return Objects.equals(this.priceBreakdown, roomPriceBreakdownResultDto.priceBreakdown);
  }

  @Override
  public int hashCode() {
    return Objects.hash(priceBreakdown);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomPriceBreakdownResultDto {\n");
    sb.append("    priceBreakdown: ").append(toIndentedString(priceBreakdown)).append("\n");
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


package uk.co.whitbread.hotel.info.generated.hotelinfo.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * MultiHotelInfoResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:34.398121+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MultiHotelInfoResponse {

  @Valid
  private List<String> hotels = new ArrayList<>();

  private @Nullable Integer total;

  public MultiHotelInfoResponse hotels(List<String> hotels) {
    this.hotels = hotels;
    return this;
  }

  public MultiHotelInfoResponse addHotelsItem(String hotelsItem) {
    if (this.hotels == null) {
      this.hotels = new ArrayList<>();
    }
    this.hotels.add(hotelsItem);
    return this;
  }

  /**
   * Get hotels
   * @return hotels
   */
  
  @Schema(name = "hotels", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotels")
  public List<String> getHotels() {
    return hotels;
  }

  public void setHotels(List<String> hotels) {
    this.hotels = hotels;
  }

  public MultiHotelInfoResponse total(Integer total) {
    this.total = total;
    return this;
  }

  /**
   * Get total
   * @return total
   */
  
  @Schema(name = "total", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("total")
  public Integer getTotal() {
    return total;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MultiHotelInfoResponse multiHotelInfoResponse = (MultiHotelInfoResponse) o;
    return Objects.equals(this.hotels, multiHotelInfoResponse.hotels) &&
        Objects.equals(this.total, multiHotelInfoResponse.total);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotels, total);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MultiHotelInfoResponse {\n");
    sb.append("    hotels: ").append(toIndentedString(hotels)).append("\n");
    sb.append("    total: ").append(toIndentedString(total)).append("\n");
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


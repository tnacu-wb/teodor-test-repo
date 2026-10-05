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
 * HotelStatusDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HotelStatusDto {

  private @Nullable String hotelId;

  private @Nullable Boolean onSale;

  private @Nullable String pmsSource;

  public HotelStatusDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public HotelStatusDto onSale(Boolean onSale) {
    this.onSale = onSale;
    return this;
  }

  /**
   * Get onSale
   * @return onSale
   */
  
  @Schema(name = "onSale", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("onSale")
  public Boolean getOnSale() {
    return onSale;
  }

  public void setOnSale(Boolean onSale) {
    this.onSale = onSale;
  }

  public HotelStatusDto pmsSource(String pmsSource) {
    this.pmsSource = pmsSource;
    return this;
  }

  /**
   * Get pmsSource
   * @return pmsSource
   */
  
  @Schema(name = "pmsSource", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pmsSource")
  public String getPmsSource() {
    return pmsSource;
  }

  public void setPmsSource(String pmsSource) {
    this.pmsSource = pmsSource;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HotelStatusDto hotelStatusDto = (HotelStatusDto) o;
    return Objects.equals(this.hotelId, hotelStatusDto.hotelId) &&
        Objects.equals(this.onSale, hotelStatusDto.onSale) &&
        Objects.equals(this.pmsSource, hotelStatusDto.pmsSource);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelId, onSale, pmsSource);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelStatusDto {\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    onSale: ").append(toIndentedString(onSale)).append("\n");
    sb.append("    pmsSource: ").append(toIndentedString(pmsSource)).append("\n");
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


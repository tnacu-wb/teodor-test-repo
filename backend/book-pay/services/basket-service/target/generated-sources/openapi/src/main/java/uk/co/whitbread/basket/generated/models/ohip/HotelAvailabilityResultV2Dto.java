package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * HotelAvailabilityResultV2Dto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HotelAvailabilityResultV2Dto {

  private @Nullable Boolean available;

  private @Nullable String currency;

  private @Nullable String hotelId;

  private @Nullable BigDecimal minimumRate;

  public HotelAvailabilityResultV2Dto available(Boolean available) {
    this.available = available;
    return this;
  }

  /**
   * Get available
   * @return available
   */
  
  @Schema(name = "available", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("available")
  public Boolean getAvailable() {
    return available;
  }

  public void setAvailable(Boolean available) {
    this.available = available;
  }

  public HotelAvailabilityResultV2Dto currency(String currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Get currency
   * @return currency
   */
  
  @Schema(name = "currency", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currency")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public HotelAvailabilityResultV2Dto hotelId(String hotelId) {
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

  public HotelAvailabilityResultV2Dto minimumRate(BigDecimal minimumRate) {
    this.minimumRate = minimumRate;
    return this;
  }

  /**
   * Get minimumRate
   * @return minimumRate
   */
  @Valid 
  @Schema(name = "minimumRate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("minimumRate")
  public BigDecimal getMinimumRate() {
    return minimumRate;
  }

  public void setMinimumRate(BigDecimal minimumRate) {
    this.minimumRate = minimumRate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HotelAvailabilityResultV2Dto hotelAvailabilityResultV2Dto = (HotelAvailabilityResultV2Dto) o;
    return Objects.equals(this.available, hotelAvailabilityResultV2Dto.available) &&
        Objects.equals(this.currency, hotelAvailabilityResultV2Dto.currency) &&
        Objects.equals(this.hotelId, hotelAvailabilityResultV2Dto.hotelId) &&
        Objects.equals(this.minimumRate, hotelAvailabilityResultV2Dto.minimumRate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(available, currency, hotelId, minimumRate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelAvailabilityResultV2Dto {\n");
    sb.append("    available: ").append(toIndentedString(available)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    minimumRate: ").append(toIndentedString(minimumRate)).append("\n");
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


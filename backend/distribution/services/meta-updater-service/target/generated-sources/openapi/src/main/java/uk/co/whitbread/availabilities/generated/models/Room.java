package uk.co.whitbread.availabilities.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.availabilities.generated.models.Rate;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Room
 */
@lombok.Builder @lombok.AllArgsConstructor

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:31.761066+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Room {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate availabilityDate;

  private @Nullable String roomType;

  private @Nullable Integer quantity;

  @lombok.Builder.Default
  @Valid
  private List<@Valid Rate> rates = new ArrayList<>();

  public Room availabilityDate(LocalDate availabilityDate) {
    this.availabilityDate = availabilityDate;
    return this;
  }

  /**
   * Get availabilityDate
   * @return availabilityDate
   */
  @Valid 
  @Schema(name = "availabilityDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("availabilityDate")
  public LocalDate getAvailabilityDate() {
    return availabilityDate;
  }

  public void setAvailabilityDate(LocalDate availabilityDate) {
    this.availabilityDate = availabilityDate;
  }

  public Room roomType(String roomType) {
    this.roomType = roomType;
    return this;
  }

  /**
   * Get roomType
   * @return roomType
   */
  
  @Schema(name = "roomType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomType")
  public String getRoomType() {
    return roomType;
  }

  public void setRoomType(String roomType) {
    this.roomType = roomType;
  }

  public Room quantity(Integer quantity) {
    this.quantity = quantity;
    return this;
  }

  /**
   * Get quantity
   * @return quantity
   */
  
  @Schema(name = "quantity", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("quantity")
  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public Room rates(List<@Valid Rate> rates) {
    this.rates = rates;
    return this;
  }

  public Room addRatesItem(Rate ratesItem) {
    if (this.rates == null) {
      this.rates = new ArrayList<>();
    }
    this.rates.add(ratesItem);
    return this;
  }

  /**
   * Get rates
   * @return rates
   */
  @Valid 
  @Schema(name = "rates", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rates")
  public List<@Valid Rate> getRates() {
    return rates;
  }

  public void setRates(List<@Valid Rate> rates) {
    this.rates = rates;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Room room = (Room) o;
    return Objects.equals(this.availabilityDate, room.availabilityDate) &&
        Objects.equals(this.roomType, room.roomType) &&
        Objects.equals(this.quantity, room.quantity) &&
        Objects.equals(this.rates, room.rates);
  }

  @Override
  public int hashCode() {
    return Objects.hash(availabilityDate, roomType, quantity, rates);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Room {\n");
    sb.append("    availabilityDate: ").append(toIndentedString(availabilityDate)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
    sb.append("    rates: ").append(toIndentedString(rates)).append("\n");
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


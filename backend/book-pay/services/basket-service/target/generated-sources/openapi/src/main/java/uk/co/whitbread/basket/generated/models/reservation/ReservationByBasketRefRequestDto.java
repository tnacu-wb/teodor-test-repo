package uk.co.whitbread.basket.generated.models.reservation;

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
 * ReservationByBasketRefRequestDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:03.993735+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationByBasketRefRequestDto {

  private String priceBreakDownNeeded = "false";

  private Boolean rateInfoNeeded = true;

  public ReservationByBasketRefRequestDto priceBreakDownNeeded(String priceBreakDownNeeded) {
    this.priceBreakDownNeeded = priceBreakDownNeeded;
    return this;
  }

  /**
   * Get priceBreakDownNeeded
   * @return priceBreakDownNeeded
   */
  
  @Schema(name = "priceBreakDownNeeded", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("priceBreakDownNeeded")
  public String getPriceBreakDownNeeded() {
    return priceBreakDownNeeded;
  }

  public void setPriceBreakDownNeeded(String priceBreakDownNeeded) {
    this.priceBreakDownNeeded = priceBreakDownNeeded;
  }

  public ReservationByBasketRefRequestDto rateInfoNeeded(Boolean rateInfoNeeded) {
    this.rateInfoNeeded = rateInfoNeeded;
    return this;
  }

  /**
   * Get rateInfoNeeded
   * @return rateInfoNeeded
   */
  
  @Schema(name = "rateInfoNeeded", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateInfoNeeded")
  public Boolean getRateInfoNeeded() {
    return rateInfoNeeded;
  }

  public void setRateInfoNeeded(Boolean rateInfoNeeded) {
    this.rateInfoNeeded = rateInfoNeeded;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationByBasketRefRequestDto reservationByBasketRefRequestDto = (ReservationByBasketRefRequestDto) o;
    return Objects.equals(this.priceBreakDownNeeded, reservationByBasketRefRequestDto.priceBreakDownNeeded) &&
        Objects.equals(this.rateInfoNeeded, reservationByBasketRefRequestDto.rateInfoNeeded);
  }

  @Override
  public int hashCode() {
    return Objects.hash(priceBreakDownNeeded, rateInfoNeeded);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationByBasketRefRequestDto {\n");
    sb.append("    priceBreakDownNeeded: ").append(toIndentedString(priceBreakDownNeeded)).append("\n");
    sb.append("    rateInfoNeeded: ").append(toIndentedString(rateInfoNeeded)).append("\n");
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


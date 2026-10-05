package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DailyPriceDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomPriceBreakdownDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomPriceBreakdownDto {

  private @Nullable String currencyCode;

  @Valid
  private @Nullable List<@Valid DailyPriceDto> dailyPrices;

  private @Nullable BigDecimal totalGrossAmount;

  private @Nullable BigDecimal totalNetAmount;

  private @Nullable BigDecimal totalTaxAmount;

  public RoomPriceBreakdownDto currencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
    return this;
  }

  /**
   * Get currencyCode
   * @return currencyCode
   */
  
  @Schema(name = "currencyCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currencyCode")
  public String getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
  }

  public RoomPriceBreakdownDto dailyPrices(List<@Valid DailyPriceDto> dailyPrices) {
    this.dailyPrices = dailyPrices;
    return this;
  }

  public RoomPriceBreakdownDto addDailyPricesItem(DailyPriceDto dailyPricesItem) {
    if (this.dailyPrices == null) {
      this.dailyPrices = new ArrayList<>();
    }
    this.dailyPrices.add(dailyPricesItem);
    return this;
  }

  /**
   * Get dailyPrices
   * @return dailyPrices
   */
  @Valid 
  @Schema(name = "dailyPrices", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dailyPrices")
  public List<@Valid DailyPriceDto> getDailyPrices() {
    return dailyPrices;
  }

  public void setDailyPrices(List<@Valid DailyPriceDto> dailyPrices) {
    this.dailyPrices = dailyPrices;
  }

  public RoomPriceBreakdownDto totalGrossAmount(BigDecimal totalGrossAmount) {
    this.totalGrossAmount = totalGrossAmount;
    return this;
  }

  /**
   * Get totalGrossAmount
   * @return totalGrossAmount
   */
  @Valid 
  @Schema(name = "totalGrossAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalGrossAmount")
  public BigDecimal getTotalGrossAmount() {
    return totalGrossAmount;
  }

  public void setTotalGrossAmount(BigDecimal totalGrossAmount) {
    this.totalGrossAmount = totalGrossAmount;
  }

  public RoomPriceBreakdownDto totalNetAmount(BigDecimal totalNetAmount) {
    this.totalNetAmount = totalNetAmount;
    return this;
  }

  /**
   * Get totalNetAmount
   * @return totalNetAmount
   */
  @Valid 
  @Schema(name = "totalNetAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalNetAmount")
  public BigDecimal getTotalNetAmount() {
    return totalNetAmount;
  }

  public void setTotalNetAmount(BigDecimal totalNetAmount) {
    this.totalNetAmount = totalNetAmount;
  }

  public RoomPriceBreakdownDto totalTaxAmount(BigDecimal totalTaxAmount) {
    this.totalTaxAmount = totalTaxAmount;
    return this;
  }

  /**
   * Get totalTaxAmount
   * @return totalTaxAmount
   */
  @Valid 
  @Schema(name = "totalTaxAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalTaxAmount")
  public BigDecimal getTotalTaxAmount() {
    return totalTaxAmount;
  }

  public void setTotalTaxAmount(BigDecimal totalTaxAmount) {
    this.totalTaxAmount = totalTaxAmount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomPriceBreakdownDto roomPriceBreakdownDto = (RoomPriceBreakdownDto) o;
    return Objects.equals(this.currencyCode, roomPriceBreakdownDto.currencyCode) &&
        Objects.equals(this.dailyPrices, roomPriceBreakdownDto.dailyPrices) &&
        Objects.equals(this.totalGrossAmount, roomPriceBreakdownDto.totalGrossAmount) &&
        Objects.equals(this.totalNetAmount, roomPriceBreakdownDto.totalNetAmount) &&
        Objects.equals(this.totalTaxAmount, roomPriceBreakdownDto.totalTaxAmount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currencyCode, dailyPrices, totalGrossAmount, totalNetAmount, totalTaxAmount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomPriceBreakdownDto {\n");
    sb.append("    currencyCode: ").append(toIndentedString(currencyCode)).append("\n");
    sb.append("    dailyPrices: ").append(toIndentedString(dailyPrices)).append("\n");
    sb.append("    totalGrossAmount: ").append(toIndentedString(totalGrossAmount)).append("\n");
    sb.append("    totalNetAmount: ").append(toIndentedString(totalNetAmount)).append("\n");
    sb.append("    totalTaxAmount: ").append(toIndentedString(totalTaxAmount)).append("\n");
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


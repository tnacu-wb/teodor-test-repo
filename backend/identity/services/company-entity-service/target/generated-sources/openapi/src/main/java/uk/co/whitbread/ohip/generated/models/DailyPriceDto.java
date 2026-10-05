package uk.co.whitbread.ohip.generated.models;

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
 * DailyPriceDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class DailyPriceDto {

  private @Nullable String date;

  private @Nullable BigDecimal grossPrice;

  private @Nullable BigDecimal netPrice;

  public DailyPriceDto date(String date) {
    this.date = date;
    return this;
  }

  /**
   * Get date
   * @return date
   */
  
  @Schema(name = "date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("date")
  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public DailyPriceDto grossPrice(BigDecimal grossPrice) {
    this.grossPrice = grossPrice;
    return this;
  }

  /**
   * Get grossPrice
   * @return grossPrice
   */
  @Valid 
  @Schema(name = "grossPrice", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("grossPrice")
  public BigDecimal getGrossPrice() {
    return grossPrice;
  }

  public void setGrossPrice(BigDecimal grossPrice) {
    this.grossPrice = grossPrice;
  }

  public DailyPriceDto netPrice(BigDecimal netPrice) {
    this.netPrice = netPrice;
    return this;
  }

  /**
   * Get netPrice
   * @return netPrice
   */
  @Valid 
  @Schema(name = "netPrice", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("netPrice")
  public BigDecimal getNetPrice() {
    return netPrice;
  }

  public void setNetPrice(BigDecimal netPrice) {
    this.netPrice = netPrice;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DailyPriceDto dailyPriceDto = (DailyPriceDto) o;
    return Objects.equals(this.date, dailyPriceDto.date) &&
        Objects.equals(this.grossPrice, dailyPriceDto.grossPrice) &&
        Objects.equals(this.netPrice, dailyPriceDto.netPrice);
  }

  @Override
  public int hashCode() {
    return Objects.hash(date, grossPrice, netPrice);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DailyPriceDto {\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    grossPrice: ").append(toIndentedString(grossPrice)).append("\n");
    sb.append("    netPrice: ").append(toIndentedString(netPrice)).append("\n");
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


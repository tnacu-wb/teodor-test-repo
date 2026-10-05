package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RatePriceDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RatePriceDto {

  private @Nullable BigDecimal amount;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate priceEndDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate priceStartDate;

  public RatePriceDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RatePriceDto(LocalDate priceEndDate, LocalDate priceStartDate) {
    this.priceEndDate = priceEndDate;
    this.priceStartDate = priceStartDate;
  }

  public RatePriceDto amount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Get amount
   * @return amount
   */
  @Valid 
  @Schema(name = "amount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amount")
  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public RatePriceDto priceEndDate(LocalDate priceEndDate) {
    this.priceEndDate = priceEndDate;
    return this;
  }

  /**
   * Get priceEndDate
   * @return priceEndDate
   */
  @NotNull @Valid 
  @Schema(name = "priceEndDate", example = "2015-10-21", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("priceEndDate")
  public LocalDate getPriceEndDate() {
    return priceEndDate;
  }

  public void setPriceEndDate(LocalDate priceEndDate) {
    this.priceEndDate = priceEndDate;
  }

  public RatePriceDto priceStartDate(LocalDate priceStartDate) {
    this.priceStartDate = priceStartDate;
    return this;
  }

  /**
   * Get priceStartDate
   * @return priceStartDate
   */
  @NotNull @Valid 
  @Schema(name = "priceStartDate", example = "2015-10-20", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("priceStartDate")
  public LocalDate getPriceStartDate() {
    return priceStartDate;
  }

  public void setPriceStartDate(LocalDate priceStartDate) {
    this.priceStartDate = priceStartDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RatePriceDto ratePriceDto = (RatePriceDto) o;
    return Objects.equals(this.amount, ratePriceDto.amount) &&
        Objects.equals(this.priceEndDate, ratePriceDto.priceEndDate) &&
        Objects.equals(this.priceStartDate, ratePriceDto.priceStartDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, priceEndDate, priceStartDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RatePriceDto {\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    priceEndDate: ").append(toIndentedString(priceEndDate)).append("\n");
    sb.append("    priceStartDate: ").append(toIndentedString(priceStartDate)).append("\n");
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


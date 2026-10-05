package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models;

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
 * InvoiceVatDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:45.778333+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class InvoiceVatDto {

  private @Nullable String rate;

  private @Nullable String rateDesc;

  private @Nullable String totalExclVat;

  private @Nullable String totalIncVat;

  private @Nullable String totalVat;

  public InvoiceVatDto rate(String rate) {
    this.rate = rate;
    return this;
  }

  /**
   * Get rate
   * @return rate
   */
  
  @Schema(name = "rate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rate")
  public String getRate() {
    return rate;
  }

  public void setRate(String rate) {
    this.rate = rate;
  }

  public InvoiceVatDto rateDesc(String rateDesc) {
    this.rateDesc = rateDesc;
    return this;
  }

  /**
   * Get rateDesc
   * @return rateDesc
   */
  
  @Schema(name = "rateDesc", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateDesc")
  public String getRateDesc() {
    return rateDesc;
  }

  public void setRateDesc(String rateDesc) {
    this.rateDesc = rateDesc;
  }

  public InvoiceVatDto totalExclVat(String totalExclVat) {
    this.totalExclVat = totalExclVat;
    return this;
  }

  /**
   * Get totalExclVat
   * @return totalExclVat
   */
  
  @Schema(name = "totalExclVat", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalExclVat")
  public String getTotalExclVat() {
    return totalExclVat;
  }

  public void setTotalExclVat(String totalExclVat) {
    this.totalExclVat = totalExclVat;
  }

  public InvoiceVatDto totalIncVat(String totalIncVat) {
    this.totalIncVat = totalIncVat;
    return this;
  }

  /**
   * Get totalIncVat
   * @return totalIncVat
   */
  
  @Schema(name = "totalIncVat", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalIncVat")
  public String getTotalIncVat() {
    return totalIncVat;
  }

  public void setTotalIncVat(String totalIncVat) {
    this.totalIncVat = totalIncVat;
  }

  public InvoiceVatDto totalVat(String totalVat) {
    this.totalVat = totalVat;
    return this;
  }

  /**
   * Get totalVat
   * @return totalVat
   */
  
  @Schema(name = "totalVat", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalVat")
  public String getTotalVat() {
    return totalVat;
  }

  public void setTotalVat(String totalVat) {
    this.totalVat = totalVat;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvoiceVatDto invoiceVatDto = (InvoiceVatDto) o;
    return Objects.equals(this.rate, invoiceVatDto.rate) &&
        Objects.equals(this.rateDesc, invoiceVatDto.rateDesc) &&
        Objects.equals(this.totalExclVat, invoiceVatDto.totalExclVat) &&
        Objects.equals(this.totalIncVat, invoiceVatDto.totalIncVat) &&
        Objects.equals(this.totalVat, invoiceVatDto.totalVat);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rate, rateDesc, totalExclVat, totalIncVat, totalVat);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvoiceVatDto {\n");
    sb.append("    rate: ").append(toIndentedString(rate)).append("\n");
    sb.append("    rateDesc: ").append(toIndentedString(rateDesc)).append("\n");
    sb.append("    totalExclVat: ").append(toIndentedString(totalExclVat)).append("\n");
    sb.append("    totalIncVat: ").append(toIndentedString(totalIncVat)).append("\n");
    sb.append("    totalVat: ").append(toIndentedString(totalVat)).append("\n");
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


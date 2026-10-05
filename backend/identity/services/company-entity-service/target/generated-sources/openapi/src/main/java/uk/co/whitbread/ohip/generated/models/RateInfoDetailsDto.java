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
 * RateInfoDetailsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RateInfoDetailsDto {

  private @Nullable String currencyCode;

  private @Nullable BigDecimal gross;

  private @Nullable BigDecimal net;

  private @Nullable BigDecimal packageDetails;

  private @Nullable String ratePlanCode;

  private @Nullable BigDecimal revenue;

  private @Nullable String summaryDate;

  private @Nullable BigDecimal tax;

  public RateInfoDetailsDto currencyCode(String currencyCode) {
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

  public RateInfoDetailsDto gross(BigDecimal gross) {
    this.gross = gross;
    return this;
  }

  /**
   * Get gross
   * @return gross
   */
  @Valid 
  @Schema(name = "gross", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("gross")
  public BigDecimal getGross() {
    return gross;
  }

  public void setGross(BigDecimal gross) {
    this.gross = gross;
  }

  public RateInfoDetailsDto net(BigDecimal net) {
    this.net = net;
    return this;
  }

  /**
   * Get net
   * @return net
   */
  @Valid 
  @Schema(name = "net", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("net")
  public BigDecimal getNet() {
    return net;
  }

  public void setNet(BigDecimal net) {
    this.net = net;
  }

  public RateInfoDetailsDto packageDetails(BigDecimal packageDetails) {
    this.packageDetails = packageDetails;
    return this;
  }

  /**
   * Get packageDetails
   * @return packageDetails
   */
  @Valid 
  @Schema(name = "packageDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("packageDetails")
  public BigDecimal getPackageDetails() {
    return packageDetails;
  }

  public void setPackageDetails(BigDecimal packageDetails) {
    this.packageDetails = packageDetails;
  }

  public RateInfoDetailsDto ratePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
    return this;
  }

  /**
   * Get ratePlanCode
   * @return ratePlanCode
   */
  
  @Schema(name = "ratePlanCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanCode")
  public String getRatePlanCode() {
    return ratePlanCode;
  }

  public void setRatePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
  }

  public RateInfoDetailsDto revenue(BigDecimal revenue) {
    this.revenue = revenue;
    return this;
  }

  /**
   * Get revenue
   * @return revenue
   */
  @Valid 
  @Schema(name = "revenue", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("revenue")
  public BigDecimal getRevenue() {
    return revenue;
  }

  public void setRevenue(BigDecimal revenue) {
    this.revenue = revenue;
  }

  public RateInfoDetailsDto summaryDate(String summaryDate) {
    this.summaryDate = summaryDate;
    return this;
  }

  /**
   * Get summaryDate
   * @return summaryDate
   */
  
  @Schema(name = "summaryDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("summaryDate")
  public String getSummaryDate() {
    return summaryDate;
  }

  public void setSummaryDate(String summaryDate) {
    this.summaryDate = summaryDate;
  }

  public RateInfoDetailsDto tax(BigDecimal tax) {
    this.tax = tax;
    return this;
  }

  /**
   * Get tax
   * @return tax
   */
  @Valid 
  @Schema(name = "tax", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tax")
  public BigDecimal getTax() {
    return tax;
  }

  public void setTax(BigDecimal tax) {
    this.tax = tax;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RateInfoDetailsDto rateInfoDetailsDto = (RateInfoDetailsDto) o;
    return Objects.equals(this.currencyCode, rateInfoDetailsDto.currencyCode) &&
        Objects.equals(this.gross, rateInfoDetailsDto.gross) &&
        Objects.equals(this.net, rateInfoDetailsDto.net) &&
        Objects.equals(this.packageDetails, rateInfoDetailsDto.packageDetails) &&
        Objects.equals(this.ratePlanCode, rateInfoDetailsDto.ratePlanCode) &&
        Objects.equals(this.revenue, rateInfoDetailsDto.revenue) &&
        Objects.equals(this.summaryDate, rateInfoDetailsDto.summaryDate) &&
        Objects.equals(this.tax, rateInfoDetailsDto.tax);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currencyCode, gross, net, packageDetails, ratePlanCode, revenue, summaryDate, tax);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateInfoDetailsDto {\n");
    sb.append("    currencyCode: ").append(toIndentedString(currencyCode)).append("\n");
    sb.append("    gross: ").append(toIndentedString(gross)).append("\n");
    sb.append("    net: ").append(toIndentedString(net)).append("\n");
    sb.append("    packageDetails: ").append(toIndentedString(packageDetails)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    revenue: ").append(toIndentedString(revenue)).append("\n");
    sb.append("    summaryDate: ").append(toIndentedString(summaryDate)).append("\n");
    sb.append("    tax: ").append(toIndentedString(tax)).append("\n");
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


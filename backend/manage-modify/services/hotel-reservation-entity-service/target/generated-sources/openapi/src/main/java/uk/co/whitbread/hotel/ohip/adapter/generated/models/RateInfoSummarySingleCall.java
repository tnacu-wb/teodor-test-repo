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
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RateInfoDetailsSingleCall;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * RateInfoSummarySingleCall
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RateInfoSummarySingleCall {

  private @Nullable String currencyCode;

  private @Nullable BigDecimal deposit;

  @Valid
  private List<@Valid RateInfoDetailsSingleCall> details = new ArrayList<>();

  private @Nullable String end;

  private @Nullable BigDecimal gross;

  private @Nullable BigDecimal guestPay;

  private @Nullable String hasSuppressedRate;

  private @Nullable BigDecimal net;

  private @Nullable BigDecimal outStandingCostOfStay;

  private @Nullable BigDecimal routing;

  private @Nullable String start;

  private @Nullable BigDecimal totalCostOfStay;

  public RateInfoSummarySingleCall currencyCode(String currencyCode) {
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

  public RateInfoSummarySingleCall deposit(BigDecimal deposit) {
    this.deposit = deposit;
    return this;
  }

  /**
   * Get deposit
   * @return deposit
   */
  @Valid 
  @Schema(name = "deposit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("deposit")
  public BigDecimal getDeposit() {
    return deposit;
  }

  public void setDeposit(BigDecimal deposit) {
    this.deposit = deposit;
  }

  public RateInfoSummarySingleCall details(List<@Valid RateInfoDetailsSingleCall> details) {
    this.details = details;
    return this;
  }

  public RateInfoSummarySingleCall addDetailsItem(RateInfoDetailsSingleCall detailsItem) {
    if (this.details == null) {
      this.details = new ArrayList<>();
    }
    this.details.add(detailsItem);
    return this;
  }

  /**
   * Get details
   * @return details
   */
  @Valid 
  @Schema(name = "details", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("details")
  public List<@Valid RateInfoDetailsSingleCall> getDetails() {
    return details;
  }

  public void setDetails(List<@Valid RateInfoDetailsSingleCall> details) {
    this.details = details;
  }

  public RateInfoSummarySingleCall end(String end) {
    this.end = end;
    return this;
  }

  /**
   * Get end
   * @return end
   */
  
  @Schema(name = "end", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("end")
  public String getEnd() {
    return end;
  }

  public void setEnd(String end) {
    this.end = end;
  }

  public RateInfoSummarySingleCall gross(BigDecimal gross) {
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

  public RateInfoSummarySingleCall guestPay(BigDecimal guestPay) {
    this.guestPay = guestPay;
    return this;
  }

  /**
   * Get guestPay
   * @return guestPay
   */
  @Valid 
  @Schema(name = "guestPay", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("guestPay")
  public BigDecimal getGuestPay() {
    return guestPay;
  }

  public void setGuestPay(BigDecimal guestPay) {
    this.guestPay = guestPay;
  }

  public RateInfoSummarySingleCall hasSuppressedRate(String hasSuppressedRate) {
    this.hasSuppressedRate = hasSuppressedRate;
    return this;
  }

  /**
   * Get hasSuppressedRate
   * @return hasSuppressedRate
   */
  
  @Schema(name = "hasSuppressedRate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hasSuppressedRate")
  public String getHasSuppressedRate() {
    return hasSuppressedRate;
  }

  public void setHasSuppressedRate(String hasSuppressedRate) {
    this.hasSuppressedRate = hasSuppressedRate;
  }

  public RateInfoSummarySingleCall net(BigDecimal net) {
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

  public RateInfoSummarySingleCall outStandingCostOfStay(BigDecimal outStandingCostOfStay) {
    this.outStandingCostOfStay = outStandingCostOfStay;
    return this;
  }

  /**
   * Get outStandingCostOfStay
   * @return outStandingCostOfStay
   */
  @Valid 
  @Schema(name = "outStandingCostOfStay", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("outStandingCostOfStay")
  public BigDecimal getOutStandingCostOfStay() {
    return outStandingCostOfStay;
  }

  public void setOutStandingCostOfStay(BigDecimal outStandingCostOfStay) {
    this.outStandingCostOfStay = outStandingCostOfStay;
  }

  public RateInfoSummarySingleCall routing(BigDecimal routing) {
    this.routing = routing;
    return this;
  }

  /**
   * Get routing
   * @return routing
   */
  @Valid 
  @Schema(name = "routing", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("routing")
  public BigDecimal getRouting() {
    return routing;
  }

  public void setRouting(BigDecimal routing) {
    this.routing = routing;
  }

  public RateInfoSummarySingleCall start(String start) {
    this.start = start;
    return this;
  }

  /**
   * Get start
   * @return start
   */
  
  @Schema(name = "start", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("start")
  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public RateInfoSummarySingleCall totalCostOfStay(BigDecimal totalCostOfStay) {
    this.totalCostOfStay = totalCostOfStay;
    return this;
  }

  /**
   * Get totalCostOfStay
   * @return totalCostOfStay
   */
  @Valid 
  @Schema(name = "totalCostOfStay", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalCostOfStay")
  public BigDecimal getTotalCostOfStay() {
    return totalCostOfStay;
  }

  public void setTotalCostOfStay(BigDecimal totalCostOfStay) {
    this.totalCostOfStay = totalCostOfStay;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RateInfoSummarySingleCall rateInfoSummarySingleCall = (RateInfoSummarySingleCall) o;
    return Objects.equals(this.currencyCode, rateInfoSummarySingleCall.currencyCode) &&
        Objects.equals(this.deposit, rateInfoSummarySingleCall.deposit) &&
        Objects.equals(this.details, rateInfoSummarySingleCall.details) &&
        Objects.equals(this.end, rateInfoSummarySingleCall.end) &&
        Objects.equals(this.gross, rateInfoSummarySingleCall.gross) &&
        Objects.equals(this.guestPay, rateInfoSummarySingleCall.guestPay) &&
        Objects.equals(this.hasSuppressedRate, rateInfoSummarySingleCall.hasSuppressedRate) &&
        Objects.equals(this.net, rateInfoSummarySingleCall.net) &&
        Objects.equals(this.outStandingCostOfStay, rateInfoSummarySingleCall.outStandingCostOfStay) &&
        Objects.equals(this.routing, rateInfoSummarySingleCall.routing) &&
        Objects.equals(this.start, rateInfoSummarySingleCall.start) &&
        Objects.equals(this.totalCostOfStay, rateInfoSummarySingleCall.totalCostOfStay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currencyCode, deposit, details, end, gross, guestPay, hasSuppressedRate, net, outStandingCostOfStay, routing, start, totalCostOfStay);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateInfoSummarySingleCall {\n");
    sb.append("    currencyCode: ").append(toIndentedString(currencyCode)).append("\n");
    sb.append("    deposit: ").append(toIndentedString(deposit)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
    sb.append("    end: ").append(toIndentedString(end)).append("\n");
    sb.append("    gross: ").append(toIndentedString(gross)).append("\n");
    sb.append("    guestPay: ").append(toIndentedString(guestPay)).append("\n");
    sb.append("    hasSuppressedRate: ").append(toIndentedString(hasSuppressedRate)).append("\n");
    sb.append("    net: ").append(toIndentedString(net)).append("\n");
    sb.append("    outStandingCostOfStay: ").append(toIndentedString(outStandingCostOfStay)).append("\n");
    sb.append("    routing: ").append(toIndentedString(routing)).append("\n");
    sb.append("    start: ").append(toIndentedString(start)).append("\n");
    sb.append("    totalCostOfStay: ").append(toIndentedString(totalCostOfStay)).append("\n");
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


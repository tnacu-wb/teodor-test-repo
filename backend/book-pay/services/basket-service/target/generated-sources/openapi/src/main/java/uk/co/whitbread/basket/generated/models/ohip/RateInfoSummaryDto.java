package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.RateInfoDetailsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RateInfoSummaryDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RateInfoSummaryDto {

  private @Nullable String currencyCode;

  private @Nullable BigDecimal deposit;

  @Valid
  private List<@Valid RateInfoDetailsDto> details = new ArrayList<>();

  private @Nullable String end;

  private @Nullable BigDecimal gross;

  private @Nullable BigDecimal guestPay;

  private @Nullable String hasSuppressedRate;

  private @Nullable BigDecimal net;

  private @Nullable BigDecimal outStandingCostOfStay;

  private @Nullable BigDecimal routing;

  private @Nullable String start;

  private @Nullable BigDecimal totalCostOfStay;

  public RateInfoSummaryDto currencyCode(String currencyCode) {
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

  public RateInfoSummaryDto deposit(BigDecimal deposit) {
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

  public RateInfoSummaryDto details(List<@Valid RateInfoDetailsDto> details) {
    this.details = details;
    return this;
  }

  public RateInfoSummaryDto addDetailsItem(RateInfoDetailsDto detailsItem) {
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
  public List<@Valid RateInfoDetailsDto> getDetails() {
    return details;
  }

  public void setDetails(List<@Valid RateInfoDetailsDto> details) {
    this.details = details;
  }

  public RateInfoSummaryDto end(String end) {
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

  public RateInfoSummaryDto gross(BigDecimal gross) {
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

  public RateInfoSummaryDto guestPay(BigDecimal guestPay) {
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

  public RateInfoSummaryDto hasSuppressedRate(String hasSuppressedRate) {
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

  public RateInfoSummaryDto net(BigDecimal net) {
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

  public RateInfoSummaryDto outStandingCostOfStay(BigDecimal outStandingCostOfStay) {
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

  public RateInfoSummaryDto routing(BigDecimal routing) {
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

  public RateInfoSummaryDto start(String start) {
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

  public RateInfoSummaryDto totalCostOfStay(BigDecimal totalCostOfStay) {
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
    RateInfoSummaryDto rateInfoSummaryDto = (RateInfoSummaryDto) o;
    return Objects.equals(this.currencyCode, rateInfoSummaryDto.currencyCode) &&
        Objects.equals(this.deposit, rateInfoSummaryDto.deposit) &&
        Objects.equals(this.details, rateInfoSummaryDto.details) &&
        Objects.equals(this.end, rateInfoSummaryDto.end) &&
        Objects.equals(this.gross, rateInfoSummaryDto.gross) &&
        Objects.equals(this.guestPay, rateInfoSummaryDto.guestPay) &&
        Objects.equals(this.hasSuppressedRate, rateInfoSummaryDto.hasSuppressedRate) &&
        Objects.equals(this.net, rateInfoSummaryDto.net) &&
        Objects.equals(this.outStandingCostOfStay, rateInfoSummaryDto.outStandingCostOfStay) &&
        Objects.equals(this.routing, rateInfoSummaryDto.routing) &&
        Objects.equals(this.start, rateInfoSummaryDto.start) &&
        Objects.equals(this.totalCostOfStay, rateInfoSummaryDto.totalCostOfStay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currencyCode, deposit, details, end, gross, guestPay, hasSuppressedRate, net, outStandingCostOfStay, routing, start, totalCostOfStay);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateInfoSummaryDto {\n");
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


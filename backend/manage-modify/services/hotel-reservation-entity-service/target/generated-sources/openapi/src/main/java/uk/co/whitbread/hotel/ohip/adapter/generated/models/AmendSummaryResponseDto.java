package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * AmendSummaryResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AmendSummaryResponseDto {

  @Valid
  private Map<String, BigDecimal> deposit = new HashMap<>();

  @Valid
  private Map<String, BigDecimal> guestPay = new HashMap<>();

  private @Nullable BigDecimal net;

  private @Nullable BigDecimal outStandingCostOfStay;

  private @Nullable BigDecimal totalCostOfStay;

  public AmendSummaryResponseDto deposit(Map<String, BigDecimal> deposit) {
    this.deposit = deposit;
    return this;
  }

  public AmendSummaryResponseDto putDepositItem(String key, BigDecimal depositItem) {
    if (this.deposit == null) {
      this.deposit = new HashMap<>();
    }
    this.deposit.put(key, depositItem);
    return this;
  }

  /**
   * Get deposit
   * @return deposit
   */
  @Valid 
  @Schema(name = "deposit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("deposit")
  public Map<String, BigDecimal> getDeposit() {
    return deposit;
  }

  public void setDeposit(Map<String, BigDecimal> deposit) {
    this.deposit = deposit;
  }

  public AmendSummaryResponseDto guestPay(Map<String, BigDecimal> guestPay) {
    this.guestPay = guestPay;
    return this;
  }

  public AmendSummaryResponseDto putGuestPayItem(String key, BigDecimal guestPayItem) {
    if (this.guestPay == null) {
      this.guestPay = new HashMap<>();
    }
    this.guestPay.put(key, guestPayItem);
    return this;
  }

  /**
   * Get guestPay
   * @return guestPay
   */
  @Valid 
  @Schema(name = "guestPay", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("guestPay")
  public Map<String, BigDecimal> getGuestPay() {
    return guestPay;
  }

  public void setGuestPay(Map<String, BigDecimal> guestPay) {
    this.guestPay = guestPay;
  }

  public AmendSummaryResponseDto net(BigDecimal net) {
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

  public AmendSummaryResponseDto outStandingCostOfStay(BigDecimal outStandingCostOfStay) {
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

  public AmendSummaryResponseDto totalCostOfStay(BigDecimal totalCostOfStay) {
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
    AmendSummaryResponseDto amendSummaryResponseDto = (AmendSummaryResponseDto) o;
    return Objects.equals(this.deposit, amendSummaryResponseDto.deposit) &&
        Objects.equals(this.guestPay, amendSummaryResponseDto.guestPay) &&
        Objects.equals(this.net, amendSummaryResponseDto.net) &&
        Objects.equals(this.outStandingCostOfStay, amendSummaryResponseDto.outStandingCostOfStay) &&
        Objects.equals(this.totalCostOfStay, amendSummaryResponseDto.totalCostOfStay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(deposit, guestPay, net, outStandingCostOfStay, totalCostOfStay);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AmendSummaryResponseDto {\n");
    sb.append("    deposit: ").append(toIndentedString(deposit)).append("\n");
    sb.append("    guestPay: ").append(toIndentedString(guestPay)).append("\n");
    sb.append("    net: ").append(toIndentedString(net)).append("\n");
    sb.append("    outStandingCostOfStay: ").append(toIndentedString(outStandingCostOfStay)).append("\n");
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


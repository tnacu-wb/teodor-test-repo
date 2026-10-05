package uk.co.whitbread.hotel.reservation.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.reservation.generated.models.PaymentCardDetailsDto;
import uk.co.whitbread.hotel.reservation.generated.models.PaymentOptionsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AmendSummaryDetailsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:09:52.163805+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AmendSummaryDetailsDto {

  private @Nullable String balanceAuthorised;

  private @Nullable String balancePaid;

  private @Nullable String charitable;

  private @Nullable String nonRefundable;

  private @Nullable String payOnArrival;

  private @Nullable PaymentCardDetailsDto paymentCardDetails;

  private @Nullable PaymentOptionsDto paymentOptions;

  private @Nullable String previousTotal;

  private @Nullable String refund;

  private @Nullable String totalCost;

  public AmendSummaryDetailsDto balanceAuthorised(String balanceAuthorised) {
    this.balanceAuthorised = balanceAuthorised;
    return this;
  }

  /**
   * Get balanceAuthorised
   * @return balanceAuthorised
   */
  
  @Schema(name = "balanceAuthorised", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("balanceAuthorised")
  public String getBalanceAuthorised() {
    return balanceAuthorised;
  }

  public void setBalanceAuthorised(String balanceAuthorised) {
    this.balanceAuthorised = balanceAuthorised;
  }

  public AmendSummaryDetailsDto balancePaid(String balancePaid) {
    this.balancePaid = balancePaid;
    return this;
  }

  /**
   * Get balancePaid
   * @return balancePaid
   */
  
  @Schema(name = "balancePaid", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("balancePaid")
  public String getBalancePaid() {
    return balancePaid;
  }

  public void setBalancePaid(String balancePaid) {
    this.balancePaid = balancePaid;
  }

  public AmendSummaryDetailsDto charitable(String charitable) {
    this.charitable = charitable;
    return this;
  }

  /**
   * Get charitable
   * @return charitable
   */
  
  @Schema(name = "charitable", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("charitable")
  public String getCharitable() {
    return charitable;
  }

  public void setCharitable(String charitable) {
    this.charitable = charitable;
  }

  public AmendSummaryDetailsDto nonRefundable(String nonRefundable) {
    this.nonRefundable = nonRefundable;
    return this;
  }

  /**
   * Get nonRefundable
   * @return nonRefundable
   */
  
  @Schema(name = "nonRefundable", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nonRefundable")
  public String getNonRefundable() {
    return nonRefundable;
  }

  public void setNonRefundable(String nonRefundable) {
    this.nonRefundable = nonRefundable;
  }

  public AmendSummaryDetailsDto payOnArrival(String payOnArrival) {
    this.payOnArrival = payOnArrival;
    return this;
  }

  /**
   * Get payOnArrival
   * @return payOnArrival
   */
  
  @Schema(name = "payOnArrival", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("payOnArrival")
  public String getPayOnArrival() {
    return payOnArrival;
  }

  public void setPayOnArrival(String payOnArrival) {
    this.payOnArrival = payOnArrival;
  }

  public AmendSummaryDetailsDto paymentCardDetails(PaymentCardDetailsDto paymentCardDetails) {
    this.paymentCardDetails = paymentCardDetails;
    return this;
  }

  /**
   * Get paymentCardDetails
   * @return paymentCardDetails
   */
  @Valid 
  @Schema(name = "paymentCardDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentCardDetails")
  public PaymentCardDetailsDto getPaymentCardDetails() {
    return paymentCardDetails;
  }

  public void setPaymentCardDetails(PaymentCardDetailsDto paymentCardDetails) {
    this.paymentCardDetails = paymentCardDetails;
  }

  public AmendSummaryDetailsDto paymentOptions(PaymentOptionsDto paymentOptions) {
    this.paymentOptions = paymentOptions;
    return this;
  }

  /**
   * Get paymentOptions
   * @return paymentOptions
   */
  @Valid 
  @Schema(name = "paymentOptions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentOptions")
  public PaymentOptionsDto getPaymentOptions() {
    return paymentOptions;
  }

  public void setPaymentOptions(PaymentOptionsDto paymentOptions) {
    this.paymentOptions = paymentOptions;
  }

  public AmendSummaryDetailsDto previousTotal(String previousTotal) {
    this.previousTotal = previousTotal;
    return this;
  }

  /**
   * Get previousTotal
   * @return previousTotal
   */
  
  @Schema(name = "previousTotal", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("previousTotal")
  public String getPreviousTotal() {
    return previousTotal;
  }

  public void setPreviousTotal(String previousTotal) {
    this.previousTotal = previousTotal;
  }

  public AmendSummaryDetailsDto refund(String refund) {
    this.refund = refund;
    return this;
  }

  /**
   * Get refund
   * @return refund
   */
  
  @Schema(name = "refund", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("refund")
  public String getRefund() {
    return refund;
  }

  public void setRefund(String refund) {
    this.refund = refund;
  }

  public AmendSummaryDetailsDto totalCost(String totalCost) {
    this.totalCost = totalCost;
    return this;
  }

  /**
   * Get totalCost
   * @return totalCost
   */
  
  @Schema(name = "totalCost", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalCost")
  public String getTotalCost() {
    return totalCost;
  }

  public void setTotalCost(String totalCost) {
    this.totalCost = totalCost;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AmendSummaryDetailsDto amendSummaryDetailsDto = (AmendSummaryDetailsDto) o;
    return Objects.equals(this.balanceAuthorised, amendSummaryDetailsDto.balanceAuthorised) &&
        Objects.equals(this.balancePaid, amendSummaryDetailsDto.balancePaid) &&
        Objects.equals(this.charitable, amendSummaryDetailsDto.charitable) &&
        Objects.equals(this.nonRefundable, amendSummaryDetailsDto.nonRefundable) &&
        Objects.equals(this.payOnArrival, amendSummaryDetailsDto.payOnArrival) &&
        Objects.equals(this.paymentCardDetails, amendSummaryDetailsDto.paymentCardDetails) &&
        Objects.equals(this.paymentOptions, amendSummaryDetailsDto.paymentOptions) &&
        Objects.equals(this.previousTotal, amendSummaryDetailsDto.previousTotal) &&
        Objects.equals(this.refund, amendSummaryDetailsDto.refund) &&
        Objects.equals(this.totalCost, amendSummaryDetailsDto.totalCost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(balanceAuthorised, balancePaid, charitable, nonRefundable, payOnArrival, paymentCardDetails, paymentOptions, previousTotal, refund, totalCost);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AmendSummaryDetailsDto {\n");
    sb.append("    balanceAuthorised: ").append(toIndentedString(balanceAuthorised)).append("\n");
    sb.append("    balancePaid: ").append(toIndentedString(balancePaid)).append("\n");
    sb.append("    charitable: ").append(toIndentedString(charitable)).append("\n");
    sb.append("    nonRefundable: ").append(toIndentedString(nonRefundable)).append("\n");
    sb.append("    payOnArrival: ").append(toIndentedString(payOnArrival)).append("\n");
    sb.append("    paymentCardDetails: ").append(toIndentedString(paymentCardDetails)).append("\n");
    sb.append("    paymentOptions: ").append(toIndentedString(paymentOptions)).append("\n");
    sb.append("    previousTotal: ").append(toIndentedString(previousTotal)).append("\n");
    sb.append("    refund: ").append(toIndentedString(refund)).append("\n");
    sb.append("    totalCost: ").append(toIndentedString(totalCost)).append("\n");
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


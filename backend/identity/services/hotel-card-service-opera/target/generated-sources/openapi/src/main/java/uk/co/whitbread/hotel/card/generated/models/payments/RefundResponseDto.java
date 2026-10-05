package uk.co.whitbread.hotel.card.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.card.generated.models.payments.BookingDto;
import uk.co.whitbread.hotel.card.generated.models.payments.ProviderResponseDto;
import uk.co.whitbread.hotel.card.generated.models.payments.RefundDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RefundResponseDto
 */

@JsonTypeName("RefundResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:52.919417+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RefundResponseDto {

  private @Nullable BookingDto booking;

  private @Nullable String paymentId;

  private @Nullable ProviderResponseDto providerResponse;

  private @Nullable RefundDto refund;

  private @Nullable String refundId;

  private @Nullable Boolean refunded;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable Date refundedOn;

  private @Nullable String requestId;

  public RefundResponseDto booking(BookingDto booking) {
    this.booking = booking;
    return this;
  }

  /**
   * Get booking
   * @return booking
   */
  @Valid 
  @Schema(name = "booking", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("booking")
  public BookingDto getBooking() {
    return booking;
  }

  public void setBooking(BookingDto booking) {
    this.booking = booking;
  }

  public RefundResponseDto paymentId(String paymentId) {
    this.paymentId = paymentId;
    return this;
  }

  /**
   * Get paymentId
   * @return paymentId
   */
  
  @Schema(name = "paymentId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentId")
  public String getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }

  public RefundResponseDto providerResponse(ProviderResponseDto providerResponse) {
    this.providerResponse = providerResponse;
    return this;
  }

  /**
   * Get providerResponse
   * @return providerResponse
   */
  @Valid 
  @Schema(name = "providerResponse", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("providerResponse")
  public ProviderResponseDto getProviderResponse() {
    return providerResponse;
  }

  public void setProviderResponse(ProviderResponseDto providerResponse) {
    this.providerResponse = providerResponse;
  }

  public RefundResponseDto refund(RefundDto refund) {
    this.refund = refund;
    return this;
  }

  /**
   * Get refund
   * @return refund
   */
  @Valid 
  @Schema(name = "refund", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("refund")
  public RefundDto getRefund() {
    return refund;
  }

  public void setRefund(RefundDto refund) {
    this.refund = refund;
  }

  public RefundResponseDto refundId(String refundId) {
    this.refundId = refundId;
    return this;
  }

  /**
   * Get refundId
   * @return refundId
   */
  
  @Schema(name = "refundId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("refundId")
  public String getRefundId() {
    return refundId;
  }

  public void setRefundId(String refundId) {
    this.refundId = refundId;
  }

  public RefundResponseDto refunded(Boolean refunded) {
    this.refunded = refunded;
    return this;
  }

  /**
   * Get refunded
   * @return refunded
   */
  
  @Schema(name = "refunded", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("refunded")
  public Boolean getRefunded() {
    return refunded;
  }

  public void setRefunded(Boolean refunded) {
    this.refunded = refunded;
  }

  public RefundResponseDto refundedOn(Date refundedOn) {
    this.refundedOn = refundedOn;
    return this;
  }

  /**
   * Get refundedOn
   * @return refundedOn
   */
  @Valid 
  @Schema(name = "refundedOn", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("refundedOn")
  public Date getRefundedOn() {
    return refundedOn;
  }

  public void setRefundedOn(Date refundedOn) {
    this.refundedOn = refundedOn;
  }

  public RefundResponseDto requestId(String requestId) {
    this.requestId = requestId;
    return this;
  }

  /**
   * Get requestId
   * @return requestId
   */
  
  @Schema(name = "requestId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("requestId")
  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RefundResponseDto refundResponse = (RefundResponseDto) o;
    return Objects.equals(this.booking, refundResponse.booking) &&
        Objects.equals(this.paymentId, refundResponse.paymentId) &&
        Objects.equals(this.providerResponse, refundResponse.providerResponse) &&
        Objects.equals(this.refund, refundResponse.refund) &&
        Objects.equals(this.refundId, refundResponse.refundId) &&
        Objects.equals(this.refunded, refundResponse.refunded) &&
        Objects.equals(this.refundedOn, refundResponse.refundedOn) &&
        Objects.equals(this.requestId, refundResponse.requestId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(booking, paymentId, providerResponse, refund, refundId, refunded, refundedOn, requestId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RefundResponseDto {\n");
    sb.append("    booking: ").append(toIndentedString(booking)).append("\n");
    sb.append("    paymentId: ").append(toIndentedString(paymentId)).append("\n");
    sb.append("    providerResponse: ").append(toIndentedString(providerResponse)).append("\n");
    sb.append("    refund: ").append(toIndentedString(refund)).append("\n");
    sb.append("    refundId: ").append(toIndentedString(refundId)).append("\n");
    sb.append("    refunded: ").append(toIndentedString(refunded)).append("\n");
    sb.append("    refundedOn: ").append(toIndentedString(refundedOn)).append("\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
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


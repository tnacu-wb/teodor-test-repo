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
import uk.co.whitbread.hotel.card.generated.models.payments.PaymentDto;
import uk.co.whitbread.hotel.card.generated.models.payments.ProviderResponseDto;
import uk.co.whitbread.hotel.card.generated.models.payments.SaveCardDetailsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PaymentResponseDto
 */

@JsonTypeName("PaymentResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:52.919417+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentResponseDto {

  private @Nullable BookingDto booking;

  private @Nullable String bookingReference;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable Date createdOn;

  private @Nullable PaymentDto payment;

  private @Nullable String paymentId;

  private @Nullable String paymentStatus;

  private @Nullable ProviderResponseDto providerResponse;

  private @Nullable Boolean refunded;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable Date refundedOn;

  private @Nullable String requestId;

  private @Nullable Boolean revisedSolution;

  private @Nullable SaveCardDetailsDto saveCardDetails;

  public PaymentResponseDto booking(BookingDto booking) {
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

  public PaymentResponseDto bookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
    return this;
  }

  /**
   * Get bookingReference
   * @return bookingReference
   */
  
  @Schema(name = "bookingReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingReference")
  public String getBookingReference() {
    return bookingReference;
  }

  public void setBookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
  }

  public PaymentResponseDto createdOn(Date createdOn) {
    this.createdOn = createdOn;
    return this;
  }

  /**
   * Get createdOn
   * @return createdOn
   */
  @Valid 
  @Schema(name = "createdOn", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("createdOn")
  public Date getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(Date createdOn) {
    this.createdOn = createdOn;
  }

  public PaymentResponseDto payment(PaymentDto payment) {
    this.payment = payment;
    return this;
  }

  /**
   * Get payment
   * @return payment
   */
  @Valid 
  @Schema(name = "payment", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("payment")
  public PaymentDto getPayment() {
    return payment;
  }

  public void setPayment(PaymentDto payment) {
    this.payment = payment;
  }

  public PaymentResponseDto paymentId(String paymentId) {
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

  public PaymentResponseDto paymentStatus(String paymentStatus) {
    this.paymentStatus = paymentStatus;
    return this;
  }

  /**
   * Get paymentStatus
   * @return paymentStatus
   */
  
  @Schema(name = "paymentStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentStatus")
  public String getPaymentStatus() {
    return paymentStatus;
  }

  public void setPaymentStatus(String paymentStatus) {
    this.paymentStatus = paymentStatus;
  }

  public PaymentResponseDto providerResponse(ProviderResponseDto providerResponse) {
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

  public PaymentResponseDto refunded(Boolean refunded) {
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

  public PaymentResponseDto refundedOn(Date refundedOn) {
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

  public PaymentResponseDto requestId(String requestId) {
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

  public PaymentResponseDto revisedSolution(Boolean revisedSolution) {
    this.revisedSolution = revisedSolution;
    return this;
  }

  /**
   * Get revisedSolution
   * @return revisedSolution
   */
  
  @Schema(name = "revisedSolution", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("revisedSolution")
  public Boolean getRevisedSolution() {
    return revisedSolution;
  }

  public void setRevisedSolution(Boolean revisedSolution) {
    this.revisedSolution = revisedSolution;
  }

  public PaymentResponseDto saveCardDetails(SaveCardDetailsDto saveCardDetails) {
    this.saveCardDetails = saveCardDetails;
    return this;
  }

  /**
   * Get saveCardDetails
   * @return saveCardDetails
   */
  @Valid 
  @Schema(name = "saveCardDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("saveCardDetails")
  public SaveCardDetailsDto getSaveCardDetails() {
    return saveCardDetails;
  }

  public void setSaveCardDetails(SaveCardDetailsDto saveCardDetails) {
    this.saveCardDetails = saveCardDetails;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentResponseDto paymentResponse = (PaymentResponseDto) o;
    return Objects.equals(this.booking, paymentResponse.booking) &&
        Objects.equals(this.bookingReference, paymentResponse.bookingReference) &&
        Objects.equals(this.createdOn, paymentResponse.createdOn) &&
        Objects.equals(this.payment, paymentResponse.payment) &&
        Objects.equals(this.paymentId, paymentResponse.paymentId) &&
        Objects.equals(this.paymentStatus, paymentResponse.paymentStatus) &&
        Objects.equals(this.providerResponse, paymentResponse.providerResponse) &&
        Objects.equals(this.refunded, paymentResponse.refunded) &&
        Objects.equals(this.refundedOn, paymentResponse.refundedOn) &&
        Objects.equals(this.requestId, paymentResponse.requestId) &&
        Objects.equals(this.revisedSolution, paymentResponse.revisedSolution) &&
        Objects.equals(this.saveCardDetails, paymentResponse.saveCardDetails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(booking, bookingReference, createdOn, payment, paymentId, paymentStatus, providerResponse, refunded, refundedOn, requestId, revisedSolution, saveCardDetails);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentResponseDto {\n");
    sb.append("    booking: ").append(toIndentedString(booking)).append("\n");
    sb.append("    bookingReference: ").append(toIndentedString(bookingReference)).append("\n");
    sb.append("    createdOn: ").append(toIndentedString(createdOn)).append("\n");
    sb.append("    payment: ").append(toIndentedString(payment)).append("\n");
    sb.append("    paymentId: ").append(toIndentedString(paymentId)).append("\n");
    sb.append("    paymentStatus: ").append(toIndentedString(paymentStatus)).append("\n");
    sb.append("    providerResponse: ").append(toIndentedString(providerResponse)).append("\n");
    sb.append("    refunded: ").append(toIndentedString(refunded)).append("\n");
    sb.append("    refundedOn: ").append(toIndentedString(refundedOn)).append("\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    revisedSolution: ").append(toIndentedString(revisedSolution)).append("\n");
    sb.append("    saveCardDetails: ").append(toIndentedString(saveCardDetails)).append("\n");
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


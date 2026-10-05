package uk.co.whitbread.refund.processor.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import uk.co.whitbread.refund.processor.generated.models.payments.BookingDto;
import uk.co.whitbread.refund.processor.generated.models.payments.PaymentRsDto;
import uk.co.whitbread.refund.processor.generated.models.payments.ProviderResponseDto;
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
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:12:20.597747+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentResponseDto {

  private @Nullable String paymentId;

  private @Nullable ProviderResponseDto providerResponse;

  private @Nullable BookingDto booking;

  private @Nullable PaymentRsDto payment;

  private @Nullable Boolean refunded;

  private @Nullable String bookingReference;

  private @Nullable String paymentStatus;

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

  public PaymentResponseDto payment(PaymentRsDto payment) {
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
  public PaymentRsDto getPayment() {
    return payment;
  }

  public void setPayment(PaymentRsDto payment) {
    this.payment = payment;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentResponseDto paymentResponse = (PaymentResponseDto) o;
    return Objects.equals(this.paymentId, paymentResponse.paymentId) &&
        Objects.equals(this.providerResponse, paymentResponse.providerResponse) &&
        Objects.equals(this.booking, paymentResponse.booking) &&
        Objects.equals(this.payment, paymentResponse.payment) &&
        Objects.equals(this.refunded, paymentResponse.refunded) &&
        Objects.equals(this.bookingReference, paymentResponse.bookingReference) &&
        Objects.equals(this.paymentStatus, paymentResponse.paymentStatus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(paymentId, providerResponse, booking, payment, refunded, bookingReference, paymentStatus);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentResponseDto {\n");
    sb.append("    paymentId: ").append(toIndentedString(paymentId)).append("\n");
    sb.append("    providerResponse: ").append(toIndentedString(providerResponse)).append("\n");
    sb.append("    booking: ").append(toIndentedString(booking)).append("\n");
    sb.append("    payment: ").append(toIndentedString(payment)).append("\n");
    sb.append("    refunded: ").append(toIndentedString(refunded)).append("\n");
    sb.append("    bookingReference: ").append(toIndentedString(bookingReference)).append("\n");
    sb.append("    paymentStatus: ").append(toIndentedString(paymentStatus)).append("\n");
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


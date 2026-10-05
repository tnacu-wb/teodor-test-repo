package uk.co.whitbread.refund.processor.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import uk.co.whitbread.refund.processor.generated.models.payments.BookingDto;
import uk.co.whitbread.refund.processor.generated.models.payments.PaymentDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PaymentRequestDto
 */

@JsonTypeName("PaymentRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:12:20.597747+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentRequestDto {

  private String requestId;

  private PaymentDto payment;

  private BookingDto booking;

  private @Nullable String language;

  private @Nullable String paymentSubType;

  private @Nullable String hotelCode;

  public PaymentRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PaymentRequestDto(String requestId, PaymentDto payment, BookingDto booking) {
    this.requestId = requestId;
    this.payment = payment;
    this.booking = booking;
  }

  public PaymentRequestDto requestId(String requestId) {
    this.requestId = requestId;
    return this;
  }

  /**
   * Unique reference for transaction provided by consumer.
   * @return requestId
   */
  @NotNull 
  @Schema(name = "requestId", example = "a0a9f782-98ee-468c-9839-30c487c832a3", description = "Unique reference for transaction provided by consumer.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("requestId")
  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public PaymentRequestDto payment(PaymentDto payment) {
    this.payment = payment;
    return this;
  }

  /**
   * Get payment
   * @return payment
   */
  @NotNull @Valid 
  @Schema(name = "payment", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("payment")
  public PaymentDto getPayment() {
    return payment;
  }

  public void setPayment(PaymentDto payment) {
    this.payment = payment;
  }

  public PaymentRequestDto booking(BookingDto booking) {
    this.booking = booking;
    return this;
  }

  /**
   * Get booking
   * @return booking
   */
  @NotNull @Valid 
  @Schema(name = "booking", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("booking")
  public BookingDto getBooking() {
    return booking;
  }

  public void setBooking(BookingDto booking) {
    this.booking = booking;
  }

  public PaymentRequestDto language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
   */
  
  @Schema(name = "language", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("language")
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public PaymentRequestDto paymentSubType(String paymentSubType) {
    this.paymentSubType = paymentSubType;
    return this;
  }

  /**
   * Get paymentSubType
   * @return paymentSubType
   */
  
  @Schema(name = "paymentSubType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentSubType")
  public String getPaymentSubType() {
    return paymentSubType;
  }

  public void setPaymentSubType(String paymentSubType) {
    this.paymentSubType = paymentSubType;
  }

  public PaymentRequestDto hotelCode(String hotelCode) {
    this.hotelCode = hotelCode;
    return this;
  }

  /**
   * Get hotelCode
   * @return hotelCode
   */
  
  @Schema(name = "hotelCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelCode")
  public String getHotelCode() {
    return hotelCode;
  }

  public void setHotelCode(String hotelCode) {
    this.hotelCode = hotelCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentRequestDto paymentRequest = (PaymentRequestDto) o;
    return Objects.equals(this.requestId, paymentRequest.requestId) &&
        Objects.equals(this.payment, paymentRequest.payment) &&
        Objects.equals(this.booking, paymentRequest.booking) &&
        Objects.equals(this.language, paymentRequest.language) &&
        Objects.equals(this.paymentSubType, paymentRequest.paymentSubType) &&
        Objects.equals(this.hotelCode, paymentRequest.hotelCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, payment, booking, language, paymentSubType, hotelCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentRequestDto {\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    payment: ").append(toIndentedString(payment)).append("\n");
    sb.append("    booking: ").append(toIndentedString(booking)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    paymentSubType: ").append(toIndentedString(paymentSubType)).append("\n");
    sb.append("    hotelCode: ").append(toIndentedString(hotelCode)).append("\n");
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


package uk.co.whitbread.hotel.card.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.card.generated.models.payments.BookingDto;
import uk.co.whitbread.hotel.card.generated.models.payments.PaymentDto;
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
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:52.919417+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentRequestDto {

  private @Nullable Integer amount;

  private BookingDto booking;

  private @Nullable String bookingType;

  private @Nullable String channel;

  private @Nullable String countryCode;

  private @Nullable String hotelCode;

  private @Nullable String language;

  private PaymentDto payment;

  private @Nullable String paymentSubType;

  private String requestId;

  private String sessionId;

  public PaymentRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PaymentRequestDto(BookingDto booking, PaymentDto payment, String requestId, String sessionId) {
    this.booking = booking;
    this.payment = payment;
    this.requestId = requestId;
    this.sessionId = sessionId;
  }

  public PaymentRequestDto amount(Integer amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Get amount
   * @return amount
   */
  
  @Schema(name = "amount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amount")
  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
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

  public PaymentRequestDto bookingType(String bookingType) {
    this.bookingType = bookingType;
    return this;
  }

  /**
   * Get bookingType
   * @return bookingType
   */
  
  @Schema(name = "bookingType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingType")
  public String getBookingType() {
    return bookingType;
  }

  public void setBookingType(String bookingType) {
    this.bookingType = bookingType;
  }

  public PaymentRequestDto channel(String channel) {
    this.channel = channel;
    return this;
  }

  /**
   * Get channel
   * @return channel
   */
  
  @Schema(name = "channel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("channel")
  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public PaymentRequestDto countryCode(String countryCode) {
    this.countryCode = countryCode;
    return this;
  }

  /**
   * Get countryCode
   * @return countryCode
   */
  
  @Schema(name = "countryCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("countryCode")
  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
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

  public PaymentRequestDto sessionId(String sessionId) {
    this.sessionId = sessionId;
    return this;
  }

  /**
   * BART Session Id
   * @return sessionId
   */
  @NotNull 
  @Schema(name = "sessionId", description = "BART Session Id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("sessionId")
  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
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
    return Objects.equals(this.amount, paymentRequest.amount) &&
        Objects.equals(this.booking, paymentRequest.booking) &&
        Objects.equals(this.bookingType, paymentRequest.bookingType) &&
        Objects.equals(this.channel, paymentRequest.channel) &&
        Objects.equals(this.countryCode, paymentRequest.countryCode) &&
        Objects.equals(this.hotelCode, paymentRequest.hotelCode) &&
        Objects.equals(this.language, paymentRequest.language) &&
        Objects.equals(this.payment, paymentRequest.payment) &&
        Objects.equals(this.paymentSubType, paymentRequest.paymentSubType) &&
        Objects.equals(this.requestId, paymentRequest.requestId) &&
        Objects.equals(this.sessionId, paymentRequest.sessionId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, booking, bookingType, channel, countryCode, hotelCode, language, payment, paymentSubType, requestId, sessionId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentRequestDto {\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    booking: ").append(toIndentedString(booking)).append("\n");
    sb.append("    bookingType: ").append(toIndentedString(bookingType)).append("\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    countryCode: ").append(toIndentedString(countryCode)).append("\n");
    sb.append("    hotelCode: ").append(toIndentedString(hotelCode)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    payment: ").append(toIndentedString(payment)).append("\n");
    sb.append("    paymentSubType: ").append(toIndentedString(paymentSubType)).append("\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    sessionId: ").append(toIndentedString(sessionId)).append("\n");
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


package uk.co.whitbread.basket.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.payments.BookingDto;
import uk.co.whitbread.basket.generated.models.payments.RefundDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * TokenRefundRequestDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@JsonTypeName("TokenRefundRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:02.841275+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class TokenRefundRequestDto {

  private BookingDto booking;

  private String hotelCode;

  private RefundDto refund;

  private String requestId;

  public TokenRefundRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public TokenRefundRequestDto(BookingDto booking, String hotelCode, RefundDto refund, String requestId) {
    this.booking = booking;
    this.hotelCode = hotelCode;
    this.refund = refund;
    this.requestId = requestId;
  }

  public TokenRefundRequestDto booking(BookingDto booking) {
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

  public TokenRefundRequestDto hotelCode(String hotelCode) {
    this.hotelCode = hotelCode;
    return this;
  }

  /**
   * Get hotelCode
   * @return hotelCode
   */
  @NotNull 
  @Schema(name = "hotelCode", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelCode")
  public String getHotelCode() {
    return hotelCode;
  }

  public void setHotelCode(String hotelCode) {
    this.hotelCode = hotelCode;
  }

  public TokenRefundRequestDto refund(RefundDto refund) {
    this.refund = refund;
    return this;
  }

  /**
   * Get refund
   * @return refund
   */
  @NotNull @Valid 
  @Schema(name = "refund", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("refund")
  public RefundDto getRefund() {
    return refund;
  }

  public void setRefund(RefundDto refund) {
    this.refund = refund;
  }

  public TokenRefundRequestDto requestId(String requestId) {
    this.requestId = requestId;
    return this;
  }

  /**
   * Get requestId
   * @return requestId
   */
  @NotNull 
  @Schema(name = "requestId", requiredMode = Schema.RequiredMode.REQUIRED)
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
    TokenRefundRequestDto tokenRefundRequest = (TokenRefundRequestDto) o;
    return Objects.equals(this.booking, tokenRefundRequest.booking) &&
        Objects.equals(this.hotelCode, tokenRefundRequest.hotelCode) &&
        Objects.equals(this.refund, tokenRefundRequest.refund) &&
        Objects.equals(this.requestId, tokenRefundRequest.requestId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(booking, hotelCode, refund, requestId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TokenRefundRequestDto {\n");
    sb.append("    booking: ").append(toIndentedString(booking)).append("\n");
    sb.append("    hotelCode: ").append(toIndentedString(hotelCode)).append("\n");
    sb.append("    refund: ").append(toIndentedString(refund)).append("\n");
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


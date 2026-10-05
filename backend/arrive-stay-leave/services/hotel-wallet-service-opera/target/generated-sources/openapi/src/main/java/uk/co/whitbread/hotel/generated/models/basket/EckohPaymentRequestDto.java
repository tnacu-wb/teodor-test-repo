package uk.co.whitbread.hotel.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.basket.EckohBookingDto;
import uk.co.whitbread.hotel.generated.models.basket.EckohPaymentDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * EckohPaymentRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:22.312200+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class EckohPaymentRequestDto {

  private EckohBookingDto booking;

  private EckohPaymentDto payment;

  private String requestId;

  public EckohPaymentRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EckohPaymentRequestDto(EckohBookingDto booking, EckohPaymentDto payment, String requestId) {
    this.booking = booking;
    this.payment = payment;
    this.requestId = requestId;
  }

  public EckohPaymentRequestDto booking(EckohBookingDto booking) {
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
  public EckohBookingDto getBooking() {
    return booking;
  }

  public void setBooking(EckohBookingDto booking) {
    this.booking = booking;
  }

  public EckohPaymentRequestDto payment(EckohPaymentDto payment) {
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
  public EckohPaymentDto getPayment() {
    return payment;
  }

  public void setPayment(EckohPaymentDto payment) {
    this.payment = payment;
  }

  public EckohPaymentRequestDto requestId(String requestId) {
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
    EckohPaymentRequestDto eckohPaymentRequestDto = (EckohPaymentRequestDto) o;
    return Objects.equals(this.booking, eckohPaymentRequestDto.booking) &&
        Objects.equals(this.payment, eckohPaymentRequestDto.payment) &&
        Objects.equals(this.requestId, eckohPaymentRequestDto.requestId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(booking, payment, requestId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EckohPaymentRequestDto {\n");
    sb.append("    booking: ").append(toIndentedString(booking)).append("\n");
    sb.append("    payment: ").append(toIndentedString(payment)).append("\n");
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


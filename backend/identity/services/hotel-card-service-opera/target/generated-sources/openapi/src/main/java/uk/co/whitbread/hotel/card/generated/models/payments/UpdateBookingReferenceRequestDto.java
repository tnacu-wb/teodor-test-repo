package uk.co.whitbread.hotel.card.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UpdateBookingReferenceRequestDto
 */

@JsonTypeName("UpdateBookingReferenceRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:52.919417+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateBookingReferenceRequestDto {

  private String bookingReference;

  private String paymentId;

  public UpdateBookingReferenceRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdateBookingReferenceRequestDto(String bookingReference, String paymentId) {
    this.bookingReference = bookingReference;
    this.paymentId = paymentId;
  }

  public UpdateBookingReferenceRequestDto bookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
    return this;
  }

  /**
   * Booking Confirmation Number
   * @return bookingReference
   */
  @NotNull 
  @Schema(name = "bookingReference", example = "BR260692A", description = "Booking Confirmation Number", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("bookingReference")
  public String getBookingReference() {
    return bookingReference;
  }

  public void setBookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
  }

  public UpdateBookingReferenceRequestDto paymentId(String paymentId) {
    this.paymentId = paymentId;
    return this;
  }

  /**
   * Payment Id
   * @return paymentId
   */
  @NotNull 
  @Schema(name = "paymentId", example = "a4a63ec9-1065-4f91-8625-5fe7a7c5432c", description = "Payment Id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("paymentId")
  public String getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateBookingReferenceRequestDto updateBookingReferenceRequest = (UpdateBookingReferenceRequestDto) o;
    return Objects.equals(this.bookingReference, updateBookingReferenceRequest.bookingReference) &&
        Objects.equals(this.paymentId, updateBookingReferenceRequest.paymentId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingReference, paymentId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateBookingReferenceRequestDto {\n");
    sb.append("    bookingReference: ").append(toIndentedString(bookingReference)).append("\n");
    sb.append("    paymentId: ").append(toIndentedString(paymentId)).append("\n");
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


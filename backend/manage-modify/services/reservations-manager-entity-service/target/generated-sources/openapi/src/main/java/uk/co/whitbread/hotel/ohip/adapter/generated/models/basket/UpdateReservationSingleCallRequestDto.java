package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PaymentRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ReservationGuestRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ReservationPackagesRequestDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * UpdateReservationSingleCallRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:44.119190+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateReservationSingleCallRequestDto {

  private @Nullable ReservationPackagesRequestDto ancillaries;

  private @Nullable String basketReference;

  private @Nullable PaymentRequestDto createPayment;

  private @Nullable String hotelId;

  private @Nullable String priceBreakdownNeeded;

  private @Nullable String requestId;

  private @Nullable ReservationGuestRequestDto reservationGuest;

  public UpdateReservationSingleCallRequestDto ancillaries(ReservationPackagesRequestDto ancillaries) {
    this.ancillaries = ancillaries;
    return this;
  }

  /**
   * Get ancillaries
   * @return ancillaries
   */
  @Valid 
  @Schema(name = "ancillaries", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ancillaries")
  public ReservationPackagesRequestDto getAncillaries() {
    return ancillaries;
  }

  public void setAncillaries(ReservationPackagesRequestDto ancillaries) {
    this.ancillaries = ancillaries;
  }

  public UpdateReservationSingleCallRequestDto basketReference(String basketReference) {
    this.basketReference = basketReference;
    return this;
  }

  /**
   * Get basketReference
   * @return basketReference
   */
  
  @Schema(name = "basketReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("basketReference")
  public String getBasketReference() {
    return basketReference;
  }

  public void setBasketReference(String basketReference) {
    this.basketReference = basketReference;
  }

  public UpdateReservationSingleCallRequestDto createPayment(PaymentRequestDto createPayment) {
    this.createPayment = createPayment;
    return this;
  }

  /**
   * Get createPayment
   * @return createPayment
   */
  @Valid 
  @Schema(name = "createPayment", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("createPayment")
  public PaymentRequestDto getCreatePayment() {
    return createPayment;
  }

  public void setCreatePayment(PaymentRequestDto createPayment) {
    this.createPayment = createPayment;
  }

  public UpdateReservationSingleCallRequestDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public UpdateReservationSingleCallRequestDto priceBreakdownNeeded(String priceBreakdownNeeded) {
    this.priceBreakdownNeeded = priceBreakdownNeeded;
    return this;
  }

  /**
   * Get priceBreakdownNeeded
   * @return priceBreakdownNeeded
   */
  
  @Schema(name = "priceBreakdownNeeded", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("priceBreakdownNeeded")
  public String getPriceBreakdownNeeded() {
    return priceBreakdownNeeded;
  }

  public void setPriceBreakdownNeeded(String priceBreakdownNeeded) {
    this.priceBreakdownNeeded = priceBreakdownNeeded;
  }

  public UpdateReservationSingleCallRequestDto requestId(String requestId) {
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

  public UpdateReservationSingleCallRequestDto reservationGuest(ReservationGuestRequestDto reservationGuest) {
    this.reservationGuest = reservationGuest;
    return this;
  }

  /**
   * Get reservationGuest
   * @return reservationGuest
   */
  @Valid 
  @Schema(name = "reservationGuest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationGuest")
  public ReservationGuestRequestDto getReservationGuest() {
    return reservationGuest;
  }

  public void setReservationGuest(ReservationGuestRequestDto reservationGuest) {
    this.reservationGuest = reservationGuest;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateReservationSingleCallRequestDto updateReservationSingleCallRequestDto = (UpdateReservationSingleCallRequestDto) o;
    return Objects.equals(this.ancillaries, updateReservationSingleCallRequestDto.ancillaries) &&
        Objects.equals(this.basketReference, updateReservationSingleCallRequestDto.basketReference) &&
        Objects.equals(this.createPayment, updateReservationSingleCallRequestDto.createPayment) &&
        Objects.equals(this.hotelId, updateReservationSingleCallRequestDto.hotelId) &&
        Objects.equals(this.priceBreakdownNeeded, updateReservationSingleCallRequestDto.priceBreakdownNeeded) &&
        Objects.equals(this.requestId, updateReservationSingleCallRequestDto.requestId) &&
        Objects.equals(this.reservationGuest, updateReservationSingleCallRequestDto.reservationGuest);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ancillaries, basketReference, createPayment, hotelId, priceBreakdownNeeded, requestId, reservationGuest);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateReservationSingleCallRequestDto {\n");
    sb.append("    ancillaries: ").append(toIndentedString(ancillaries)).append("\n");
    sb.append("    basketReference: ").append(toIndentedString(basketReference)).append("\n");
    sb.append("    createPayment: ").append(toIndentedString(createPayment)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    priceBreakdownNeeded: ").append(toIndentedString(priceBreakdownNeeded)).append("\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    reservationGuest: ").append(toIndentedString(reservationGuest)).append("\n");
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


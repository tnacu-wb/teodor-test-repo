package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BusinessItemsRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConfirmReservationRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationGuestRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackagesRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.SpecialRequestsDto;
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

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateReservationSingleCallRequestDto {

  private @Nullable BusinessItemsRequestDto businessItem;

  private @Nullable ConfirmReservationRequestDto paymentDetails;

  private @Nullable ReservationGuestRequestDto reservationGuestDetails;

  private @Nullable ReservationPackagesRequestDto reservationPackages;

  private @Nullable SpecialRequestsDto specialRequests;

  public UpdateReservationSingleCallRequestDto businessItem(BusinessItemsRequestDto businessItem) {
    this.businessItem = businessItem;
    return this;
  }

  /**
   * Get businessItem
   * @return businessItem
   */
  @Valid 
  @Schema(name = "businessItem", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("businessItem")
  public BusinessItemsRequestDto getBusinessItem() {
    return businessItem;
  }

  public void setBusinessItem(BusinessItemsRequestDto businessItem) {
    this.businessItem = businessItem;
  }

  public UpdateReservationSingleCallRequestDto paymentDetails(ConfirmReservationRequestDto paymentDetails) {
    this.paymentDetails = paymentDetails;
    return this;
  }

  /**
   * Get paymentDetails
   * @return paymentDetails
   */
  @Valid 
  @Schema(name = "paymentDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentDetails")
  public ConfirmReservationRequestDto getPaymentDetails() {
    return paymentDetails;
  }

  public void setPaymentDetails(ConfirmReservationRequestDto paymentDetails) {
    this.paymentDetails = paymentDetails;
  }

  public UpdateReservationSingleCallRequestDto reservationGuestDetails(ReservationGuestRequestDto reservationGuestDetails) {
    this.reservationGuestDetails = reservationGuestDetails;
    return this;
  }

  /**
   * Get reservationGuestDetails
   * @return reservationGuestDetails
   */
  @Valid 
  @Schema(name = "reservationGuestDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationGuestDetails")
  public ReservationGuestRequestDto getReservationGuestDetails() {
    return reservationGuestDetails;
  }

  public void setReservationGuestDetails(ReservationGuestRequestDto reservationGuestDetails) {
    this.reservationGuestDetails = reservationGuestDetails;
  }

  public UpdateReservationSingleCallRequestDto reservationPackages(ReservationPackagesRequestDto reservationPackages) {
    this.reservationPackages = reservationPackages;
    return this;
  }

  /**
   * Get reservationPackages
   * @return reservationPackages
   */
  @Valid 
  @Schema(name = "reservationPackages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationPackages")
  public ReservationPackagesRequestDto getReservationPackages() {
    return reservationPackages;
  }

  public void setReservationPackages(ReservationPackagesRequestDto reservationPackages) {
    this.reservationPackages = reservationPackages;
  }

  public UpdateReservationSingleCallRequestDto specialRequests(SpecialRequestsDto specialRequests) {
    this.specialRequests = specialRequests;
    return this;
  }

  /**
   * Get specialRequests
   * @return specialRequests
   */
  @Valid 
  @Schema(name = "specialRequests", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("specialRequests")
  public SpecialRequestsDto getSpecialRequests() {
    return specialRequests;
  }

  public void setSpecialRequests(SpecialRequestsDto specialRequests) {
    this.specialRequests = specialRequests;
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
    return Objects.equals(this.businessItem, updateReservationSingleCallRequestDto.businessItem) &&
        Objects.equals(this.paymentDetails, updateReservationSingleCallRequestDto.paymentDetails) &&
        Objects.equals(this.reservationGuestDetails, updateReservationSingleCallRequestDto.reservationGuestDetails) &&
        Objects.equals(this.reservationPackages, updateReservationSingleCallRequestDto.reservationPackages) &&
        Objects.equals(this.specialRequests, updateReservationSingleCallRequestDto.specialRequests);
  }

  @Override
  public int hashCode() {
    return Objects.hash(businessItem, paymentDetails, reservationGuestDetails, reservationPackages, specialRequests);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateReservationSingleCallRequestDto {\n");
    sb.append("    businessItem: ").append(toIndentedString(businessItem)).append("\n");
    sb.append("    paymentDetails: ").append(toIndentedString(paymentDetails)).append("\n");
    sb.append("    reservationGuestDetails: ").append(toIndentedString(reservationGuestDetails)).append("\n");
    sb.append("    reservationPackages: ").append(toIndentedString(reservationPackages)).append("\n");
    sb.append("    specialRequests: ").append(toIndentedString(specialRequests)).append("\n");
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


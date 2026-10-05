package uk.co.whitbread.hotel.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.basket.Billing;
import uk.co.whitbread.hotel.generated.models.basket.DepositPolicies;
import uk.co.whitbread.hotel.generated.models.basket.Guest;
import uk.co.whitbread.hotel.generated.models.basket.PaymentCard;
import uk.co.whitbread.hotel.generated.models.basket.ReservationPackagesDetails;
import uk.co.whitbread.hotel.generated.models.basket.RoomStay;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Reservation
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:22.312200+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Reservation {

  private @Nullable Billing billing;

  @Valid
  private List<@Valid DepositPolicies> depositPolicies = new ArrayList<>();

  private @Nullable PaymentCard paymentCard;

  @Valid
  private List<@Valid Guest> reservationGuestList = new ArrayList<>();

  private @Nullable String reservationId;

  @Valid
  private List<@Valid ReservationPackagesDetails> reservationPackageList = new ArrayList<>();

  private @Nullable RoomStay roomStay;

  public Reservation billing(Billing billing) {
    this.billing = billing;
    return this;
  }

  /**
   * Get billing
   * @return billing
   */
  @Valid 
  @Schema(name = "billing", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("billing")
  public Billing getBilling() {
    return billing;
  }

  public void setBilling(Billing billing) {
    this.billing = billing;
  }

  public Reservation depositPolicies(List<@Valid DepositPolicies> depositPolicies) {
    this.depositPolicies = depositPolicies;
    return this;
  }

  public Reservation addDepositPoliciesItem(DepositPolicies depositPoliciesItem) {
    if (this.depositPolicies == null) {
      this.depositPolicies = new ArrayList<>();
    }
    this.depositPolicies.add(depositPoliciesItem);
    return this;
  }

  /**
   * Get depositPolicies
   * @return depositPolicies
   */
  @Valid 
  @Schema(name = "depositPolicies", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("depositPolicies")
  public List<@Valid DepositPolicies> getDepositPolicies() {
    return depositPolicies;
  }

  public void setDepositPolicies(List<@Valid DepositPolicies> depositPolicies) {
    this.depositPolicies = depositPolicies;
  }

  public Reservation paymentCard(PaymentCard paymentCard) {
    this.paymentCard = paymentCard;
    return this;
  }

  /**
   * Get paymentCard
   * @return paymentCard
   */
  @Valid 
  @Schema(name = "paymentCard", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentCard")
  public PaymentCard getPaymentCard() {
    return paymentCard;
  }

  public void setPaymentCard(PaymentCard paymentCard) {
    this.paymentCard = paymentCard;
  }

  public Reservation reservationGuestList(List<@Valid Guest> reservationGuestList) {
    this.reservationGuestList = reservationGuestList;
    return this;
  }

  public Reservation addReservationGuestListItem(Guest reservationGuestListItem) {
    if (this.reservationGuestList == null) {
      this.reservationGuestList = new ArrayList<>();
    }
    this.reservationGuestList.add(reservationGuestListItem);
    return this;
  }

  /**
   * Get reservationGuestList
   * @return reservationGuestList
   */
  @Valid 
  @Schema(name = "reservationGuestList", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationGuestList")
  public List<@Valid Guest> getReservationGuestList() {
    return reservationGuestList;
  }

  public void setReservationGuestList(List<@Valid Guest> reservationGuestList) {
    this.reservationGuestList = reservationGuestList;
  }

  public Reservation reservationId(String reservationId) {
    this.reservationId = reservationId;
    return this;
  }

  /**
   * Get reservationId
   * @return reservationId
   */
  
  @Schema(name = "reservationId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationId")
  public String getReservationId() {
    return reservationId;
  }

  public void setReservationId(String reservationId) {
    this.reservationId = reservationId;
  }

  public Reservation reservationPackageList(List<@Valid ReservationPackagesDetails> reservationPackageList) {
    this.reservationPackageList = reservationPackageList;
    return this;
  }

  public Reservation addReservationPackageListItem(ReservationPackagesDetails reservationPackageListItem) {
    if (this.reservationPackageList == null) {
      this.reservationPackageList = new ArrayList<>();
    }
    this.reservationPackageList.add(reservationPackageListItem);
    return this;
  }

  /**
   * Get reservationPackageList
   * @return reservationPackageList
   */
  @Valid 
  @Schema(name = "reservationPackageList", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationPackageList")
  public List<@Valid ReservationPackagesDetails> getReservationPackageList() {
    return reservationPackageList;
  }

  public void setReservationPackageList(List<@Valid ReservationPackagesDetails> reservationPackageList) {
    this.reservationPackageList = reservationPackageList;
  }

  public Reservation roomStay(RoomStay roomStay) {
    this.roomStay = roomStay;
    return this;
  }

  /**
   * Get roomStay
   * @return roomStay
   */
  @Valid 
  @Schema(name = "roomStay", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomStay")
  public RoomStay getRoomStay() {
    return roomStay;
  }

  public void setRoomStay(RoomStay roomStay) {
    this.roomStay = roomStay;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Reservation reservation = (Reservation) o;
    return Objects.equals(this.billing, reservation.billing) &&
        Objects.equals(this.depositPolicies, reservation.depositPolicies) &&
        Objects.equals(this.paymentCard, reservation.paymentCard) &&
        Objects.equals(this.reservationGuestList, reservation.reservationGuestList) &&
        Objects.equals(this.reservationId, reservation.reservationId) &&
        Objects.equals(this.reservationPackageList, reservation.reservationPackageList) &&
        Objects.equals(this.roomStay, reservation.roomStay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(billing, depositPolicies, paymentCard, reservationGuestList, reservationId, reservationPackageList, roomStay);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Reservation {\n");
    sb.append("    billing: ").append(toIndentedString(billing)).append("\n");
    sb.append("    depositPolicies: ").append(toIndentedString(depositPolicies)).append("\n");
    sb.append("    paymentCard: ").append(toIndentedString(paymentCard)).append("\n");
    sb.append("    reservationGuestList: ").append(toIndentedString(reservationGuestList)).append("\n");
    sb.append("    reservationId: ").append(toIndentedString(reservationId)).append("\n");
    sb.append("    reservationPackageList: ").append(toIndentedString(reservationPackageList)).append("\n");
    sb.append("    roomStay: ").append(toIndentedString(roomStay)).append("\n");
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


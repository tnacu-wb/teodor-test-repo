package uk.co.whitbread.cdh.adapter.service.generated.models.companyReports;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.cdh.adapter.service.generated.models.companyReports.GuestsDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.companyReports.PaymentCardDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.companyReports.PriceDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:17.912171+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomsDto {

  private @Nullable Boolean carDetails;

  private @Nullable Boolean cot;

  @Valid
  private List<@Valid GuestsDto> guests = new ArrayList<>();

  private @Nullable String invoiceNumber;

  private @Nullable String noOfAdults;

  private @Nullable String noOfChildren;

  @Valid
  private List<@Valid PaymentCardDto> paymentCards = new ArrayList<>();

  private @Nullable String reservationId;

  private @Nullable PriceDto roomCost;

  private @Nullable String roomId;

  private @Nullable String roomNumber;

  private @Nullable String roomType;

  private @Nullable String status;

  private @Nullable PriceDto totalCost;

  private @Nullable PriceDto upsellTotalCost;

  public RoomsDto carDetails(Boolean carDetails) {
    this.carDetails = carDetails;
    return this;
  }

  /**
   * Get carDetails
   * @return carDetails
   */
  
  @Schema(name = "carDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("carDetails")
  public Boolean getCarDetails() {
    return carDetails;
  }

  public void setCarDetails(Boolean carDetails) {
    this.carDetails = carDetails;
  }

  public RoomsDto cot(Boolean cot) {
    this.cot = cot;
    return this;
  }

  /**
   * Get cot
   * @return cot
   */
  
  @Schema(name = "cot", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cot")
  public Boolean getCot() {
    return cot;
  }

  public void setCot(Boolean cot) {
    this.cot = cot;
  }

  public RoomsDto guests(List<@Valid GuestsDto> guests) {
    this.guests = guests;
    return this;
  }

  public RoomsDto addGuestsItem(GuestsDto guestsItem) {
    if (this.guests == null) {
      this.guests = new ArrayList<>();
    }
    this.guests.add(guestsItem);
    return this;
  }

  /**
   * Get guests
   * @return guests
   */
  @Valid 
  @Schema(name = "guests", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("guests")
  public List<@Valid GuestsDto> getGuests() {
    return guests;
  }

  public void setGuests(List<@Valid GuestsDto> guests) {
    this.guests = guests;
  }

  public RoomsDto invoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
    return this;
  }

  /**
   * Get invoiceNumber
   * @return invoiceNumber
   */
  
  @Schema(name = "invoiceNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("invoiceNumber")
  public String getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  public RoomsDto noOfAdults(String noOfAdults) {
    this.noOfAdults = noOfAdults;
    return this;
  }

  /**
   * Get noOfAdults
   * @return noOfAdults
   */
  
  @Schema(name = "noOfAdults", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("noOfAdults")
  public String getNoOfAdults() {
    return noOfAdults;
  }

  public void setNoOfAdults(String noOfAdults) {
    this.noOfAdults = noOfAdults;
  }

  public RoomsDto noOfChildren(String noOfChildren) {
    this.noOfChildren = noOfChildren;
    return this;
  }

  /**
   * Get noOfChildren
   * @return noOfChildren
   */
  
  @Schema(name = "noOfChildren", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("noOfChildren")
  public String getNoOfChildren() {
    return noOfChildren;
  }

  public void setNoOfChildren(String noOfChildren) {
    this.noOfChildren = noOfChildren;
  }

  public RoomsDto paymentCards(List<@Valid PaymentCardDto> paymentCards) {
    this.paymentCards = paymentCards;
    return this;
  }

  public RoomsDto addPaymentCardsItem(PaymentCardDto paymentCardsItem) {
    if (this.paymentCards == null) {
      this.paymentCards = new ArrayList<>();
    }
    this.paymentCards.add(paymentCardsItem);
    return this;
  }

  /**
   * Get paymentCards
   * @return paymentCards
   */
  @Valid 
  @Schema(name = "paymentCards", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentCards")
  public List<@Valid PaymentCardDto> getPaymentCards() {
    return paymentCards;
  }

  public void setPaymentCards(List<@Valid PaymentCardDto> paymentCards) {
    this.paymentCards = paymentCards;
  }

  public RoomsDto reservationId(String reservationId) {
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

  public RoomsDto roomCost(PriceDto roomCost) {
    this.roomCost = roomCost;
    return this;
  }

  /**
   * Get roomCost
   * @return roomCost
   */
  @Valid 
  @Schema(name = "roomCost", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomCost")
  public PriceDto getRoomCost() {
    return roomCost;
  }

  public void setRoomCost(PriceDto roomCost) {
    this.roomCost = roomCost;
  }

  public RoomsDto roomId(String roomId) {
    this.roomId = roomId;
    return this;
  }

  /**
   * Get roomId
   * @return roomId
   */
  
  @Schema(name = "roomId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomId")
  public String getRoomId() {
    return roomId;
  }

  public void setRoomId(String roomId) {
    this.roomId = roomId;
  }

  public RoomsDto roomNumber(String roomNumber) {
    this.roomNumber = roomNumber;
    return this;
  }

  /**
   * Get roomNumber
   * @return roomNumber
   */
  
  @Schema(name = "roomNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomNumber")
  public String getRoomNumber() {
    return roomNumber;
  }

  public void setRoomNumber(String roomNumber) {
    this.roomNumber = roomNumber;
  }

  public RoomsDto roomType(String roomType) {
    this.roomType = roomType;
    return this;
  }

  /**
   * Get roomType
   * @return roomType
   */
  
  @Schema(name = "roomType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomType")
  public String getRoomType() {
    return roomType;
  }

  public void setRoomType(String roomType) {
    this.roomType = roomType;
  }

  public RoomsDto status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public RoomsDto totalCost(PriceDto totalCost) {
    this.totalCost = totalCost;
    return this;
  }

  /**
   * Get totalCost
   * @return totalCost
   */
  @Valid 
  @Schema(name = "totalCost", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalCost")
  public PriceDto getTotalCost() {
    return totalCost;
  }

  public void setTotalCost(PriceDto totalCost) {
    this.totalCost = totalCost;
  }

  public RoomsDto upsellTotalCost(PriceDto upsellTotalCost) {
    this.upsellTotalCost = upsellTotalCost;
    return this;
  }

  /**
   * Get upsellTotalCost
   * @return upsellTotalCost
   */
  @Valid 
  @Schema(name = "upsellTotalCost", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("upsellTotalCost")
  public PriceDto getUpsellTotalCost() {
    return upsellTotalCost;
  }

  public void setUpsellTotalCost(PriceDto upsellTotalCost) {
    this.upsellTotalCost = upsellTotalCost;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomsDto roomsDto = (RoomsDto) o;
    return Objects.equals(this.carDetails, roomsDto.carDetails) &&
        Objects.equals(this.cot, roomsDto.cot) &&
        Objects.equals(this.guests, roomsDto.guests) &&
        Objects.equals(this.invoiceNumber, roomsDto.invoiceNumber) &&
        Objects.equals(this.noOfAdults, roomsDto.noOfAdults) &&
        Objects.equals(this.noOfChildren, roomsDto.noOfChildren) &&
        Objects.equals(this.paymentCards, roomsDto.paymentCards) &&
        Objects.equals(this.reservationId, roomsDto.reservationId) &&
        Objects.equals(this.roomCost, roomsDto.roomCost) &&
        Objects.equals(this.roomId, roomsDto.roomId) &&
        Objects.equals(this.roomNumber, roomsDto.roomNumber) &&
        Objects.equals(this.roomType, roomsDto.roomType) &&
        Objects.equals(this.status, roomsDto.status) &&
        Objects.equals(this.totalCost, roomsDto.totalCost) &&
        Objects.equals(this.upsellTotalCost, roomsDto.upsellTotalCost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(carDetails, cot, guests, invoiceNumber, noOfAdults, noOfChildren, paymentCards, reservationId, roomCost, roomId, roomNumber, roomType, status, totalCost, upsellTotalCost);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomsDto {\n");
    sb.append("    carDetails: ").append(toIndentedString(carDetails)).append("\n");
    sb.append("    cot: ").append(toIndentedString(cot)).append("\n");
    sb.append("    guests: ").append(toIndentedString(guests)).append("\n");
    sb.append("    invoiceNumber: ").append(toIndentedString(invoiceNumber)).append("\n");
    sb.append("    noOfAdults: ").append(toIndentedString(noOfAdults)).append("\n");
    sb.append("    noOfChildren: ").append(toIndentedString(noOfChildren)).append("\n");
    sb.append("    paymentCards: ").append(toIndentedString(paymentCards)).append("\n");
    sb.append("    reservationId: ").append(toIndentedString(reservationId)).append("\n");
    sb.append("    roomCost: ").append(toIndentedString(roomCost)).append("\n");
    sb.append("    roomId: ").append(toIndentedString(roomId)).append("\n");
    sb.append("    roomNumber: ").append(toIndentedString(roomNumber)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    totalCost: ").append(toIndentedString(totalCost)).append("\n");
    sb.append("    upsellTotalCost: ").append(toIndentedString(upsellTotalCost)).append("\n");
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


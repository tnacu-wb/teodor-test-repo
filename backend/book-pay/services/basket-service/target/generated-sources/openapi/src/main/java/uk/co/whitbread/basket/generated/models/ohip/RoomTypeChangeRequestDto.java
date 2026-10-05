package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomTypeChangeRequestDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomTypeChangeRequestDto {

  @Valid
  private List<Integer> adultsNumber = new ArrayList<>();

  private String basketReferenceId;

  @Valid
  private List<Integer> childrenNumber = new ArrayList<>();

  private String currency;

  private String endDate;

  private String hotelId;

  private String rateCode;

  @Valid
  private List<String> reservationIds = new ArrayList<>();

  @Valid
  private List<String> roomTypes = new ArrayList<>();

  private String startDate;

  public RoomTypeChangeRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RoomTypeChangeRequestDto(List<Integer> adultsNumber, String basketReferenceId, String currency, String endDate, String hotelId, String rateCode, List<String> reservationIds, List<String> roomTypes, String startDate) {
    this.adultsNumber = adultsNumber;
    this.basketReferenceId = basketReferenceId;
    this.currency = currency;
    this.endDate = endDate;
    this.hotelId = hotelId;
    this.rateCode = rateCode;
    this.reservationIds = reservationIds;
    this.roomTypes = roomTypes;
    this.startDate = startDate;
  }

  public RoomTypeChangeRequestDto adultsNumber(List<Integer> adultsNumber) {
    this.adultsNumber = adultsNumber;
    return this;
  }

  public RoomTypeChangeRequestDto addAdultsNumberItem(Integer adultsNumberItem) {
    if (this.adultsNumber == null) {
      this.adultsNumber = new ArrayList<>();
    }
    this.adultsNumber.add(adultsNumberItem);
    return this;
  }

  /**
   * Get adultsNumber
   * @return adultsNumber
   */
  @NotNull @Size(min = 1, max = 2147483647) 
  @Schema(name = "adultsNumber", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("adultsNumber")
  public List<Integer> getAdultsNumber() {
    return adultsNumber;
  }

  public void setAdultsNumber(List<Integer> adultsNumber) {
    this.adultsNumber = adultsNumber;
  }

  public RoomTypeChangeRequestDto basketReferenceId(String basketReferenceId) {
    this.basketReferenceId = basketReferenceId;
    return this;
  }

  /**
   * Get basketReferenceId
   * @return basketReferenceId
   */
  @NotNull 
  @Schema(name = "basketReferenceId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("basketReferenceId")
  public String getBasketReferenceId() {
    return basketReferenceId;
  }

  public void setBasketReferenceId(String basketReferenceId) {
    this.basketReferenceId = basketReferenceId;
  }

  public RoomTypeChangeRequestDto childrenNumber(List<Integer> childrenNumber) {
    this.childrenNumber = childrenNumber;
    return this;
  }

  public RoomTypeChangeRequestDto addChildrenNumberItem(Integer childrenNumberItem) {
    if (this.childrenNumber == null) {
      this.childrenNumber = new ArrayList<>();
    }
    this.childrenNumber.add(childrenNumberItem);
    return this;
  }

  /**
   * Get childrenNumber
   * @return childrenNumber
   */
  
  @Schema(name = "childrenNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("childrenNumber")
  public List<Integer> getChildrenNumber() {
    return childrenNumber;
  }

  public void setChildrenNumber(List<Integer> childrenNumber) {
    this.childrenNumber = childrenNumber;
  }

  public RoomTypeChangeRequestDto currency(String currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Get currency
   * @return currency
   */
  @NotNull 
  @Schema(name = "currency", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("currency")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public RoomTypeChangeRequestDto endDate(String endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * Get endDate
   * @return endDate
   */
  @NotNull 
  @Schema(name = "endDate", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("endDate")
  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public RoomTypeChangeRequestDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  @NotNull 
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public RoomTypeChangeRequestDto rateCode(String rateCode) {
    this.rateCode = rateCode;
    return this;
  }

  /**
   * Get rateCode
   * @return rateCode
   */
  @NotNull 
  @Schema(name = "rateCode", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("rateCode")
  public String getRateCode() {
    return rateCode;
  }

  public void setRateCode(String rateCode) {
    this.rateCode = rateCode;
  }

  public RoomTypeChangeRequestDto reservationIds(List<String> reservationIds) {
    this.reservationIds = reservationIds;
    return this;
  }

  public RoomTypeChangeRequestDto addReservationIdsItem(String reservationIdsItem) {
    if (this.reservationIds == null) {
      this.reservationIds = new ArrayList<>();
    }
    this.reservationIds.add(reservationIdsItem);
    return this;
  }

  /**
   * Get reservationIds
   * @return reservationIds
   */
  @NotNull 
  @Schema(name = "reservationIds", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reservationIds")
  public List<String> getReservationIds() {
    return reservationIds;
  }

  public void setReservationIds(List<String> reservationIds) {
    this.reservationIds = reservationIds;
  }

  public RoomTypeChangeRequestDto roomTypes(List<String> roomTypes) {
    this.roomTypes = roomTypes;
    return this;
  }

  public RoomTypeChangeRequestDto addRoomTypesItem(String roomTypesItem) {
    if (this.roomTypes == null) {
      this.roomTypes = new ArrayList<>();
    }
    this.roomTypes.add(roomTypesItem);
    return this;
  }

  /**
   * Get roomTypes
   * @return roomTypes
   */
  @NotNull @Size(min = 1, max = 2147483647) 
  @Schema(name = "roomTypes", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("roomTypes")
  public List<String> getRoomTypes() {
    return roomTypes;
  }

  public void setRoomTypes(List<String> roomTypes) {
    this.roomTypes = roomTypes;
  }

  public RoomTypeChangeRequestDto startDate(String startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Get startDate
   * @return startDate
   */
  @NotNull 
  @Schema(name = "startDate", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("startDate")
  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomTypeChangeRequestDto roomTypeChangeRequestDto = (RoomTypeChangeRequestDto) o;
    return Objects.equals(this.adultsNumber, roomTypeChangeRequestDto.adultsNumber) &&
        Objects.equals(this.basketReferenceId, roomTypeChangeRequestDto.basketReferenceId) &&
        Objects.equals(this.childrenNumber, roomTypeChangeRequestDto.childrenNumber) &&
        Objects.equals(this.currency, roomTypeChangeRequestDto.currency) &&
        Objects.equals(this.endDate, roomTypeChangeRequestDto.endDate) &&
        Objects.equals(this.hotelId, roomTypeChangeRequestDto.hotelId) &&
        Objects.equals(this.rateCode, roomTypeChangeRequestDto.rateCode) &&
        Objects.equals(this.reservationIds, roomTypeChangeRequestDto.reservationIds) &&
        Objects.equals(this.roomTypes, roomTypeChangeRequestDto.roomTypes) &&
        Objects.equals(this.startDate, roomTypeChangeRequestDto.startDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adultsNumber, basketReferenceId, childrenNumber, currency, endDate, hotelId, rateCode, reservationIds, roomTypes, startDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomTypeChangeRequestDto {\n");
    sb.append("    adultsNumber: ").append(toIndentedString(adultsNumber)).append("\n");
    sb.append("    basketReferenceId: ").append(toIndentedString(basketReferenceId)).append("\n");
    sb.append("    childrenNumber: ").append(toIndentedString(childrenNumber)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    rateCode: ").append(toIndentedString(rateCode)).append("\n");
    sb.append("    reservationIds: ").append(toIndentedString(reservationIds)).append("\n");
    sb.append("    roomTypes: ").append(toIndentedString(roomTypes)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
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


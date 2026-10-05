package uk.co.whitbread.hotel.generated.models.reservation;

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
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * UpdateRateCodeRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateRateCodeRequestDto {

  @Valid
  private List<Integer> adultsNumber = new ArrayList<>();

  private String basketReferenceId;

  @Valid
  private List<Integer> childrenNumber = new ArrayList<>();

  private @Nullable String currency;

  private String endDate;

  private @Nullable String hotelId;

  private String rateCode;

  @Valid
  private List<String> roomType = new ArrayList<>();

  private String startDate;

  public UpdateRateCodeRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdateRateCodeRequestDto(List<Integer> adultsNumber, String basketReferenceId, String endDate, String rateCode, String startDate) {
    this.adultsNumber = adultsNumber;
    this.basketReferenceId = basketReferenceId;
    this.endDate = endDate;
    this.rateCode = rateCode;
    this.startDate = startDate;
  }

  public UpdateRateCodeRequestDto adultsNumber(List<Integer> adultsNumber) {
    this.adultsNumber = adultsNumber;
    return this;
  }

  public UpdateRateCodeRequestDto addAdultsNumberItem(Integer adultsNumberItem) {
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
  @NotNull 
  @Schema(name = "adultsNumber", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("adultsNumber")
  public List<Integer> getAdultsNumber() {
    return adultsNumber;
  }

  public void setAdultsNumber(List<Integer> adultsNumber) {
    this.adultsNumber = adultsNumber;
  }

  public UpdateRateCodeRequestDto basketReferenceId(String basketReferenceId) {
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

  public UpdateRateCodeRequestDto childrenNumber(List<Integer> childrenNumber) {
    this.childrenNumber = childrenNumber;
    return this;
  }

  public UpdateRateCodeRequestDto addChildrenNumberItem(Integer childrenNumberItem) {
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

  public UpdateRateCodeRequestDto currency(String currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Get currency
   * @return currency
   */
  
  @Schema(name = "currency", example = "EUR", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currency")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public UpdateRateCodeRequestDto endDate(String endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * Get endDate
   * @return endDate
   */
  @NotNull 
  @Schema(name = "endDate", example = "2015-10-21", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("endDate")
  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public UpdateRateCodeRequestDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  
  @Schema(name = "hotelId", example = "LONSTM", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public UpdateRateCodeRequestDto rateCode(String rateCode) {
    this.rateCode = rateCode;
    return this;
  }

  /**
   * Get rateCode
   * @return rateCode
   */
  @NotNull 
  @Schema(name = "rateCode", example = "FLEXRATE", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("rateCode")
  public String getRateCode() {
    return rateCode;
  }

  public void setRateCode(String rateCode) {
    this.rateCode = rateCode;
  }

  public UpdateRateCodeRequestDto roomType(List<String> roomType) {
    this.roomType = roomType;
    return this;
  }

  public UpdateRateCodeRequestDto addRoomTypeItem(String roomTypeItem) {
    if (this.roomType == null) {
      this.roomType = new ArrayList<>();
    }
    this.roomType.add(roomTypeItem);
    return this;
  }

  /**
   * Get roomType
   * @return roomType
   */
  
  @Schema(name = "roomType", example = "DOUBLE,FMQUAD", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomType")
  public List<String> getRoomType() {
    return roomType;
  }

  public void setRoomType(List<String> roomType) {
    this.roomType = roomType;
  }

  public UpdateRateCodeRequestDto startDate(String startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Get startDate
   * @return startDate
   */
  @NotNull 
  @Schema(name = "startDate", example = "2015-10-20", requiredMode = Schema.RequiredMode.REQUIRED)
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
    UpdateRateCodeRequestDto updateRateCodeRequestDto = (UpdateRateCodeRequestDto) o;
    return Objects.equals(this.adultsNumber, updateRateCodeRequestDto.adultsNumber) &&
        Objects.equals(this.basketReferenceId, updateRateCodeRequestDto.basketReferenceId) &&
        Objects.equals(this.childrenNumber, updateRateCodeRequestDto.childrenNumber) &&
        Objects.equals(this.currency, updateRateCodeRequestDto.currency) &&
        Objects.equals(this.endDate, updateRateCodeRequestDto.endDate) &&
        Objects.equals(this.hotelId, updateRateCodeRequestDto.hotelId) &&
        Objects.equals(this.rateCode, updateRateCodeRequestDto.rateCode) &&
        Objects.equals(this.roomType, updateRateCodeRequestDto.roomType) &&
        Objects.equals(this.startDate, updateRateCodeRequestDto.startDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adultsNumber, basketReferenceId, childrenNumber, currency, endDate, hotelId, rateCode, roomType, startDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateRateCodeRequestDto {\n");
    sb.append("    adultsNumber: ").append(toIndentedString(adultsNumber)).append("\n");
    sb.append("    basketReferenceId: ").append(toIndentedString(basketReferenceId)).append("\n");
    sb.append("    childrenNumber: ").append(toIndentedString(childrenNumber)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    rateCode: ").append(toIndentedString(rateCode)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
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


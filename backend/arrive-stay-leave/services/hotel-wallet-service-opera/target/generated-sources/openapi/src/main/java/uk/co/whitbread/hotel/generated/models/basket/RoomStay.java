package uk.co.whitbread.hotel.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.basket.RatePerNight;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * RoomStay
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:22.312200+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomStay {

  private @Nullable Integer adultsNumber;

  private @Nullable String arrivalDate;

  private @Nullable Integer childrenNumber;

  private @Nullable String departureDate;

  private @Nullable String ratePlanCode;

  @Valid
  private List<@Valid RatePerNight> ratesPerNight = new ArrayList<>();

  private @Nullable String roomType;

  public RoomStay adultsNumber(Integer adultsNumber) {
    this.adultsNumber = adultsNumber;
    return this;
  }

  /**
   * Get adultsNumber
   * @return adultsNumber
   */
  
  @Schema(name = "adultsNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adultsNumber")
  public Integer getAdultsNumber() {
    return adultsNumber;
  }

  public void setAdultsNumber(Integer adultsNumber) {
    this.adultsNumber = adultsNumber;
  }

  public RoomStay arrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Get arrivalDate
   * @return arrivalDate
   */
  
  @Schema(name = "arrivalDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("arrivalDate")
  public String getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
  }

  public RoomStay childrenNumber(Integer childrenNumber) {
    this.childrenNumber = childrenNumber;
    return this;
  }

  /**
   * Get childrenNumber
   * @return childrenNumber
   */
  
  @Schema(name = "childrenNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("childrenNumber")
  public Integer getChildrenNumber() {
    return childrenNumber;
  }

  public void setChildrenNumber(Integer childrenNumber) {
    this.childrenNumber = childrenNumber;
  }

  public RoomStay departureDate(String departureDate) {
    this.departureDate = departureDate;
    return this;
  }

  /**
   * Get departureDate
   * @return departureDate
   */
  
  @Schema(name = "departureDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("departureDate")
  public String getDepartureDate() {
    return departureDate;
  }

  public void setDepartureDate(String departureDate) {
    this.departureDate = departureDate;
  }

  public RoomStay ratePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
    return this;
  }

  /**
   * Get ratePlanCode
   * @return ratePlanCode
   */
  
  @Schema(name = "ratePlanCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanCode")
  public String getRatePlanCode() {
    return ratePlanCode;
  }

  public void setRatePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
  }

  public RoomStay ratesPerNight(List<@Valid RatePerNight> ratesPerNight) {
    this.ratesPerNight = ratesPerNight;
    return this;
  }

  public RoomStay addRatesPerNightItem(RatePerNight ratesPerNightItem) {
    if (this.ratesPerNight == null) {
      this.ratesPerNight = new ArrayList<>();
    }
    this.ratesPerNight.add(ratesPerNightItem);
    return this;
  }

  /**
   * Get ratesPerNight
   * @return ratesPerNight
   */
  @Valid 
  @Schema(name = "ratesPerNight", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratesPerNight")
  public List<@Valid RatePerNight> getRatesPerNight() {
    return ratesPerNight;
  }

  public void setRatesPerNight(List<@Valid RatePerNight> ratesPerNight) {
    this.ratesPerNight = ratesPerNight;
  }

  public RoomStay roomType(String roomType) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomStay roomStay = (RoomStay) o;
    return Objects.equals(this.adultsNumber, roomStay.adultsNumber) &&
        Objects.equals(this.arrivalDate, roomStay.arrivalDate) &&
        Objects.equals(this.childrenNumber, roomStay.childrenNumber) &&
        Objects.equals(this.departureDate, roomStay.departureDate) &&
        Objects.equals(this.ratePlanCode, roomStay.ratePlanCode) &&
        Objects.equals(this.ratesPerNight, roomStay.ratesPerNight) &&
        Objects.equals(this.roomType, roomStay.roomType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adultsNumber, arrivalDate, childrenNumber, departureDate, ratePlanCode, ratesPerNight, roomType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomStay {\n");
    sb.append("    adultsNumber: ").append(toIndentedString(adultsNumber)).append("\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    childrenNumber: ").append(toIndentedString(childrenNumber)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    ratesPerNight: ").append(toIndentedString(ratesPerNight)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
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


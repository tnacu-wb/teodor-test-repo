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
 * RoomStayDistributionDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomStayDistributionDto {

  private @Nullable Integer adultsNumber;

  private String arrivalDate;

  private @Nullable Integer childrenNumber;

  private @Nullable Boolean cot;

  private String departureDate;

  private @Nullable String operaRoomType;

  private @Nullable String ratePlanCode;

  private @Nullable String roomType;

  @Valid
  private List<String> specialRequests = new ArrayList<>();

  public RoomStayDistributionDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RoomStayDistributionDto(String arrivalDate, String departureDate) {
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
  }

  public RoomStayDistributionDto adultsNumber(Integer adultsNumber) {
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

  public RoomStayDistributionDto arrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Get arrivalDate
   * @return arrivalDate
   */
  @NotNull 
  @Schema(name = "arrivalDate", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("arrivalDate")
  public String getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
  }

  public RoomStayDistributionDto childrenNumber(Integer childrenNumber) {
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

  public RoomStayDistributionDto cot(Boolean cot) {
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

  public RoomStayDistributionDto departureDate(String departureDate) {
    this.departureDate = departureDate;
    return this;
  }

  /**
   * Get departureDate
   * @return departureDate
   */
  @NotNull 
  @Schema(name = "departureDate", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("departureDate")
  public String getDepartureDate() {
    return departureDate;
  }

  public void setDepartureDate(String departureDate) {
    this.departureDate = departureDate;
  }

  public RoomStayDistributionDto operaRoomType(String operaRoomType) {
    this.operaRoomType = operaRoomType;
    return this;
  }

  /**
   * Get operaRoomType
   * @return operaRoomType
   */
  
  @Schema(name = "operaRoomType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("operaRoomType")
  public String getOperaRoomType() {
    return operaRoomType;
  }

  public void setOperaRoomType(String operaRoomType) {
    this.operaRoomType = operaRoomType;
  }

  public RoomStayDistributionDto ratePlanCode(String ratePlanCode) {
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

  public RoomStayDistributionDto roomType(String roomType) {
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

  public RoomStayDistributionDto specialRequests(List<String> specialRequests) {
    this.specialRequests = specialRequests;
    return this;
  }

  public RoomStayDistributionDto addSpecialRequestsItem(String specialRequestsItem) {
    if (this.specialRequests == null) {
      this.specialRequests = new ArrayList<>();
    }
    this.specialRequests.add(specialRequestsItem);
    return this;
  }

  /**
   * Get specialRequests
   * @return specialRequests
   */
  
  @Schema(name = "specialRequests", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("specialRequests")
  public List<String> getSpecialRequests() {
    return specialRequests;
  }

  public void setSpecialRequests(List<String> specialRequests) {
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
    RoomStayDistributionDto roomStayDistributionDto = (RoomStayDistributionDto) o;
    return Objects.equals(this.adultsNumber, roomStayDistributionDto.adultsNumber) &&
        Objects.equals(this.arrivalDate, roomStayDistributionDto.arrivalDate) &&
        Objects.equals(this.childrenNumber, roomStayDistributionDto.childrenNumber) &&
        Objects.equals(this.cot, roomStayDistributionDto.cot) &&
        Objects.equals(this.departureDate, roomStayDistributionDto.departureDate) &&
        Objects.equals(this.operaRoomType, roomStayDistributionDto.operaRoomType) &&
        Objects.equals(this.ratePlanCode, roomStayDistributionDto.ratePlanCode) &&
        Objects.equals(this.roomType, roomStayDistributionDto.roomType) &&
        Objects.equals(this.specialRequests, roomStayDistributionDto.specialRequests);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adultsNumber, arrivalDate, childrenNumber, cot, departureDate, operaRoomType, ratePlanCode, roomType, specialRequests);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomStayDistributionDto {\n");
    sb.append("    adultsNumber: ").append(toIndentedString(adultsNumber)).append("\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    childrenNumber: ").append(toIndentedString(childrenNumber)).append("\n");
    sb.append("    cot: ").append(toIndentedString(cot)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    operaRoomType: ").append(toIndentedString(operaRoomType)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
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


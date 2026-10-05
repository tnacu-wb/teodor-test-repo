package uk.co.whitbread.hotel.ohip.adapter.generated.models.locking;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * LockRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:31.981751+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class LockRequestDto {

  private String arrivalDate;

  private String departureDate;

  private String hotelId;

  /**
   * Gets or Sets operationType
   */
  public enum OperationTypeEnum {
    NEW("NEW"),
    
    AMEND("AMEND");

    private String value;

    OperationTypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static OperationTypeEnum fromValue(String value) {
      for (OperationTypeEnum b : OperationTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private OperationTypeEnum operationType;

  private String roomType;

  public LockRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public LockRequestDto(String arrivalDate, String departureDate, String hotelId, OperationTypeEnum operationType, String roomType) {
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
    this.hotelId = hotelId;
    this.operationType = operationType;
    this.roomType = roomType;
  }

  public LockRequestDto arrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Get arrivalDate
   * @return arrivalDate
   */
  @NotNull 
  @Schema(name = "arrivalDate", example = "Arrival date in format yyyy-mm-dd", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("arrivalDate")
  public String getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
  }

  public LockRequestDto departureDate(String departureDate) {
    this.departureDate = departureDate;
    return this;
  }

  /**
   * Get departureDate
   * @return departureDate
   */
  @NotNull 
  @Schema(name = "departureDate", example = "Departure date in format yyyy-mm-dd", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("departureDate")
  public String getDepartureDate() {
    return departureDate;
  }

  public void setDepartureDate(String departureDate) {
    this.departureDate = departureDate;
  }

  public LockRequestDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  @NotNull 
  @Schema(name = "hotelId", example = "TKINPT", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public LockRequestDto operationType(OperationTypeEnum operationType) {
    this.operationType = operationType;
    return this;
  }

  /**
   * Get operationType
   * @return operationType
   */
  @NotNull 
  @Schema(name = "operationType", example = "NEW or AMEND", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("operationType")
  public OperationTypeEnum getOperationType() {
    return operationType;
  }

  public void setOperationType(OperationTypeEnum operationType) {
    this.operationType = operationType;
  }

  public LockRequestDto roomType(String roomType) {
    this.roomType = roomType;
    return this;
  }

  /**
   * Get roomType
   * @return roomType
   */
  @NotNull 
  @Schema(name = "roomType", requiredMode = Schema.RequiredMode.REQUIRED)
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
    LockRequestDto lockRequestDto = (LockRequestDto) o;
    return Objects.equals(this.arrivalDate, lockRequestDto.arrivalDate) &&
        Objects.equals(this.departureDate, lockRequestDto.departureDate) &&
        Objects.equals(this.hotelId, lockRequestDto.hotelId) &&
        Objects.equals(this.operationType, lockRequestDto.operationType) &&
        Objects.equals(this.roomType, lockRequestDto.roomType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrivalDate, departureDate, hotelId, operationType, roomType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LockRequestDto {\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    operationType: ").append(toIndentedString(operationType)).append("\n");
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


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
 * LockResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:31.981751+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class LockResponseDto {

  private String hotelId;

  private Boolean isLocked;

  private @Nullable String lockExpiration;

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

  public LockResponseDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public LockResponseDto(String hotelId, Boolean isLocked, OperationTypeEnum operationType, String roomType) {
    this.hotelId = hotelId;
    this.isLocked = isLocked;
    this.operationType = operationType;
    this.roomType = roomType;
  }

  public LockResponseDto hotelId(String hotelId) {
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

  public LockResponseDto isLocked(Boolean isLocked) {
    this.isLocked = isLocked;
    return this;
  }

  /**
   * Get isLocked
   * @return isLocked
   */
  @NotNull 
  @Schema(name = "isLocked", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("isLocked")
  public Boolean getIsLocked() {
    return isLocked;
  }

  public void setIsLocked(Boolean isLocked) {
    this.isLocked = isLocked;
  }

  public LockResponseDto lockExpiration(String lockExpiration) {
    this.lockExpiration = lockExpiration;
    return this;
  }

  /**
   * Get lockExpiration
   * @return lockExpiration
   */
  
  @Schema(name = "lockExpiration", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lockExpiration")
  public String getLockExpiration() {
    return lockExpiration;
  }

  public void setLockExpiration(String lockExpiration) {
    this.lockExpiration = lockExpiration;
  }

  public LockResponseDto operationType(OperationTypeEnum operationType) {
    this.operationType = operationType;
    return this;
  }

  /**
   * Get operationType
   * @return operationType
   */
  @NotNull 
  @Schema(name = "operationType", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("operationType")
  public OperationTypeEnum getOperationType() {
    return operationType;
  }

  public void setOperationType(OperationTypeEnum operationType) {
    this.operationType = operationType;
  }

  public LockResponseDto roomType(String roomType) {
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
    LockResponseDto lockResponseDto = (LockResponseDto) o;
    return Objects.equals(this.hotelId, lockResponseDto.hotelId) &&
        Objects.equals(this.isLocked, lockResponseDto.isLocked) &&
        Objects.equals(this.lockExpiration, lockResponseDto.lockExpiration) &&
        Objects.equals(this.operationType, lockResponseDto.operationType) &&
        Objects.equals(this.roomType, lockResponseDto.roomType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelId, isLocked, lockExpiration, operationType, roomType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LockResponseDto {\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    isLocked: ").append(toIndentedString(isLocked)).append("\n");
    sb.append("    lockExpiration: ").append(toIndentedString(lockExpiration)).append("\n");
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


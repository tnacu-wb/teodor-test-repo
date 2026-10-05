package uk.co.whitbread.hotel.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * UdfsRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UdfsRequestDto {

  /**
   * Gets or Sets ciolStatus
   */
  public enum CiolStatusEnum {
    CIOL_STARTED("CIOL_STARTED"),
    
    WALLET_PASS("WALLET_PASS"),
    
    DK_ISSUED("DK_ISSUED"),
    
    CIOL_COMPLETED("CIOL_COMPLETED");

    private String value;

    CiolStatusEnum(String value) {
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
    public static CiolStatusEnum fromValue(String value) {
      for (CiolStatusEnum b : CiolStatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private CiolStatusEnum ciolStatus;

  private String hotelId;

  @Valid
  private Set<String> reservationIds = new LinkedHashSet<>();

  public UdfsRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UdfsRequestDto(CiolStatusEnum ciolStatus, String hotelId, Set<String> reservationIds) {
    this.ciolStatus = ciolStatus;
    this.hotelId = hotelId;
    this.reservationIds = reservationIds;
  }

  public UdfsRequestDto ciolStatus(CiolStatusEnum ciolStatus) {
    this.ciolStatus = ciolStatus;
    return this;
  }

  /**
   * Get ciolStatus
   * @return ciolStatus
   */
  @NotNull 
  @Schema(name = "ciolStatus", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ciolStatus")
  public CiolStatusEnum getCiolStatus() {
    return ciolStatus;
  }

  public void setCiolStatus(CiolStatusEnum ciolStatus) {
    this.ciolStatus = ciolStatus;
  }

  public UdfsRequestDto hotelId(String hotelId) {
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

  public UdfsRequestDto reservationIds(Set<String> reservationIds) {
    this.reservationIds = reservationIds;
    return this;
  }

  public UdfsRequestDto addReservationIdsItem(String reservationIdsItem) {
    if (this.reservationIds == null) {
      this.reservationIds = new LinkedHashSet<>();
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
  public Set<String> getReservationIds() {
    return reservationIds;
  }

  @JsonDeserialize(as = LinkedHashSet.class)
  public void setReservationIds(Set<String> reservationIds) {
    this.reservationIds = reservationIds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UdfsRequestDto udfsRequestDto = (UdfsRequestDto) o;
    return Objects.equals(this.ciolStatus, udfsRequestDto.ciolStatus) &&
        Objects.equals(this.hotelId, udfsRequestDto.hotelId) &&
        Objects.equals(this.reservationIds, udfsRequestDto.reservationIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ciolStatus, hotelId, reservationIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UdfsRequestDto {\n");
    sb.append("    ciolStatus: ").append(toIndentedString(ciolStatus)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    reservationIds: ").append(toIndentedString(reservationIds)).append("\n");
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


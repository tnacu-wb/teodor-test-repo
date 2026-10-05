package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UpdateReservationOverrideReasonsRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateReservationOverrideReasonsRequestDto {

  private String callerName;

  private String hotelId;

  private @Nullable String managerName;

  private String reasonCode;

  private String reasonName;

  @Valid
  private Set<String> reservationIds;

  public UpdateReservationOverrideReasonsRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdateReservationOverrideReasonsRequestDto(String callerName, String hotelId, String reasonCode, String reasonName, Set<String> reservationIds) {
    this.callerName = callerName;
    this.hotelId = hotelId;
    this.reasonCode = reasonCode;
    this.reasonName = reasonName;
    this.reservationIds = reservationIds;
  }

  public UpdateReservationOverrideReasonsRequestDto callerName(String callerName) {
    this.callerName = callerName;
    return this;
  }

  /**
   * Get callerName
   * @return callerName
   */
  @NotNull 
  @Schema(name = "callerName", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("callerName")
  public String getCallerName() {
    return callerName;
  }

  public void setCallerName(String callerName) {
    this.callerName = callerName;
  }

  public UpdateReservationOverrideReasonsRequestDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  @NotNull 
  @Schema(name = "hotelId", example = "LONEUS", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public UpdateReservationOverrideReasonsRequestDto managerName(String managerName) {
    this.managerName = managerName;
    return this;
  }

  /**
   * Get managerName
   * @return managerName
   */
  
  @Schema(name = "managerName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("managerName")
  public String getManagerName() {
    return managerName;
  }

  public void setManagerName(String managerName) {
    this.managerName = managerName;
  }

  public UpdateReservationOverrideReasonsRequestDto reasonCode(String reasonCode) {
    this.reasonCode = reasonCode;
    return this;
  }

  /**
   * Get reasonCode
   * @return reasonCode
   */
  @NotNull 
  @Schema(name = "reasonCode", example = "ILL", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reasonCode")
  public String getReasonCode() {
    return reasonCode;
  }

  public void setReasonCode(String reasonCode) {
    this.reasonCode = reasonCode;
  }

  public UpdateReservationOverrideReasonsRequestDto reasonName(String reasonName) {
    this.reasonName = reasonName;
    return this;
  }

  /**
   * Get reasonName
   * @return reasonName
   */
  @NotNull 
  @Schema(name = "reasonName", example = "Medical Appointments", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reasonName")
  public String getReasonName() {
    return reasonName;
  }

  public void setReasonName(String reasonName) {
    this.reasonName = reasonName;
  }

  public UpdateReservationOverrideReasonsRequestDto reservationIds(Set<String> reservationIds) {
    this.reservationIds = reservationIds;
    return this;
  }

  public UpdateReservationOverrideReasonsRequestDto addReservationIdsItem(String reservationIdsItem) {
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
  @NotNull @Size(min = 1, max = 2147483647) 
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
    UpdateReservationOverrideReasonsRequestDto updateReservationOverrideReasonsRequestDto = (UpdateReservationOverrideReasonsRequestDto) o;
    return Objects.equals(this.callerName, updateReservationOverrideReasonsRequestDto.callerName) &&
        Objects.equals(this.hotelId, updateReservationOverrideReasonsRequestDto.hotelId) &&
        Objects.equals(this.managerName, updateReservationOverrideReasonsRequestDto.managerName) &&
        Objects.equals(this.reasonCode, updateReservationOverrideReasonsRequestDto.reasonCode) &&
        Objects.equals(this.reasonName, updateReservationOverrideReasonsRequestDto.reasonName) &&
        Objects.equals(this.reservationIds, updateReservationOverrideReasonsRequestDto.reservationIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(callerName, hotelId, managerName, reasonCode, reasonName, reservationIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateReservationOverrideReasonsRequestDto {\n");
    sb.append("    callerName: ").append(toIndentedString(callerName)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    managerName: ").append(toIndentedString(managerName)).append("\n");
    sb.append("    reasonCode: ").append(toIndentedString(reasonCode)).append("\n");
    sb.append("    reasonName: ").append(toIndentedString(reasonName)).append("\n");
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


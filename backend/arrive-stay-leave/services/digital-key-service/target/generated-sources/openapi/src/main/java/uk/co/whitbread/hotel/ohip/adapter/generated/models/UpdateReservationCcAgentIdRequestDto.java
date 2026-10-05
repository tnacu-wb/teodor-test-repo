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
 * UpdateReservationCcAgentIdRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateReservationCcAgentIdRequestDto {

  private String ccAgentId;

  private @Nullable Boolean clearFirst;

  private String hotelId;

  @Valid
  private Set<String> reservationIds;

  public UpdateReservationCcAgentIdRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdateReservationCcAgentIdRequestDto(String ccAgentId, String hotelId, Set<String> reservationIds) {
    this.ccAgentId = ccAgentId;
    this.hotelId = hotelId;
    this.reservationIds = reservationIds;
  }

  public UpdateReservationCcAgentIdRequestDto ccAgentId(String ccAgentId) {
    this.ccAgentId = ccAgentId;
    return this;
  }

  /**
   * Get ccAgentId
   * @return ccAgentId
   */
  @NotNull 
  @Schema(name = "ccAgentId", example = "jane.doe@wb.com", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ccAgentId")
  public String getCcAgentId() {
    return ccAgentId;
  }

  public void setCcAgentId(String ccAgentId) {
    this.ccAgentId = ccAgentId;
  }

  public UpdateReservationCcAgentIdRequestDto clearFirst(Boolean clearFirst) {
    this.clearFirst = clearFirst;
    return this;
  }

  /**
   * Get clearFirst
   * @return clearFirst
   */
  
  @Schema(name = "clearFirst", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("clearFirst")
  public Boolean getClearFirst() {
    return clearFirst;
  }

  public void setClearFirst(Boolean clearFirst) {
    this.clearFirst = clearFirst;
  }

  public UpdateReservationCcAgentIdRequestDto hotelId(String hotelId) {
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

  public UpdateReservationCcAgentIdRequestDto reservationIds(Set<String> reservationIds) {
    this.reservationIds = reservationIds;
    return this;
  }

  public UpdateReservationCcAgentIdRequestDto addReservationIdsItem(String reservationIdsItem) {
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
    UpdateReservationCcAgentIdRequestDto updateReservationCcAgentIdRequestDto = (UpdateReservationCcAgentIdRequestDto) o;
    return Objects.equals(this.ccAgentId, updateReservationCcAgentIdRequestDto.ccAgentId) &&
        Objects.equals(this.clearFirst, updateReservationCcAgentIdRequestDto.clearFirst) &&
        Objects.equals(this.hotelId, updateReservationCcAgentIdRequestDto.hotelId) &&
        Objects.equals(this.reservationIds, updateReservationCcAgentIdRequestDto.reservationIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ccAgentId, clearFirst, hotelId, reservationIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateReservationCcAgentIdRequestDto {\n");
    sb.append("    ccAgentId: ").append(toIndentedString(ccAgentId)).append("\n");
    sb.append("    clearFirst: ").append(toIndentedString(clearFirst)).append("\n");
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


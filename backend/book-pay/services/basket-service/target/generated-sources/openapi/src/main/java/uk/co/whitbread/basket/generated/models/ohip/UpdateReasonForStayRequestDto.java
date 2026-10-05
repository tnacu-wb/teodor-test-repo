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
 * UpdateReasonForStayRequestDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateReasonForStayRequestDto {

  private String hotelId;

  private String reasonForStay;

  @Valid
  private List<String> reservationIds = new ArrayList<>();

  public UpdateReasonForStayRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdateReasonForStayRequestDto(String hotelId, String reasonForStay, List<String> reservationIds) {
    this.hotelId = hotelId;
    this.reasonForStay = reasonForStay;
    this.reservationIds = reservationIds;
  }

  public UpdateReasonForStayRequestDto hotelId(String hotelId) {
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

  public UpdateReasonForStayRequestDto reasonForStay(String reasonForStay) {
    this.reasonForStay = reasonForStay;
    return this;
  }

  /**
   * Get reasonForStay
   * @return reasonForStay
   */
  @NotNull 
  @Schema(name = "reasonForStay", example = "LEI", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reasonForStay")
  public String getReasonForStay() {
    return reasonForStay;
  }

  public void setReasonForStay(String reasonForStay) {
    this.reasonForStay = reasonForStay;
  }

  public UpdateReasonForStayRequestDto reservationIds(List<String> reservationIds) {
    this.reservationIds = reservationIds;
    return this;
  }

  public UpdateReasonForStayRequestDto addReservationIdsItem(String reservationIdsItem) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateReasonForStayRequestDto updateReasonForStayRequestDto = (UpdateReasonForStayRequestDto) o;
    return Objects.equals(this.hotelId, updateReasonForStayRequestDto.hotelId) &&
        Objects.equals(this.reasonForStay, updateReasonForStayRequestDto.reasonForStay) &&
        Objects.equals(this.reservationIds, updateReasonForStayRequestDto.reservationIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelId, reasonForStay, reservationIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateReasonForStayRequestDto {\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    reasonForStay: ").append(toIndentedString(reasonForStay)).append("\n");
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


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
 * UpdateCancellationPoliciesRequestDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateCancellationPoliciesRequestDto {

  private String absoluteDeadline;

  private String hotelId;

  @Valid
  private List<String> reservationIds = new ArrayList<>();

  public UpdateCancellationPoliciesRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdateCancellationPoliciesRequestDto(String absoluteDeadline, String hotelId, List<String> reservationIds) {
    this.absoluteDeadline = absoluteDeadline;
    this.hotelId = hotelId;
    this.reservationIds = reservationIds;
  }

  public UpdateCancellationPoliciesRequestDto absoluteDeadline(String absoluteDeadline) {
    this.absoluteDeadline = absoluteDeadline;
    return this;
  }

  /**
   * Get absoluteDeadline
   * @return absoluteDeadline
   */
  @NotNull 
  @Schema(name = "absoluteDeadline", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("absoluteDeadline")
  public String getAbsoluteDeadline() {
    return absoluteDeadline;
  }

  public void setAbsoluteDeadline(String absoluteDeadline) {
    this.absoluteDeadline = absoluteDeadline;
  }

  public UpdateCancellationPoliciesRequestDto hotelId(String hotelId) {
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

  public UpdateCancellationPoliciesRequestDto reservationIds(List<String> reservationIds) {
    this.reservationIds = reservationIds;
    return this;
  }

  public UpdateCancellationPoliciesRequestDto addReservationIdsItem(String reservationIdsItem) {
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
    UpdateCancellationPoliciesRequestDto updateCancellationPoliciesRequestDto = (UpdateCancellationPoliciesRequestDto) o;
    return Objects.equals(this.absoluteDeadline, updateCancellationPoliciesRequestDto.absoluteDeadline) &&
        Objects.equals(this.hotelId, updateCancellationPoliciesRequestDto.hotelId) &&
        Objects.equals(this.reservationIds, updateCancellationPoliciesRequestDto.reservationIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(absoluteDeadline, hotelId, reservationIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateCancellationPoliciesRequestDto {\n");
    sb.append("    absoluteDeadline: ").append(toIndentedString(absoluteDeadline)).append("\n");
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


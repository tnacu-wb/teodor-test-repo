package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UpdateCancellationPolicyRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateCancellationPolicyRequestDto {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime absoluteDeadline;

  private String hotelId;

  private String reservationId;

  public UpdateCancellationPolicyRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdateCancellationPolicyRequestDto(OffsetDateTime absoluteDeadline, String hotelId, String reservationId) {
    this.absoluteDeadline = absoluteDeadline;
    this.hotelId = hotelId;
    this.reservationId = reservationId;
  }

  public UpdateCancellationPolicyRequestDto absoluteDeadline(OffsetDateTime absoluteDeadline) {
    this.absoluteDeadline = absoluteDeadline;
    return this;
  }

  /**
   * Get absoluteDeadline
   * @return absoluteDeadline
   */
  @NotNull @Valid 
  @Schema(name = "absoluteDeadline", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("absoluteDeadline")
  public OffsetDateTime getAbsoluteDeadline() {
    return absoluteDeadline;
  }

  public void setAbsoluteDeadline(OffsetDateTime absoluteDeadline) {
    this.absoluteDeadline = absoluteDeadline;
  }

  public UpdateCancellationPolicyRequestDto hotelId(String hotelId) {
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

  public UpdateCancellationPolicyRequestDto reservationId(String reservationId) {
    this.reservationId = reservationId;
    return this;
  }

  /**
   * Get reservationId
   * @return reservationId
   */
  @NotNull 
  @Schema(name = "reservationId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reservationId")
  public String getReservationId() {
    return reservationId;
  }

  public void setReservationId(String reservationId) {
    this.reservationId = reservationId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateCancellationPolicyRequestDto updateCancellationPolicyRequestDto = (UpdateCancellationPolicyRequestDto) o;
    return Objects.equals(this.absoluteDeadline, updateCancellationPolicyRequestDto.absoluteDeadline) &&
        Objects.equals(this.hotelId, updateCancellationPolicyRequestDto.hotelId) &&
        Objects.equals(this.reservationId, updateCancellationPolicyRequestDto.reservationId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(absoluteDeadline, hotelId, reservationId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateCancellationPolicyRequestDto {\n");
    sb.append("    absoluteDeadline: ").append(toIndentedString(absoluteDeadline)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    reservationId: ").append(toIndentedString(reservationId)).append("\n");
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


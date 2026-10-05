package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.SearchBookingStayingGuestDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SearchBookingReservationDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class SearchBookingReservationDto {

  private @Nullable String cancellationId;

  private @Nullable String confirmationId;

  private @Nullable String reservationId;

  private @Nullable SearchBookingStayingGuestDto stayingGuest;

  public SearchBookingReservationDto cancellationId(String cancellationId) {
    this.cancellationId = cancellationId;
    return this;
  }

  /**
   * Get cancellationId
   * @return cancellationId
   */
  
  @Schema(name = "cancellationId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cancellationId")
  public String getCancellationId() {
    return cancellationId;
  }

  public void setCancellationId(String cancellationId) {
    this.cancellationId = cancellationId;
  }

  public SearchBookingReservationDto confirmationId(String confirmationId) {
    this.confirmationId = confirmationId;
    return this;
  }

  /**
   * Get confirmationId
   * @return confirmationId
   */
  
  @Schema(name = "confirmationId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("confirmationId")
  public String getConfirmationId() {
    return confirmationId;
  }

  public void setConfirmationId(String confirmationId) {
    this.confirmationId = confirmationId;
  }

  public SearchBookingReservationDto reservationId(String reservationId) {
    this.reservationId = reservationId;
    return this;
  }

  /**
   * Get reservationId
   * @return reservationId
   */
  
  @Schema(name = "reservationId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationId")
  public String getReservationId() {
    return reservationId;
  }

  public void setReservationId(String reservationId) {
    this.reservationId = reservationId;
  }

  public SearchBookingReservationDto stayingGuest(SearchBookingStayingGuestDto stayingGuest) {
    this.stayingGuest = stayingGuest;
    return this;
  }

  /**
   * Get stayingGuest
   * @return stayingGuest
   */
  @Valid 
  @Schema(name = "stayingGuest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("stayingGuest")
  public SearchBookingStayingGuestDto getStayingGuest() {
    return stayingGuest;
  }

  public void setStayingGuest(SearchBookingStayingGuestDto stayingGuest) {
    this.stayingGuest = stayingGuest;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SearchBookingReservationDto searchBookingReservationDto = (SearchBookingReservationDto) o;
    return Objects.equals(this.cancellationId, searchBookingReservationDto.cancellationId) &&
        Objects.equals(this.confirmationId, searchBookingReservationDto.confirmationId) &&
        Objects.equals(this.reservationId, searchBookingReservationDto.reservationId) &&
        Objects.equals(this.stayingGuest, searchBookingReservationDto.stayingGuest);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cancellationId, confirmationId, reservationId, stayingGuest);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SearchBookingReservationDto {\n");
    sb.append("    cancellationId: ").append(toIndentedString(cancellationId)).append("\n");
    sb.append("    confirmationId: ").append(toIndentedString(confirmationId)).append("\n");
    sb.append("    reservationId: ").append(toIndentedString(reservationId)).append("\n");
    sb.append("    stayingGuest: ").append(toIndentedString(stayingGuest)).append("\n");
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


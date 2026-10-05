package uk.co.whitbread.hotel.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.basket.StayingGuestDetailsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * StayingGuestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:22.312200+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class StayingGuestDto {

  private @Nullable String language;

  private @Nullable String reservationId;

  private Boolean sameAsBooker;

  private StayingGuestDetailsDto stayingGuestDetails;

  public StayingGuestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public StayingGuestDto(Boolean sameAsBooker, StayingGuestDetailsDto stayingGuestDetails) {
    this.sameAsBooker = sameAsBooker;
    this.stayingGuestDetails = stayingGuestDetails;
  }

  public StayingGuestDto language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
   */
  
  @Schema(name = "language", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("language")
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public StayingGuestDto reservationId(String reservationId) {
    this.reservationId = reservationId;
    return this;
  }

  /**
   * Get reservationId
   * @return reservationId
   */
  
  @Schema(name = "reservationId", example = "132484", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationId")
  public String getReservationId() {
    return reservationId;
  }

  public void setReservationId(String reservationId) {
    this.reservationId = reservationId;
  }

  public StayingGuestDto sameAsBooker(Boolean sameAsBooker) {
    this.sameAsBooker = sameAsBooker;
    return this;
  }

  /**
   * Get sameAsBooker
   * @return sameAsBooker
   */
  @NotNull 
  @Schema(name = "sameAsBooker", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("sameAsBooker")
  public Boolean getSameAsBooker() {
    return sameAsBooker;
  }

  public void setSameAsBooker(Boolean sameAsBooker) {
    this.sameAsBooker = sameAsBooker;
  }

  public StayingGuestDto stayingGuestDetails(StayingGuestDetailsDto stayingGuestDetails) {
    this.stayingGuestDetails = stayingGuestDetails;
    return this;
  }

  /**
   * Get stayingGuestDetails
   * @return stayingGuestDetails
   */
  @NotNull @Valid 
  @Schema(name = "stayingGuestDetails", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("stayingGuestDetails")
  public StayingGuestDetailsDto getStayingGuestDetails() {
    return stayingGuestDetails;
  }

  public void setStayingGuestDetails(StayingGuestDetailsDto stayingGuestDetails) {
    this.stayingGuestDetails = stayingGuestDetails;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StayingGuestDto stayingGuestDto = (StayingGuestDto) o;
    return Objects.equals(this.language, stayingGuestDto.language) &&
        Objects.equals(this.reservationId, stayingGuestDto.reservationId) &&
        Objects.equals(this.sameAsBooker, stayingGuestDto.sameAsBooker) &&
        Objects.equals(this.stayingGuestDetails, stayingGuestDto.stayingGuestDetails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(language, reservationId, sameAsBooker, stayingGuestDetails);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StayingGuestDto {\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    reservationId: ").append(toIndentedString(reservationId)).append("\n");
    sb.append("    sameAsBooker: ").append(toIndentedString(sameAsBooker)).append("\n");
    sb.append("    stayingGuestDetails: ").append(toIndentedString(stayingGuestDetails)).append("\n");
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


package uk.co.whitbread.hotel.ohip.adapter.generated.models;

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
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * SpecialRequests
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class SpecialRequests {

  @Valid
  private List<String> bookingNotes = new ArrayList<>();

  private String hotelId;

  @Valid
  private List<String> reservationIds = new ArrayList<>();

  @Valid
  private List<String> specialRequests = new ArrayList<>();

  public SpecialRequests() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SpecialRequests(String hotelId, List<String> reservationIds, List<String> specialRequests) {
    this.hotelId = hotelId;
    this.reservationIds = reservationIds;
    this.specialRequests = specialRequests;
  }

  public SpecialRequests bookingNotes(List<String> bookingNotes) {
    this.bookingNotes = bookingNotes;
    return this;
  }

  public SpecialRequests addBookingNotesItem(String bookingNotesItem) {
    if (this.bookingNotes == null) {
      this.bookingNotes = new ArrayList<>();
    }
    this.bookingNotes.add(bookingNotesItem);
    return this;
  }

  /**
   * Get bookingNotes
   * @return bookingNotes
   */
  
  @Schema(name = "bookingNotes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingNotes")
  public List<String> getBookingNotes() {
    return bookingNotes;
  }

  public void setBookingNotes(List<String> bookingNotes) {
    this.bookingNotes = bookingNotes;
  }

  public SpecialRequests hotelId(String hotelId) {
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

  public SpecialRequests reservationIds(List<String> reservationIds) {
    this.reservationIds = reservationIds;
    return this;
  }

  public SpecialRequests addReservationIdsItem(String reservationIdsItem) {
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

  public SpecialRequests specialRequests(List<String> specialRequests) {
    this.specialRequests = specialRequests;
    return this;
  }

  public SpecialRequests addSpecialRequestsItem(String specialRequestsItem) {
    if (this.specialRequests == null) {
      this.specialRequests = new ArrayList<>();
    }
    this.specialRequests.add(specialRequestsItem);
    return this;
  }

  /**
   * Get specialRequests
   * @return specialRequests
   */
  @NotNull 
  @Schema(name = "specialRequests", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("specialRequests")
  public List<String> getSpecialRequests() {
    return specialRequests;
  }

  public void setSpecialRequests(List<String> specialRequests) {
    this.specialRequests = specialRequests;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SpecialRequests specialRequests = (SpecialRequests) o;
    return Objects.equals(this.bookingNotes, specialRequests.bookingNotes) &&
        Objects.equals(this.hotelId, specialRequests.hotelId) &&
        Objects.equals(this.reservationIds, specialRequests.reservationIds) &&
        Objects.equals(this.specialRequests, specialRequests.specialRequests);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingNotes, hotelId, reservationIds, specialRequests);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SpecialRequests {\n");
    sb.append("    bookingNotes: ").append(toIndentedString(bookingNotes)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    reservationIds: ").append(toIndentedString(reservationIds)).append("\n");
    sb.append("    specialRequests: ").append(toIndentedString(specialRequests)).append("\n");
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


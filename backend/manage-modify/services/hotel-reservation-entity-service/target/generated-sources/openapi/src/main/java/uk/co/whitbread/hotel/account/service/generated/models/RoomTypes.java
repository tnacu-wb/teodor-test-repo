package uk.co.whitbread.hotel.account.service.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.account.service.generated.models.PersonDetails;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * RoomTypes
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:35.247020+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomTypes {

  private @Nullable String bookingStatus;

  private @Nullable Boolean carDataPresent;

  @Deprecated
  private @Nullable String leadGuest;

  private @Nullable PersonDetails personDetails;

  private @Nullable String roomType;

  public RoomTypes bookingStatus(String bookingStatus) {
    this.bookingStatus = bookingStatus;
    return this;
  }

  /**
   * Get bookingStatus
   * @return bookingStatus
   */
  
  @Schema(name = "bookingStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingStatus")
  public String getBookingStatus() {
    return bookingStatus;
  }

  public void setBookingStatus(String bookingStatus) {
    this.bookingStatus = bookingStatus;
  }

  public RoomTypes carDataPresent(Boolean carDataPresent) {
    this.carDataPresent = carDataPresent;
    return this;
  }

  /**
   * Get carDataPresent
   * @return carDataPresent
   */
  
  @Schema(name = "carDataPresent", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("carDataPresent")
  public Boolean getCarDataPresent() {
    return carDataPresent;
  }

  public void setCarDataPresent(Boolean carDataPresent) {
    this.carDataPresent = carDataPresent;
  }

  public RoomTypes leadGuest(String leadGuest) {
    this.leadGuest = leadGuest;
    return this;
  }

  /**
   * Get leadGuest
   * @return leadGuest
   * @deprecated
   */
  
  @Schema(name = "leadGuest", deprecated = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("leadGuest")
  @Deprecated
  public String getLeadGuest() {
    return leadGuest;
  }

  /**
   * @deprecated
   */
  @Deprecated
  public void setLeadGuest(String leadGuest) {
    this.leadGuest = leadGuest;
  }

  public RoomTypes personDetails(PersonDetails personDetails) {
    this.personDetails = personDetails;
    return this;
  }

  /**
   * Get personDetails
   * @return personDetails
   */
  @Valid 
  @Schema(name = "personDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("personDetails")
  public PersonDetails getPersonDetails() {
    return personDetails;
  }

  public void setPersonDetails(PersonDetails personDetails) {
    this.personDetails = personDetails;
  }

  public RoomTypes roomType(String roomType) {
    this.roomType = roomType;
    return this;
  }

  /**
   * Get roomType
   * @return roomType
   */
  
  @Schema(name = "roomType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    RoomTypes roomTypes = (RoomTypes) o;
    return Objects.equals(this.bookingStatus, roomTypes.bookingStatus) &&
        Objects.equals(this.carDataPresent, roomTypes.carDataPresent) &&
        Objects.equals(this.leadGuest, roomTypes.leadGuest) &&
        Objects.equals(this.personDetails, roomTypes.personDetails) &&
        Objects.equals(this.roomType, roomTypes.roomType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingStatus, carDataPresent, leadGuest, personDetails, roomType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomTypes {\n");
    sb.append("    bookingStatus: ").append(toIndentedString(bookingStatus)).append("\n");
    sb.append("    carDataPresent: ").append(toIndentedString(carDataPresent)).append("\n");
    sb.append("    leadGuest: ").append(toIndentedString(leadGuest)).append("\n");
    sb.append("    personDetails: ").append(toIndentedString(personDetails)).append("\n");
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


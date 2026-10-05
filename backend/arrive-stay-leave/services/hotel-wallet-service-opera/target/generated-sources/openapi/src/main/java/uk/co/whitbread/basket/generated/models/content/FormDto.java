package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * FormDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:24.587145+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class FormDto {

  private @Nullable String adultsHelperText;

  private @Nullable String arrivalDateLabel;

  private @Nullable String bookingInvalid;

  private @Nullable String bookingReferenceLabel;

  private @Nullable String bookingSurnameLabel;

  private @Nullable String checkout;

  private @Nullable String childrenHelperText;

  private @Nullable String cotLimit;

  private @Nullable String findBookingDescription;

  private @Nullable String findBookingTitle;

  private @Nullable String includeCot;

  private @Nullable String invalidDate;

  private @Nullable String invalidFutureDate;

  private @Nullable String invalidLocation;

  private @Nullable String invalidNights;

  private @Nullable String invalidPastDate;

  private @Nullable String invalidReference;

  private @Nullable String invalidRooms;

  private @Nullable String invalidSurname;

  private @Nullable String removeRoom;

  private @Nullable String roomType;

  private @Nullable String searchBookingError;

  private @Nullable String snowdropError;

  private @Nullable String snowdropErrorRetry;

  private @Nullable String where;

  public FormDto adultsHelperText(String adultsHelperText) {
    this.adultsHelperText = adultsHelperText;
    return this;
  }

  /**
   * Get adultsHelperText
   * @return adultsHelperText
   */
  
  @Schema(name = "adultsHelperText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adultsHelperText")
  public String getAdultsHelperText() {
    return adultsHelperText;
  }

  public void setAdultsHelperText(String adultsHelperText) {
    this.adultsHelperText = adultsHelperText;
  }

  public FormDto arrivalDateLabel(String arrivalDateLabel) {
    this.arrivalDateLabel = arrivalDateLabel;
    return this;
  }

  /**
   * Get arrivalDateLabel
   * @return arrivalDateLabel
   */
  
  @Schema(name = "arrivalDateLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("arrivalDateLabel")
  public String getArrivalDateLabel() {
    return arrivalDateLabel;
  }

  public void setArrivalDateLabel(String arrivalDateLabel) {
    this.arrivalDateLabel = arrivalDateLabel;
  }

  public FormDto bookingInvalid(String bookingInvalid) {
    this.bookingInvalid = bookingInvalid;
    return this;
  }

  /**
   * Get bookingInvalid
   * @return bookingInvalid
   */
  
  @Schema(name = "bookingInvalid", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingInvalid")
  public String getBookingInvalid() {
    return bookingInvalid;
  }

  public void setBookingInvalid(String bookingInvalid) {
    this.bookingInvalid = bookingInvalid;
  }

  public FormDto bookingReferenceLabel(String bookingReferenceLabel) {
    this.bookingReferenceLabel = bookingReferenceLabel;
    return this;
  }

  /**
   * Get bookingReferenceLabel
   * @return bookingReferenceLabel
   */
  
  @Schema(name = "bookingReferenceLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingReferenceLabel")
  public String getBookingReferenceLabel() {
    return bookingReferenceLabel;
  }

  public void setBookingReferenceLabel(String bookingReferenceLabel) {
    this.bookingReferenceLabel = bookingReferenceLabel;
  }

  public FormDto bookingSurnameLabel(String bookingSurnameLabel) {
    this.bookingSurnameLabel = bookingSurnameLabel;
    return this;
  }

  /**
   * Get bookingSurnameLabel
   * @return bookingSurnameLabel
   */
  
  @Schema(name = "bookingSurnameLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingSurnameLabel")
  public String getBookingSurnameLabel() {
    return bookingSurnameLabel;
  }

  public void setBookingSurnameLabel(String bookingSurnameLabel) {
    this.bookingSurnameLabel = bookingSurnameLabel;
  }

  public FormDto checkout(String checkout) {
    this.checkout = checkout;
    return this;
  }

  /**
   * Get checkout
   * @return checkout
   */
  
  @Schema(name = "checkout", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("checkout")
  public String getCheckout() {
    return checkout;
  }

  public void setCheckout(String checkout) {
    this.checkout = checkout;
  }

  public FormDto childrenHelperText(String childrenHelperText) {
    this.childrenHelperText = childrenHelperText;
    return this;
  }

  /**
   * Get childrenHelperText
   * @return childrenHelperText
   */
  
  @Schema(name = "childrenHelperText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("childrenHelperText")
  public String getChildrenHelperText() {
    return childrenHelperText;
  }

  public void setChildrenHelperText(String childrenHelperText) {
    this.childrenHelperText = childrenHelperText;
  }

  public FormDto cotLimit(String cotLimit) {
    this.cotLimit = cotLimit;
    return this;
  }

  /**
   * Get cotLimit
   * @return cotLimit
   */
  
  @Schema(name = "cotLimit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cotLimit")
  public String getCotLimit() {
    return cotLimit;
  }

  public void setCotLimit(String cotLimit) {
    this.cotLimit = cotLimit;
  }

  public FormDto findBookingDescription(String findBookingDescription) {
    this.findBookingDescription = findBookingDescription;
    return this;
  }

  /**
   * Get findBookingDescription
   * @return findBookingDescription
   */
  
  @Schema(name = "findBookingDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("findBookingDescription")
  public String getFindBookingDescription() {
    return findBookingDescription;
  }

  public void setFindBookingDescription(String findBookingDescription) {
    this.findBookingDescription = findBookingDescription;
  }

  public FormDto findBookingTitle(String findBookingTitle) {
    this.findBookingTitle = findBookingTitle;
    return this;
  }

  /**
   * Get findBookingTitle
   * @return findBookingTitle
   */
  
  @Schema(name = "findBookingTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("findBookingTitle")
  public String getFindBookingTitle() {
    return findBookingTitle;
  }

  public void setFindBookingTitle(String findBookingTitle) {
    this.findBookingTitle = findBookingTitle;
  }

  public FormDto includeCot(String includeCot) {
    this.includeCot = includeCot;
    return this;
  }

  /**
   * Get includeCot
   * @return includeCot
   */
  
  @Schema(name = "includeCot", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("includeCot")
  public String getIncludeCot() {
    return includeCot;
  }

  public void setIncludeCot(String includeCot) {
    this.includeCot = includeCot;
  }

  public FormDto invalidDate(String invalidDate) {
    this.invalidDate = invalidDate;
    return this;
  }

  /**
   * Get invalidDate
   * @return invalidDate
   */
  
  @Schema(name = "invalidDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("invalidDate")
  public String getInvalidDate() {
    return invalidDate;
  }

  public void setInvalidDate(String invalidDate) {
    this.invalidDate = invalidDate;
  }

  public FormDto invalidFutureDate(String invalidFutureDate) {
    this.invalidFutureDate = invalidFutureDate;
    return this;
  }

  /**
   * Get invalidFutureDate
   * @return invalidFutureDate
   */
  
  @Schema(name = "invalidFutureDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("invalidFutureDate")
  public String getInvalidFutureDate() {
    return invalidFutureDate;
  }

  public void setInvalidFutureDate(String invalidFutureDate) {
    this.invalidFutureDate = invalidFutureDate;
  }

  public FormDto invalidLocation(String invalidLocation) {
    this.invalidLocation = invalidLocation;
    return this;
  }

  /**
   * Get invalidLocation
   * @return invalidLocation
   */
  
  @Schema(name = "invalidLocation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("invalidLocation")
  public String getInvalidLocation() {
    return invalidLocation;
  }

  public void setInvalidLocation(String invalidLocation) {
    this.invalidLocation = invalidLocation;
  }

  public FormDto invalidNights(String invalidNights) {
    this.invalidNights = invalidNights;
    return this;
  }

  /**
   * Get invalidNights
   * @return invalidNights
   */
  
  @Schema(name = "invalidNights", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("invalidNights")
  public String getInvalidNights() {
    return invalidNights;
  }

  public void setInvalidNights(String invalidNights) {
    this.invalidNights = invalidNights;
  }

  public FormDto invalidPastDate(String invalidPastDate) {
    this.invalidPastDate = invalidPastDate;
    return this;
  }

  /**
   * Get invalidPastDate
   * @return invalidPastDate
   */
  
  @Schema(name = "invalidPastDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("invalidPastDate")
  public String getInvalidPastDate() {
    return invalidPastDate;
  }

  public void setInvalidPastDate(String invalidPastDate) {
    this.invalidPastDate = invalidPastDate;
  }

  public FormDto invalidReference(String invalidReference) {
    this.invalidReference = invalidReference;
    return this;
  }

  /**
   * Get invalidReference
   * @return invalidReference
   */
  
  @Schema(name = "invalidReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("invalidReference")
  public String getInvalidReference() {
    return invalidReference;
  }

  public void setInvalidReference(String invalidReference) {
    this.invalidReference = invalidReference;
  }

  public FormDto invalidRooms(String invalidRooms) {
    this.invalidRooms = invalidRooms;
    return this;
  }

  /**
   * Get invalidRooms
   * @return invalidRooms
   */
  
  @Schema(name = "invalidRooms", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("invalidRooms")
  public String getInvalidRooms() {
    return invalidRooms;
  }

  public void setInvalidRooms(String invalidRooms) {
    this.invalidRooms = invalidRooms;
  }

  public FormDto invalidSurname(String invalidSurname) {
    this.invalidSurname = invalidSurname;
    return this;
  }

  /**
   * Get invalidSurname
   * @return invalidSurname
   */
  
  @Schema(name = "invalidSurname", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("invalidSurname")
  public String getInvalidSurname() {
    return invalidSurname;
  }

  public void setInvalidSurname(String invalidSurname) {
    this.invalidSurname = invalidSurname;
  }

  public FormDto removeRoom(String removeRoom) {
    this.removeRoom = removeRoom;
    return this;
  }

  /**
   * Get removeRoom
   * @return removeRoom
   */
  
  @Schema(name = "removeRoom", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("removeRoom")
  public String getRemoveRoom() {
    return removeRoom;
  }

  public void setRemoveRoom(String removeRoom) {
    this.removeRoom = removeRoom;
  }

  public FormDto roomType(String roomType) {
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

  public FormDto searchBookingError(String searchBookingError) {
    this.searchBookingError = searchBookingError;
    return this;
  }

  /**
   * Get searchBookingError
   * @return searchBookingError
   */
  
  @Schema(name = "searchBookingError", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("searchBookingError")
  public String getSearchBookingError() {
    return searchBookingError;
  }

  public void setSearchBookingError(String searchBookingError) {
    this.searchBookingError = searchBookingError;
  }

  public FormDto snowdropError(String snowdropError) {
    this.snowdropError = snowdropError;
    return this;
  }

  /**
   * Get snowdropError
   * @return snowdropError
   */
  
  @Schema(name = "snowdropError", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("snowdropError")
  public String getSnowdropError() {
    return snowdropError;
  }

  public void setSnowdropError(String snowdropError) {
    this.snowdropError = snowdropError;
  }

  public FormDto snowdropErrorRetry(String snowdropErrorRetry) {
    this.snowdropErrorRetry = snowdropErrorRetry;
    return this;
  }

  /**
   * Get snowdropErrorRetry
   * @return snowdropErrorRetry
   */
  
  @Schema(name = "snowdropErrorRetry", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("snowdropErrorRetry")
  public String getSnowdropErrorRetry() {
    return snowdropErrorRetry;
  }

  public void setSnowdropErrorRetry(String snowdropErrorRetry) {
    this.snowdropErrorRetry = snowdropErrorRetry;
  }

  public FormDto where(String where) {
    this.where = where;
    return this;
  }

  /**
   * Get where
   * @return where
   */
  
  @Schema(name = "where", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("where")
  public String getWhere() {
    return where;
  }

  public void setWhere(String where) {
    this.where = where;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FormDto formDto = (FormDto) o;
    return Objects.equals(this.adultsHelperText, formDto.adultsHelperText) &&
        Objects.equals(this.arrivalDateLabel, formDto.arrivalDateLabel) &&
        Objects.equals(this.bookingInvalid, formDto.bookingInvalid) &&
        Objects.equals(this.bookingReferenceLabel, formDto.bookingReferenceLabel) &&
        Objects.equals(this.bookingSurnameLabel, formDto.bookingSurnameLabel) &&
        Objects.equals(this.checkout, formDto.checkout) &&
        Objects.equals(this.childrenHelperText, formDto.childrenHelperText) &&
        Objects.equals(this.cotLimit, formDto.cotLimit) &&
        Objects.equals(this.findBookingDescription, formDto.findBookingDescription) &&
        Objects.equals(this.findBookingTitle, formDto.findBookingTitle) &&
        Objects.equals(this.includeCot, formDto.includeCot) &&
        Objects.equals(this.invalidDate, formDto.invalidDate) &&
        Objects.equals(this.invalidFutureDate, formDto.invalidFutureDate) &&
        Objects.equals(this.invalidLocation, formDto.invalidLocation) &&
        Objects.equals(this.invalidNights, formDto.invalidNights) &&
        Objects.equals(this.invalidPastDate, formDto.invalidPastDate) &&
        Objects.equals(this.invalidReference, formDto.invalidReference) &&
        Objects.equals(this.invalidRooms, formDto.invalidRooms) &&
        Objects.equals(this.invalidSurname, formDto.invalidSurname) &&
        Objects.equals(this.removeRoom, formDto.removeRoom) &&
        Objects.equals(this.roomType, formDto.roomType) &&
        Objects.equals(this.searchBookingError, formDto.searchBookingError) &&
        Objects.equals(this.snowdropError, formDto.snowdropError) &&
        Objects.equals(this.snowdropErrorRetry, formDto.snowdropErrorRetry) &&
        Objects.equals(this.where, formDto.where);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adultsHelperText, arrivalDateLabel, bookingInvalid, bookingReferenceLabel, bookingSurnameLabel, checkout, childrenHelperText, cotLimit, findBookingDescription, findBookingTitle, includeCot, invalidDate, invalidFutureDate, invalidLocation, invalidNights, invalidPastDate, invalidReference, invalidRooms, invalidSurname, removeRoom, roomType, searchBookingError, snowdropError, snowdropErrorRetry, where);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FormDto {\n");
    sb.append("    adultsHelperText: ").append(toIndentedString(adultsHelperText)).append("\n");
    sb.append("    arrivalDateLabel: ").append(toIndentedString(arrivalDateLabel)).append("\n");
    sb.append("    bookingInvalid: ").append(toIndentedString(bookingInvalid)).append("\n");
    sb.append("    bookingReferenceLabel: ").append(toIndentedString(bookingReferenceLabel)).append("\n");
    sb.append("    bookingSurnameLabel: ").append(toIndentedString(bookingSurnameLabel)).append("\n");
    sb.append("    checkout: ").append(toIndentedString(checkout)).append("\n");
    sb.append("    childrenHelperText: ").append(toIndentedString(childrenHelperText)).append("\n");
    sb.append("    cotLimit: ").append(toIndentedString(cotLimit)).append("\n");
    sb.append("    findBookingDescription: ").append(toIndentedString(findBookingDescription)).append("\n");
    sb.append("    findBookingTitle: ").append(toIndentedString(findBookingTitle)).append("\n");
    sb.append("    includeCot: ").append(toIndentedString(includeCot)).append("\n");
    sb.append("    invalidDate: ").append(toIndentedString(invalidDate)).append("\n");
    sb.append("    invalidFutureDate: ").append(toIndentedString(invalidFutureDate)).append("\n");
    sb.append("    invalidLocation: ").append(toIndentedString(invalidLocation)).append("\n");
    sb.append("    invalidNights: ").append(toIndentedString(invalidNights)).append("\n");
    sb.append("    invalidPastDate: ").append(toIndentedString(invalidPastDate)).append("\n");
    sb.append("    invalidReference: ").append(toIndentedString(invalidReference)).append("\n");
    sb.append("    invalidRooms: ").append(toIndentedString(invalidRooms)).append("\n");
    sb.append("    invalidSurname: ").append(toIndentedString(invalidSurname)).append("\n");
    sb.append("    removeRoom: ").append(toIndentedString(removeRoom)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
    sb.append("    searchBookingError: ").append(toIndentedString(searchBookingError)).append("\n");
    sb.append("    snowdropError: ").append(toIndentedString(snowdropError)).append("\n");
    sb.append("    snowdropErrorRetry: ").append(toIndentedString(snowdropErrorRetry)).append("\n");
    sb.append("    where: ").append(toIndentedString(where)).append("\n");
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


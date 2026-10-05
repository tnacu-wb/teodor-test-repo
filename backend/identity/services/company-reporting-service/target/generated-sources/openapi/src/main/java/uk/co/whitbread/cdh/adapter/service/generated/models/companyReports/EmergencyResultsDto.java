package uk.co.whitbread.cdh.adapter.service.generated.models.companyReports;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.cdh.adapter.service.generated.models.companyReports.EmergencyReportBookerDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.companyReports.GuestsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * EmergencyResultsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:17.912171+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class EmergencyResultsDto {

  private @Nullable String arrivalDate;

  private @Nullable EmergencyReportBookerDto booker;

  private @Nullable String bookingReference;

  private @Nullable String departureDate;

  @Valid
  private List<@Valid GuestsDto> guests = new ArrayList<>();

  private @Nullable String hotelArea;

  private @Nullable String hotelName;

  private @Nullable String hotelPhoneNumber;

  private @Nullable String hotelPostcode;

  private @Nullable String noOfAdults;

  private @Nullable String noOfChildren;

  private @Nullable String status;

  public EmergencyResultsDto arrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Get arrivalDate
   * @return arrivalDate
   */
  
  @Schema(name = "arrivalDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("arrivalDate")
  public String getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
  }

  public EmergencyResultsDto booker(EmergencyReportBookerDto booker) {
    this.booker = booker;
    return this;
  }

  /**
   * Get booker
   * @return booker
   */
  @Valid 
  @Schema(name = "booker", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("booker")
  public EmergencyReportBookerDto getBooker() {
    return booker;
  }

  public void setBooker(EmergencyReportBookerDto booker) {
    this.booker = booker;
  }

  public EmergencyResultsDto bookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
    return this;
  }

  /**
   * Get bookingReference
   * @return bookingReference
   */
  
  @Schema(name = "bookingReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingReference")
  public String getBookingReference() {
    return bookingReference;
  }

  public void setBookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
  }

  public EmergencyResultsDto departureDate(String departureDate) {
    this.departureDate = departureDate;
    return this;
  }

  /**
   * Get departureDate
   * @return departureDate
   */
  
  @Schema(name = "departureDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("departureDate")
  public String getDepartureDate() {
    return departureDate;
  }

  public void setDepartureDate(String departureDate) {
    this.departureDate = departureDate;
  }

  public EmergencyResultsDto guests(List<@Valid GuestsDto> guests) {
    this.guests = guests;
    return this;
  }

  public EmergencyResultsDto addGuestsItem(GuestsDto guestsItem) {
    if (this.guests == null) {
      this.guests = new ArrayList<>();
    }
    this.guests.add(guestsItem);
    return this;
  }

  /**
   * Get guests
   * @return guests
   */
  @Valid 
  @Schema(name = "guests", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("guests")
  public List<@Valid GuestsDto> getGuests() {
    return guests;
  }

  public void setGuests(List<@Valid GuestsDto> guests) {
    this.guests = guests;
  }

  public EmergencyResultsDto hotelArea(String hotelArea) {
    this.hotelArea = hotelArea;
    return this;
  }

  /**
   * Get hotelArea
   * @return hotelArea
   */
  
  @Schema(name = "hotelArea", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelArea")
  public String getHotelArea() {
    return hotelArea;
  }

  public void setHotelArea(String hotelArea) {
    this.hotelArea = hotelArea;
  }

  public EmergencyResultsDto hotelName(String hotelName) {
    this.hotelName = hotelName;
    return this;
  }

  /**
   * Get hotelName
   * @return hotelName
   */
  
  @Schema(name = "hotelName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelName")
  public String getHotelName() {
    return hotelName;
  }

  public void setHotelName(String hotelName) {
    this.hotelName = hotelName;
  }

  public EmergencyResultsDto hotelPhoneNumber(String hotelPhoneNumber) {
    this.hotelPhoneNumber = hotelPhoneNumber;
    return this;
  }

  /**
   * Get hotelPhoneNumber
   * @return hotelPhoneNumber
   */
  
  @Schema(name = "hotelPhoneNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelPhoneNumber")
  public String getHotelPhoneNumber() {
    return hotelPhoneNumber;
  }

  public void setHotelPhoneNumber(String hotelPhoneNumber) {
    this.hotelPhoneNumber = hotelPhoneNumber;
  }

  public EmergencyResultsDto hotelPostcode(String hotelPostcode) {
    this.hotelPostcode = hotelPostcode;
    return this;
  }

  /**
   * Get hotelPostcode
   * @return hotelPostcode
   */
  
  @Schema(name = "hotelPostcode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelPostcode")
  public String getHotelPostcode() {
    return hotelPostcode;
  }

  public void setHotelPostcode(String hotelPostcode) {
    this.hotelPostcode = hotelPostcode;
  }

  public EmergencyResultsDto noOfAdults(String noOfAdults) {
    this.noOfAdults = noOfAdults;
    return this;
  }

  /**
   * Get noOfAdults
   * @return noOfAdults
   */
  
  @Schema(name = "noOfAdults", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("noOfAdults")
  public String getNoOfAdults() {
    return noOfAdults;
  }

  public void setNoOfAdults(String noOfAdults) {
    this.noOfAdults = noOfAdults;
  }

  public EmergencyResultsDto noOfChildren(String noOfChildren) {
    this.noOfChildren = noOfChildren;
    return this;
  }

  /**
   * Get noOfChildren
   * @return noOfChildren
   */
  
  @Schema(name = "noOfChildren", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("noOfChildren")
  public String getNoOfChildren() {
    return noOfChildren;
  }

  public void setNoOfChildren(String noOfChildren) {
    this.noOfChildren = noOfChildren;
  }

  public EmergencyResultsDto status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EmergencyResultsDto emergencyResultsDto = (EmergencyResultsDto) o;
    return Objects.equals(this.arrivalDate, emergencyResultsDto.arrivalDate) &&
        Objects.equals(this.booker, emergencyResultsDto.booker) &&
        Objects.equals(this.bookingReference, emergencyResultsDto.bookingReference) &&
        Objects.equals(this.departureDate, emergencyResultsDto.departureDate) &&
        Objects.equals(this.guests, emergencyResultsDto.guests) &&
        Objects.equals(this.hotelArea, emergencyResultsDto.hotelArea) &&
        Objects.equals(this.hotelName, emergencyResultsDto.hotelName) &&
        Objects.equals(this.hotelPhoneNumber, emergencyResultsDto.hotelPhoneNumber) &&
        Objects.equals(this.hotelPostcode, emergencyResultsDto.hotelPostcode) &&
        Objects.equals(this.noOfAdults, emergencyResultsDto.noOfAdults) &&
        Objects.equals(this.noOfChildren, emergencyResultsDto.noOfChildren) &&
        Objects.equals(this.status, emergencyResultsDto.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrivalDate, booker, bookingReference, departureDate, guests, hotelArea, hotelName, hotelPhoneNumber, hotelPostcode, noOfAdults, noOfChildren, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EmergencyResultsDto {\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    booker: ").append(toIndentedString(booker)).append("\n");
    sb.append("    bookingReference: ").append(toIndentedString(bookingReference)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    guests: ").append(toIndentedString(guests)).append("\n");
    sb.append("    hotelArea: ").append(toIndentedString(hotelArea)).append("\n");
    sb.append("    hotelName: ").append(toIndentedString(hotelName)).append("\n");
    sb.append("    hotelPhoneNumber: ").append(toIndentedString(hotelPhoneNumber)).append("\n");
    sb.append("    hotelPostcode: ").append(toIndentedString(hotelPostcode)).append("\n");
    sb.append("    noOfAdults: ").append(toIndentedString(noOfAdults)).append("\n");
    sb.append("    noOfChildren: ").append(toIndentedString(noOfChildren)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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


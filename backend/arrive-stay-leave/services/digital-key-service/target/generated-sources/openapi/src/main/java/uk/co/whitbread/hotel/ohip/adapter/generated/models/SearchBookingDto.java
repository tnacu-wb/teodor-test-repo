package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.SearchBookingBookerDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.SearchBookingReservationDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SearchBookingDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class SearchBookingDto {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate arrivalDate;

  private @Nullable SearchBookingBookerDto booker;

  private @Nullable String bookingReference;

  private @Nullable String currencyCode;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate departureDate;

  private @Nullable String hotelId;

  private @Nullable String hotelName;

  @Valid
  private @Nullable List<@Valid SearchBookingReservationDto> reservations;

  private @Nullable String status;

  private @Nullable BigDecimal totalCost;

  public SearchBookingDto arrivalDate(LocalDate arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Get arrivalDate
   * @return arrivalDate
   */
  @Valid 
  @Schema(name = "arrivalDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("arrivalDate")
  public LocalDate getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(LocalDate arrivalDate) {
    this.arrivalDate = arrivalDate;
  }

  public SearchBookingDto booker(SearchBookingBookerDto booker) {
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
  public SearchBookingBookerDto getBooker() {
    return booker;
  }

  public void setBooker(SearchBookingBookerDto booker) {
    this.booker = booker;
  }

  public SearchBookingDto bookingReference(String bookingReference) {
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

  public SearchBookingDto currencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
    return this;
  }

  /**
   * Get currencyCode
   * @return currencyCode
   */
  
  @Schema(name = "currencyCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currencyCode")
  public String getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
  }

  public SearchBookingDto departureDate(LocalDate departureDate) {
    this.departureDate = departureDate;
    return this;
  }

  /**
   * Get departureDate
   * @return departureDate
   */
  @Valid 
  @Schema(name = "departureDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("departureDate")
  public LocalDate getDepartureDate() {
    return departureDate;
  }

  public void setDepartureDate(LocalDate departureDate) {
    this.departureDate = departureDate;
  }

  public SearchBookingDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public SearchBookingDto hotelName(String hotelName) {
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

  public SearchBookingDto reservations(List<@Valid SearchBookingReservationDto> reservations) {
    this.reservations = reservations;
    return this;
  }

  public SearchBookingDto addReservationsItem(SearchBookingReservationDto reservationsItem) {
    if (this.reservations == null) {
      this.reservations = new ArrayList<>();
    }
    this.reservations.add(reservationsItem);
    return this;
  }

  /**
   * Get reservations
   * @return reservations
   */
  @Valid 
  @Schema(name = "reservations", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservations")
  public List<@Valid SearchBookingReservationDto> getReservations() {
    return reservations;
  }

  public void setReservations(List<@Valid SearchBookingReservationDto> reservations) {
    this.reservations = reservations;
  }

  public SearchBookingDto status(String status) {
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

  public SearchBookingDto totalCost(BigDecimal totalCost) {
    this.totalCost = totalCost;
    return this;
  }

  /**
   * Get totalCost
   * @return totalCost
   */
  @Valid 
  @Schema(name = "totalCost", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalCost")
  public BigDecimal getTotalCost() {
    return totalCost;
  }

  public void setTotalCost(BigDecimal totalCost) {
    this.totalCost = totalCost;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SearchBookingDto searchBookingDto = (SearchBookingDto) o;
    return Objects.equals(this.arrivalDate, searchBookingDto.arrivalDate) &&
        Objects.equals(this.booker, searchBookingDto.booker) &&
        Objects.equals(this.bookingReference, searchBookingDto.bookingReference) &&
        Objects.equals(this.currencyCode, searchBookingDto.currencyCode) &&
        Objects.equals(this.departureDate, searchBookingDto.departureDate) &&
        Objects.equals(this.hotelId, searchBookingDto.hotelId) &&
        Objects.equals(this.hotelName, searchBookingDto.hotelName) &&
        Objects.equals(this.reservations, searchBookingDto.reservations) &&
        Objects.equals(this.status, searchBookingDto.status) &&
        Objects.equals(this.totalCost, searchBookingDto.totalCost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrivalDate, booker, bookingReference, currencyCode, departureDate, hotelId, hotelName, reservations, status, totalCost);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SearchBookingDto {\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    booker: ").append(toIndentedString(booker)).append("\n");
    sb.append("    bookingReference: ").append(toIndentedString(bookingReference)).append("\n");
    sb.append("    currencyCode: ").append(toIndentedString(currencyCode)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    hotelName: ").append(toIndentedString(hotelName)).append("\n");
    sb.append("    reservations: ").append(toIndentedString(reservations)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    totalCost: ").append(toIndentedString(totalCost)).append("\n");
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


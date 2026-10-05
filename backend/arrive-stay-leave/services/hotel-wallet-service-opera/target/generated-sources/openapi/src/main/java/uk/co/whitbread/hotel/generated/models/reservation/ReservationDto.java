package uk.co.whitbread.hotel.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.reservation.ReservationPackageDto;
import uk.co.whitbread.hotel.generated.models.reservation.RoomRateDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ReservationDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationDto {

  private Integer adultsNumber;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate arrival;

  private @Nullable String basketReferenceId;

  private @Nullable String bookingNotes;

  private @Nullable Integer childrenNumber;

  private @Nullable Boolean cotRequired;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate departure;

  private @Nullable String distributionIATANumber;

  private @Nullable String distributionUsername;

  private @Nullable String gdsReferenceNumber;

  private String hotelId;

  @Valid
  private List<@Valid ReservationPackageDto> reservationPackages = new ArrayList<>();

  private RoomRateDto roomRates;

  public ReservationDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ReservationDto(Integer adultsNumber, LocalDate arrival, LocalDate departure, String hotelId, RoomRateDto roomRates) {
    this.adultsNumber = adultsNumber;
    this.arrival = arrival;
    this.departure = departure;
    this.hotelId = hotelId;
    this.roomRates = roomRates;
  }

  public ReservationDto adultsNumber(Integer adultsNumber) {
    this.adultsNumber = adultsNumber;
    return this;
  }

  /**
   * Get adultsNumber
   * minimum: 1
   * maximum: 2
   * @return adultsNumber
   */
  @NotNull @Min(1) @Max(2) 
  @Schema(name = "adultsNumber", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("adultsNumber")
  public Integer getAdultsNumber() {
    return adultsNumber;
  }

  public void setAdultsNumber(Integer adultsNumber) {
    this.adultsNumber = adultsNumber;
  }

  public ReservationDto arrival(LocalDate arrival) {
    this.arrival = arrival;
    return this;
  }

  /**
   * Get arrival
   * @return arrival
   */
  @NotNull @Valid 
  @Schema(name = "arrival", example = "2015-10-20", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("arrival")
  public LocalDate getArrival() {
    return arrival;
  }

  public void setArrival(LocalDate arrival) {
    this.arrival = arrival;
  }

  public ReservationDto basketReferenceId(String basketReferenceId) {
    this.basketReferenceId = basketReferenceId;
    return this;
  }

  /**
   * Get basketReferenceId
   * @return basketReferenceId
   */
  
  @Schema(name = "basketReferenceId", example = "132484", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("basketReferenceId")
  public String getBasketReferenceId() {
    return basketReferenceId;
  }

  public void setBasketReferenceId(String basketReferenceId) {
    this.basketReferenceId = basketReferenceId;
  }

  public ReservationDto bookingNotes(String bookingNotes) {
    this.bookingNotes = bookingNotes;
    return this;
  }

  /**
   * Get bookingNotes
   * @return bookingNotes
   */
  
  @Schema(name = "bookingNotes", example = "bookingNotes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingNotes")
  public String getBookingNotes() {
    return bookingNotes;
  }

  public void setBookingNotes(String bookingNotes) {
    this.bookingNotes = bookingNotes;
  }

  public ReservationDto childrenNumber(Integer childrenNumber) {
    this.childrenNumber = childrenNumber;
    return this;
  }

  /**
   * Get childrenNumber
   * minimum: 0
   * maximum: 3
   * @return childrenNumber
   */
  @Min(0) @Max(3) 
  @Schema(name = "childrenNumber", example = "0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("childrenNumber")
  public Integer getChildrenNumber() {
    return childrenNumber;
  }

  public void setChildrenNumber(Integer childrenNumber) {
    this.childrenNumber = childrenNumber;
  }

  public ReservationDto cotRequired(Boolean cotRequired) {
    this.cotRequired = cotRequired;
    return this;
  }

  /**
   * Get cotRequired
   * @return cotRequired
   */
  
  @Schema(name = "cotRequired", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cotRequired")
  public Boolean getCotRequired() {
    return cotRequired;
  }

  public void setCotRequired(Boolean cotRequired) {
    this.cotRequired = cotRequired;
  }

  public ReservationDto departure(LocalDate departure) {
    this.departure = departure;
    return this;
  }

  /**
   * Get departure
   * @return departure
   */
  @NotNull @Valid 
  @Schema(name = "departure", example = "2015-10-21", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("departure")
  public LocalDate getDeparture() {
    return departure;
  }

  public void setDeparture(LocalDate departure) {
    this.departure = departure;
  }

  public ReservationDto distributionIATANumber(String distributionIATANumber) {
    this.distributionIATANumber = distributionIATANumber;
    return this;
  }

  /**
   * Get distributionIATANumber
   * @return distributionIATANumber
   */
  
  @Schema(name = "distributionIATANumber", example = "00000000123456", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("distributionIATANumber")
  public String getDistributionIATANumber() {
    return distributionIATANumber;
  }

  public void setDistributionIATANumber(String distributionIATANumber) {
    this.distributionIATANumber = distributionIATANumber;
  }

  public ReservationDto distributionUsername(String distributionUsername) {
    this.distributionUsername = distributionUsername;
    return this;
  }

  /**
   * Get distributionUsername
   * @return distributionUsername
   */
  
  @Schema(name = "distributionUsername", example = "distributionUsername", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("distributionUsername")
  public String getDistributionUsername() {
    return distributionUsername;
  }

  public void setDistributionUsername(String distributionUsername) {
    this.distributionUsername = distributionUsername;
  }

  public ReservationDto gdsReferenceNumber(String gdsReferenceNumber) {
    this.gdsReferenceNumber = gdsReferenceNumber;
    return this;
  }

  /**
   * Get gdsReferenceNumber
   * @return gdsReferenceNumber
   */
  
  @Schema(name = "gdsReferenceNumber", example = "gdsReferenceNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("gdsReferenceNumber")
  public String getGdsReferenceNumber() {
    return gdsReferenceNumber;
  }

  public void setGdsReferenceNumber(String gdsReferenceNumber) {
    this.gdsReferenceNumber = gdsReferenceNumber;
  }

  public ReservationDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  @NotNull @Pattern(regexp = "^[A-Za-z]{6}$") 
  @Schema(name = "hotelId", example = "LONSTM", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public ReservationDto reservationPackages(List<@Valid ReservationPackageDto> reservationPackages) {
    this.reservationPackages = reservationPackages;
    return this;
  }

  public ReservationDto addReservationPackagesItem(ReservationPackageDto reservationPackagesItem) {
    if (this.reservationPackages == null) {
      this.reservationPackages = new ArrayList<>();
    }
    this.reservationPackages.add(reservationPackagesItem);
    return this;
  }

  /**
   * Get reservationPackages
   * @return reservationPackages
   */
  @Valid 
  @Schema(name = "reservationPackages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationPackages")
  public List<@Valid ReservationPackageDto> getReservationPackages() {
    return reservationPackages;
  }

  public void setReservationPackages(List<@Valid ReservationPackageDto> reservationPackages) {
    this.reservationPackages = reservationPackages;
  }

  public ReservationDto roomRates(RoomRateDto roomRates) {
    this.roomRates = roomRates;
    return this;
  }

  /**
   * Get roomRates
   * @return roomRates
   */
  @NotNull @Valid 
  @Schema(name = "roomRates", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("roomRates")
  public RoomRateDto getRoomRates() {
    return roomRates;
  }

  public void setRoomRates(RoomRateDto roomRates) {
    this.roomRates = roomRates;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationDto reservationDto = (ReservationDto) o;
    return Objects.equals(this.adultsNumber, reservationDto.adultsNumber) &&
        Objects.equals(this.arrival, reservationDto.arrival) &&
        Objects.equals(this.basketReferenceId, reservationDto.basketReferenceId) &&
        Objects.equals(this.bookingNotes, reservationDto.bookingNotes) &&
        Objects.equals(this.childrenNumber, reservationDto.childrenNumber) &&
        Objects.equals(this.cotRequired, reservationDto.cotRequired) &&
        Objects.equals(this.departure, reservationDto.departure) &&
        Objects.equals(this.distributionIATANumber, reservationDto.distributionIATANumber) &&
        Objects.equals(this.distributionUsername, reservationDto.distributionUsername) &&
        Objects.equals(this.gdsReferenceNumber, reservationDto.gdsReferenceNumber) &&
        Objects.equals(this.hotelId, reservationDto.hotelId) &&
        Objects.equals(this.reservationPackages, reservationDto.reservationPackages) &&
        Objects.equals(this.roomRates, reservationDto.roomRates);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adultsNumber, arrival, basketReferenceId, bookingNotes, childrenNumber, cotRequired, departure, distributionIATANumber, distributionUsername, gdsReferenceNumber, hotelId, reservationPackages, roomRates);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationDto {\n");
    sb.append("    adultsNumber: ").append(toIndentedString(adultsNumber)).append("\n");
    sb.append("    arrival: ").append(toIndentedString(arrival)).append("\n");
    sb.append("    basketReferenceId: ").append(toIndentedString(basketReferenceId)).append("\n");
    sb.append("    bookingNotes: ").append(toIndentedString(bookingNotes)).append("\n");
    sb.append("    childrenNumber: ").append(toIndentedString(childrenNumber)).append("\n");
    sb.append("    cotRequired: ").append(toIndentedString(cotRequired)).append("\n");
    sb.append("    departure: ").append(toIndentedString(departure)).append("\n");
    sb.append("    distributionIATANumber: ").append(toIndentedString(distributionIATANumber)).append("\n");
    sb.append("    distributionUsername: ").append(toIndentedString(distributionUsername)).append("\n");
    sb.append("    gdsReferenceNumber: ").append(toIndentedString(gdsReferenceNumber)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    reservationPackages: ").append(toIndentedString(reservationPackages)).append("\n");
    sb.append("    roomRates: ").append(toIndentedString(roomRates)).append("\n");
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


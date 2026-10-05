package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.LeadGuestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackagesDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomRateDto;
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

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationDto {

  private Integer adults;

  private String arrival;

  private @Nullable String bookingNotes;

  private @Nullable String bookingType;

  private @Nullable String ccuiUserEmailId;

  private @Nullable Integer children;

  private @Nullable String companyAccountId;

  private @Nullable Boolean cotRequired;

  private String departure;

  private @Nullable String distributionIATANumber;

  private @Nullable String distributionUsername;

  private @Nullable String externalReferenceId;

  private @Nullable String gdsReferenceNumber;

  private @Nullable String hotelId;

  private @Nullable LeadGuestDto leadGuest;

  private @Nullable String operaCompanyId;

  @Valid
  private List<@Valid ReservationPackagesDto> reservationPackages = new ArrayList<>();

  private RoomRateDto roomRates;

  private @Nullable String userAccountId;

  public ReservationDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ReservationDto(Integer adults, String arrival, String departure, RoomRateDto roomRates) {
    this.adults = adults;
    this.arrival = arrival;
    this.departure = departure;
    this.roomRates = roomRates;
  }

  public ReservationDto adults(Integer adults) {
    this.adults = adults;
    return this;
  }

  /**
   * Get adults
   * minimum: 1
   * maximum: 2
   * @return adults
   */
  @NotNull @Min(1) @Max(2) 
  @Schema(name = "adults", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("adults")
  public Integer getAdults() {
    return adults;
  }

  public void setAdults(Integer adults) {
    this.adults = adults;
  }

  public ReservationDto arrival(String arrival) {
    this.arrival = arrival;
    return this;
  }

  /**
   * Get arrival
   * @return arrival
   */
  @NotNull 
  @Schema(name = "arrival", example = "2015-10-20", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("arrival")
  public String getArrival() {
    return arrival;
  }

  public void setArrival(String arrival) {
    this.arrival = arrival;
  }

  public ReservationDto bookingNotes(String bookingNotes) {
    this.bookingNotes = bookingNotes;
    return this;
  }

  /**
   * Get bookingNotes
   * @return bookingNotes
   */
  
  @Schema(name = "bookingNotes", example = "nonSmoking", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingNotes")
  public String getBookingNotes() {
    return bookingNotes;
  }

  public void setBookingNotes(String bookingNotes) {
    this.bookingNotes = bookingNotes;
  }

  public ReservationDto bookingType(String bookingType) {
    this.bookingType = bookingType;
    return this;
  }

  /**
   * Get bookingType
   * @return bookingType
   */
  
  @Schema(name = "bookingType", example = "ANON", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingType")
  public String getBookingType() {
    return bookingType;
  }

  public void setBookingType(String bookingType) {
    this.bookingType = bookingType;
  }

  public ReservationDto ccuiUserEmailId(String ccuiUserEmailId) {
    this.ccuiUserEmailId = ccuiUserEmailId;
    return this;
  }

  /**
   * Get ccuiUserEmailId
   * @return ccuiUserEmailId
   */
  
  @Schema(name = "ccuiUserEmailId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ccuiUserEmailId")
  public String getCcuiUserEmailId() {
    return ccuiUserEmailId;
  }

  public void setCcuiUserEmailId(String ccuiUserEmailId) {
    this.ccuiUserEmailId = ccuiUserEmailId;
  }

  public ReservationDto children(Integer children) {
    this.children = children;
    return this;
  }

  /**
   * Get children
   * minimum: 0
   * maximum: 3
   * @return children
   */
  @Min(0) @Max(3) 
  @Schema(name = "children", example = "0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("children")
  public Integer getChildren() {
    return children;
  }

  public void setChildren(Integer children) {
    this.children = children;
  }

  public ReservationDto companyAccountId(String companyAccountId) {
    this.companyAccountId = companyAccountId;
    return this;
  }

  /**
   * Get companyAccountId
   * @return companyAccountId
   */
  
  @Schema(name = "companyAccountId", example = "COMP_7846063e-acd1-4931-8c07-1c33ddc2a42f", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyAccountId")
  public String getCompanyAccountId() {
    return companyAccountId;
  }

  public void setCompanyAccountId(String companyAccountId) {
    this.companyAccountId = companyAccountId;
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

  public ReservationDto departure(String departure) {
    this.departure = departure;
    return this;
  }

  /**
   * Get departure
   * @return departure
   */
  @NotNull 
  @Schema(name = "departure", example = "2015-10-21", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("departure")
  public String getDeparture() {
    return departure;
  }

  public void setDeparture(String departure) {
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
  
  @Schema(name = "distributionUsername", example = "testUsername", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("distributionUsername")
  public String getDistributionUsername() {
    return distributionUsername;
  }

  public void setDistributionUsername(String distributionUsername) {
    this.distributionUsername = distributionUsername;
  }

  public ReservationDto externalReferenceId(String externalReferenceId) {
    this.externalReferenceId = externalReferenceId;
    return this;
  }

  /**
   * Get externalReferenceId
   * @return externalReferenceId
   */
  
  @Schema(name = "externalReferenceId", example = "132484", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("externalReferenceId")
  public String getExternalReferenceId() {
    return externalReferenceId;
  }

  public void setExternalReferenceId(String externalReferenceId) {
    this.externalReferenceId = externalReferenceId;
  }

  public ReservationDto gdsReferenceNumber(String gdsReferenceNumber) {
    this.gdsReferenceNumber = gdsReferenceNumber;
    return this;
  }

  /**
   * Get gdsReferenceNumber
   * @return gdsReferenceNumber
   */
  
  @Schema(name = "gdsReferenceNumber", example = "ABCD1234", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
  @Pattern(regexp = "^[A-Za-z]{6}$") 
  @Schema(name = "hotelId", example = "LONSTM", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public ReservationDto leadGuest(LeadGuestDto leadGuest) {
    this.leadGuest = leadGuest;
    return this;
  }

  /**
   * Get leadGuest
   * @return leadGuest
   */
  @Valid 
  @Schema(name = "leadGuest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("leadGuest")
  public LeadGuestDto getLeadGuest() {
    return leadGuest;
  }

  public void setLeadGuest(LeadGuestDto leadGuest) {
    this.leadGuest = leadGuest;
  }

  public ReservationDto operaCompanyId(String operaCompanyId) {
    this.operaCompanyId = operaCompanyId;
    return this;
  }

  /**
   * Get operaCompanyId
   * @return operaCompanyId
   */
  
  @Schema(name = "operaCompanyId", example = "12345", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("operaCompanyId")
  public String getOperaCompanyId() {
    return operaCompanyId;
  }

  public void setOperaCompanyId(String operaCompanyId) {
    this.operaCompanyId = operaCompanyId;
  }

  public ReservationDto reservationPackages(List<@Valid ReservationPackagesDto> reservationPackages) {
    this.reservationPackages = reservationPackages;
    return this;
  }

  public ReservationDto addReservationPackagesItem(ReservationPackagesDto reservationPackagesItem) {
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
  public List<@Valid ReservationPackagesDto> getReservationPackages() {
    return reservationPackages;
  }

  public void setReservationPackages(List<@Valid ReservationPackagesDto> reservationPackages) {
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

  public ReservationDto userAccountId(String userAccountId) {
    this.userAccountId = userAccountId;
    return this;
  }

  /**
   * Get userAccountId
   * @return userAccountId
   */
  
  @Schema(name = "userAccountId", example = "EMPL_5404e3cf-0c57-4987-84f6-3f70a4079bdc", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userAccountId")
  public String getUserAccountId() {
    return userAccountId;
  }

  public void setUserAccountId(String userAccountId) {
    this.userAccountId = userAccountId;
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
    return Objects.equals(this.adults, reservationDto.adults) &&
        Objects.equals(this.arrival, reservationDto.arrival) &&
        Objects.equals(this.bookingNotes, reservationDto.bookingNotes) &&
        Objects.equals(this.bookingType, reservationDto.bookingType) &&
        Objects.equals(this.ccuiUserEmailId, reservationDto.ccuiUserEmailId) &&
        Objects.equals(this.children, reservationDto.children) &&
        Objects.equals(this.companyAccountId, reservationDto.companyAccountId) &&
        Objects.equals(this.cotRequired, reservationDto.cotRequired) &&
        Objects.equals(this.departure, reservationDto.departure) &&
        Objects.equals(this.distributionIATANumber, reservationDto.distributionIATANumber) &&
        Objects.equals(this.distributionUsername, reservationDto.distributionUsername) &&
        Objects.equals(this.externalReferenceId, reservationDto.externalReferenceId) &&
        Objects.equals(this.gdsReferenceNumber, reservationDto.gdsReferenceNumber) &&
        Objects.equals(this.hotelId, reservationDto.hotelId) &&
        Objects.equals(this.leadGuest, reservationDto.leadGuest) &&
        Objects.equals(this.operaCompanyId, reservationDto.operaCompanyId) &&
        Objects.equals(this.reservationPackages, reservationDto.reservationPackages) &&
        Objects.equals(this.roomRates, reservationDto.roomRates) &&
        Objects.equals(this.userAccountId, reservationDto.userAccountId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adults, arrival, bookingNotes, bookingType, ccuiUserEmailId, children, companyAccountId, cotRequired, departure, distributionIATANumber, distributionUsername, externalReferenceId, gdsReferenceNumber, hotelId, leadGuest, operaCompanyId, reservationPackages, roomRates, userAccountId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationDto {\n");
    sb.append("    adults: ").append(toIndentedString(adults)).append("\n");
    sb.append("    arrival: ").append(toIndentedString(arrival)).append("\n");
    sb.append("    bookingNotes: ").append(toIndentedString(bookingNotes)).append("\n");
    sb.append("    bookingType: ").append(toIndentedString(bookingType)).append("\n");
    sb.append("    ccuiUserEmailId: ").append(toIndentedString(ccuiUserEmailId)).append("\n");
    sb.append("    children: ").append(toIndentedString(children)).append("\n");
    sb.append("    companyAccountId: ").append(toIndentedString(companyAccountId)).append("\n");
    sb.append("    cotRequired: ").append(toIndentedString(cotRequired)).append("\n");
    sb.append("    departure: ").append(toIndentedString(departure)).append("\n");
    sb.append("    distributionIATANumber: ").append(toIndentedString(distributionIATANumber)).append("\n");
    sb.append("    distributionUsername: ").append(toIndentedString(distributionUsername)).append("\n");
    sb.append("    externalReferenceId: ").append(toIndentedString(externalReferenceId)).append("\n");
    sb.append("    gdsReferenceNumber: ").append(toIndentedString(gdsReferenceNumber)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    leadGuest: ").append(toIndentedString(leadGuest)).append("\n");
    sb.append("    operaCompanyId: ").append(toIndentedString(operaCompanyId)).append("\n");
    sb.append("    reservationPackages: ").append(toIndentedString(reservationPackages)).append("\n");
    sb.append("    roomRates: ").append(toIndentedString(roomRates)).append("\n");
    sb.append("    userAccountId: ").append(toIndentedString(userAccountId)).append("\n");
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


package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BookerDetailsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.StayingGuestDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ReservationGuestRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationGuestRequestDto {

  private BookerDetailsDto booker;

  private @Nullable String bookerProfileId;

  private @Nullable String bookingType;

  private @Nullable String companyAccountId;

  private @Nullable String companyProfileId;

  private String hotelId;

  private @Nullable Boolean preCheckIn;

  private String reasonForStay;

  private @Nullable Boolean sendEmailConfirmation;

  private @Nullable Boolean sendEmailInvoice;

  @Valid
  private List<@Valid StayingGuestDto> stayingGuests = new ArrayList<>();

  private @Nullable String userAccountId;

  public ReservationGuestRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ReservationGuestRequestDto(BookerDetailsDto booker, String hotelId, String reasonForStay, List<@Valid StayingGuestDto> stayingGuests) {
    this.booker = booker;
    this.hotelId = hotelId;
    this.reasonForStay = reasonForStay;
    this.stayingGuests = stayingGuests;
  }

  public ReservationGuestRequestDto booker(BookerDetailsDto booker) {
    this.booker = booker;
    return this;
  }

  /**
   * Get booker
   * @return booker
   */
  @NotNull @Valid 
  @Schema(name = "booker", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("booker")
  public BookerDetailsDto getBooker() {
    return booker;
  }

  public void setBooker(BookerDetailsDto booker) {
    this.booker = booker;
  }

  public ReservationGuestRequestDto bookerProfileId(String bookerProfileId) {
    this.bookerProfileId = bookerProfileId;
    return this;
  }

  /**
   * Get bookerProfileId
   * @return bookerProfileId
   */
  
  @Schema(name = "bookerProfileId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookerProfileId")
  public String getBookerProfileId() {
    return bookerProfileId;
  }

  public void setBookerProfileId(String bookerProfileId) {
    this.bookerProfileId = bookerProfileId;
  }

  public ReservationGuestRequestDto bookingType(String bookingType) {
    this.bookingType = bookingType;
    return this;
  }

  /**
   * Get bookingType
   * @return bookingType
   */
  
  @Schema(name = "bookingType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingType")
  public String getBookingType() {
    return bookingType;
  }

  public void setBookingType(String bookingType) {
    this.bookingType = bookingType;
  }

  public ReservationGuestRequestDto companyAccountId(String companyAccountId) {
    this.companyAccountId = companyAccountId;
    return this;
  }

  /**
   * Get companyAccountId
   * @return companyAccountId
   */
  
  @Schema(name = "companyAccountId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyAccountId")
  public String getCompanyAccountId() {
    return companyAccountId;
  }

  public void setCompanyAccountId(String companyAccountId) {
    this.companyAccountId = companyAccountId;
  }

  public ReservationGuestRequestDto companyProfileId(String companyProfileId) {
    this.companyProfileId = companyProfileId;
    return this;
  }

  /**
   * Get companyProfileId
   * @return companyProfileId
   */
  
  @Schema(name = "companyProfileId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyProfileId")
  public String getCompanyProfileId() {
    return companyProfileId;
  }

  public void setCompanyProfileId(String companyProfileId) {
    this.companyProfileId = companyProfileId;
  }

  public ReservationGuestRequestDto hotelId(String hotelId) {
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

  public ReservationGuestRequestDto preCheckIn(Boolean preCheckIn) {
    this.preCheckIn = preCheckIn;
    return this;
  }

  /**
   * Get preCheckIn
   * @return preCheckIn
   */
  
  @Schema(name = "preCheckIn", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("preCheckIn")
  public Boolean getPreCheckIn() {
    return preCheckIn;
  }

  public void setPreCheckIn(Boolean preCheckIn) {
    this.preCheckIn = preCheckIn;
  }

  public ReservationGuestRequestDto reasonForStay(String reasonForStay) {
    this.reasonForStay = reasonForStay;
    return this;
  }

  /**
   * Get reasonForStay
   * @return reasonForStay
   */
  @NotNull 
  @Schema(name = "reasonForStay", example = "LEI", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reasonForStay")
  public String getReasonForStay() {
    return reasonForStay;
  }

  public void setReasonForStay(String reasonForStay) {
    this.reasonForStay = reasonForStay;
  }

  public ReservationGuestRequestDto sendEmailConfirmation(Boolean sendEmailConfirmation) {
    this.sendEmailConfirmation = sendEmailConfirmation;
    return this;
  }

  /**
   * Get sendEmailConfirmation
   * @return sendEmailConfirmation
   */
  
  @Schema(name = "sendEmailConfirmation", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sendEmailConfirmation")
  public Boolean getSendEmailConfirmation() {
    return sendEmailConfirmation;
  }

  public void setSendEmailConfirmation(Boolean sendEmailConfirmation) {
    this.sendEmailConfirmation = sendEmailConfirmation;
  }

  public ReservationGuestRequestDto sendEmailInvoice(Boolean sendEmailInvoice) {
    this.sendEmailInvoice = sendEmailInvoice;
    return this;
  }

  /**
   * Get sendEmailInvoice
   * @return sendEmailInvoice
   */
  
  @Schema(name = "sendEmailInvoice", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sendEmailInvoice")
  public Boolean getSendEmailInvoice() {
    return sendEmailInvoice;
  }

  public void setSendEmailInvoice(Boolean sendEmailInvoice) {
    this.sendEmailInvoice = sendEmailInvoice;
  }

  public ReservationGuestRequestDto stayingGuests(List<@Valid StayingGuestDto> stayingGuests) {
    this.stayingGuests = stayingGuests;
    return this;
  }

  public ReservationGuestRequestDto addStayingGuestsItem(StayingGuestDto stayingGuestsItem) {
    if (this.stayingGuests == null) {
      this.stayingGuests = new ArrayList<>();
    }
    this.stayingGuests.add(stayingGuestsItem);
    return this;
  }

  /**
   * Get stayingGuests
   * @return stayingGuests
   */
  @NotNull @Valid @Size(min = 1, max = 2147483647) 
  @Schema(name = "stayingGuests", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("stayingGuests")
  public List<@Valid StayingGuestDto> getStayingGuests() {
    return stayingGuests;
  }

  public void setStayingGuests(List<@Valid StayingGuestDto> stayingGuests) {
    this.stayingGuests = stayingGuests;
  }

  public ReservationGuestRequestDto userAccountId(String userAccountId) {
    this.userAccountId = userAccountId;
    return this;
  }

  /**
   * Get userAccountId
   * @return userAccountId
   */
  
  @Schema(name = "userAccountId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    ReservationGuestRequestDto reservationGuestRequestDto = (ReservationGuestRequestDto) o;
    return Objects.equals(this.booker, reservationGuestRequestDto.booker) &&
        Objects.equals(this.bookerProfileId, reservationGuestRequestDto.bookerProfileId) &&
        Objects.equals(this.bookingType, reservationGuestRequestDto.bookingType) &&
        Objects.equals(this.companyAccountId, reservationGuestRequestDto.companyAccountId) &&
        Objects.equals(this.companyProfileId, reservationGuestRequestDto.companyProfileId) &&
        Objects.equals(this.hotelId, reservationGuestRequestDto.hotelId) &&
        Objects.equals(this.preCheckIn, reservationGuestRequestDto.preCheckIn) &&
        Objects.equals(this.reasonForStay, reservationGuestRequestDto.reasonForStay) &&
        Objects.equals(this.sendEmailConfirmation, reservationGuestRequestDto.sendEmailConfirmation) &&
        Objects.equals(this.sendEmailInvoice, reservationGuestRequestDto.sendEmailInvoice) &&
        Objects.equals(this.stayingGuests, reservationGuestRequestDto.stayingGuests) &&
        Objects.equals(this.userAccountId, reservationGuestRequestDto.userAccountId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(booker, bookerProfileId, bookingType, companyAccountId, companyProfileId, hotelId, preCheckIn, reasonForStay, sendEmailConfirmation, sendEmailInvoice, stayingGuests, userAccountId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationGuestRequestDto {\n");
    sb.append("    booker: ").append(toIndentedString(booker)).append("\n");
    sb.append("    bookerProfileId: ").append(toIndentedString(bookerProfileId)).append("\n");
    sb.append("    bookingType: ").append(toIndentedString(bookingType)).append("\n");
    sb.append("    companyAccountId: ").append(toIndentedString(companyAccountId)).append("\n");
    sb.append("    companyProfileId: ").append(toIndentedString(companyProfileId)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    preCheckIn: ").append(toIndentedString(preCheckIn)).append("\n");
    sb.append("    reasonForStay: ").append(toIndentedString(reasonForStay)).append("\n");
    sb.append("    sendEmailConfirmation: ").append(toIndentedString(sendEmailConfirmation)).append("\n");
    sb.append("    sendEmailInvoice: ").append(toIndentedString(sendEmailInvoice)).append("\n");
    sb.append("    stayingGuests: ").append(toIndentedString(stayingGuests)).append("\n");
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


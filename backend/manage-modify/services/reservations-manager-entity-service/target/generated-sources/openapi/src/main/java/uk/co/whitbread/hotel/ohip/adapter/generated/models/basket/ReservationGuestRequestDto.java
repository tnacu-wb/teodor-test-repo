package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BookerDetailsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.StayingGuestDto;
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

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:44.119190+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationGuestRequestDto {

  private @Nullable String basketReference;

  private @Nullable BookerDetailsDto booker;

  private @Nullable String bookerProfileId;

  private @Nullable String companyProfileId;

  private @Nullable String hotelId;

  private @Nullable String reasonForStay;

  private @Nullable Boolean sendEmailConfirmation;

  private @Nullable Boolean sendEmailInvoice;

  @Valid
  private List<@Valid StayingGuestDto> stayingGuests = new ArrayList<>();

  public ReservationGuestRequestDto basketReference(String basketReference) {
    this.basketReference = basketReference;
    return this;
  }

  /**
   * Get basketReference
   * @return basketReference
   */
  
  @Schema(name = "basketReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("basketReference")
  public String getBasketReference() {
    return basketReference;
  }

  public void setBasketReference(String basketReference) {
    this.basketReference = basketReference;
  }

  public ReservationGuestRequestDto booker(BookerDetailsDto booker) {
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
  
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public ReservationGuestRequestDto reasonForStay(String reasonForStay) {
    this.reasonForStay = reasonForStay;
    return this;
  }

  /**
   * Get reasonForStay
   * @return reasonForStay
   */
  
  @Schema(name = "reasonForStay", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
  
  @Schema(name = "sendEmailConfirmation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
  
  @Schema(name = "sendEmailInvoice", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
  @Valid 
  @Schema(name = "stayingGuests", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("stayingGuests")
  public List<@Valid StayingGuestDto> getStayingGuests() {
    return stayingGuests;
  }

  public void setStayingGuests(List<@Valid StayingGuestDto> stayingGuests) {
    this.stayingGuests = stayingGuests;
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
    return Objects.equals(this.basketReference, reservationGuestRequestDto.basketReference) &&
        Objects.equals(this.booker, reservationGuestRequestDto.booker) &&
        Objects.equals(this.bookerProfileId, reservationGuestRequestDto.bookerProfileId) &&
        Objects.equals(this.companyProfileId, reservationGuestRequestDto.companyProfileId) &&
        Objects.equals(this.hotelId, reservationGuestRequestDto.hotelId) &&
        Objects.equals(this.reasonForStay, reservationGuestRequestDto.reasonForStay) &&
        Objects.equals(this.sendEmailConfirmation, reservationGuestRequestDto.sendEmailConfirmation) &&
        Objects.equals(this.sendEmailInvoice, reservationGuestRequestDto.sendEmailInvoice) &&
        Objects.equals(this.stayingGuests, reservationGuestRequestDto.stayingGuests);
  }

  @Override
  public int hashCode() {
    return Objects.hash(basketReference, booker, bookerProfileId, companyProfileId, hotelId, reasonForStay, sendEmailConfirmation, sendEmailInvoice, stayingGuests);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationGuestRequestDto {\n");
    sb.append("    basketReference: ").append(toIndentedString(basketReference)).append("\n");
    sb.append("    booker: ").append(toIndentedString(booker)).append("\n");
    sb.append("    bookerProfileId: ").append(toIndentedString(bookerProfileId)).append("\n");
    sb.append("    companyProfileId: ").append(toIndentedString(companyProfileId)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    reasonForStay: ").append(toIndentedString(reasonForStay)).append("\n");
    sb.append("    sendEmailConfirmation: ").append(toIndentedString(sendEmailConfirmation)).append("\n");
    sb.append("    sendEmailInvoice: ").append(toIndentedString(sendEmailInvoice)).append("\n");
    sb.append("    stayingGuests: ").append(toIndentedString(stayingGuests)).append("\n");
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


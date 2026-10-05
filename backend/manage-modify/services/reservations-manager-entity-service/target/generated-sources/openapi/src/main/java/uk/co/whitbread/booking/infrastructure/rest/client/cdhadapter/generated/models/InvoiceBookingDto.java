package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceBillingContactDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceExternalReferencesDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceGuestDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * InvoiceBookingDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:45.778333+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class InvoiceBookingDto {

  private @Nullable String arrivalDate;

  private @Nullable InvoiceBillingContactDto billingContact;

  private @Nullable String bookingReference;

  private @Nullable String confirmationNumber;

  private @Nullable String dctmcUserNAme;

  private @Nullable String departureDate;

  private @Nullable InvoiceExternalReferencesDto externalReferences;

  @Valid
  private List<@Valid InvoiceGuestDto> guest = new ArrayList<>();

  private @Nullable Integer iataNumber;

  private @Nullable String iataNumberString;

  private @Nullable String partnerId;

  private @Nullable String reservationId;

  private @Nullable String roomNumber;

  public InvoiceBookingDto arrivalDate(String arrivalDate) {
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

  public InvoiceBookingDto billingContact(InvoiceBillingContactDto billingContact) {
    this.billingContact = billingContact;
    return this;
  }

  /**
   * Get billingContact
   * @return billingContact
   */
  @Valid 
  @Schema(name = "billingContact", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("billingContact")
  public InvoiceBillingContactDto getBillingContact() {
    return billingContact;
  }

  public void setBillingContact(InvoiceBillingContactDto billingContact) {
    this.billingContact = billingContact;
  }

  public InvoiceBookingDto bookingReference(String bookingReference) {
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

  public InvoiceBookingDto confirmationNumber(String confirmationNumber) {
    this.confirmationNumber = confirmationNumber;
    return this;
  }

  /**
   * Get confirmationNumber
   * @return confirmationNumber
   */
  
  @Schema(name = "confirmationNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("confirmationNumber")
  public String getConfirmationNumber() {
    return confirmationNumber;
  }

  public void setConfirmationNumber(String confirmationNumber) {
    this.confirmationNumber = confirmationNumber;
  }

  public InvoiceBookingDto dctmcUserNAme(String dctmcUserNAme) {
    this.dctmcUserNAme = dctmcUserNAme;
    return this;
  }

  /**
   * Get dctmcUserNAme
   * @return dctmcUserNAme
   */
  
  @Schema(name = "dctmcUserNAme", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dctmcUserNAme")
  public String getDctmcUserNAme() {
    return dctmcUserNAme;
  }

  public void setDctmcUserNAme(String dctmcUserNAme) {
    this.dctmcUserNAme = dctmcUserNAme;
  }

  public InvoiceBookingDto departureDate(String departureDate) {
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

  public InvoiceBookingDto externalReferences(InvoiceExternalReferencesDto externalReferences) {
    this.externalReferences = externalReferences;
    return this;
  }

  /**
   * Get externalReferences
   * @return externalReferences
   */
  @Valid 
  @Schema(name = "externalReferences", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("externalReferences")
  public InvoiceExternalReferencesDto getExternalReferences() {
    return externalReferences;
  }

  public void setExternalReferences(InvoiceExternalReferencesDto externalReferences) {
    this.externalReferences = externalReferences;
  }

  public InvoiceBookingDto guest(List<@Valid InvoiceGuestDto> guest) {
    this.guest = guest;
    return this;
  }

  public InvoiceBookingDto addGuestItem(InvoiceGuestDto guestItem) {
    if (this.guest == null) {
      this.guest = new ArrayList<>();
    }
    this.guest.add(guestItem);
    return this;
  }

  /**
   * Get guest
   * @return guest
   */
  @Valid 
  @Schema(name = "guest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("guest")
  public List<@Valid InvoiceGuestDto> getGuest() {
    return guest;
  }

  public void setGuest(List<@Valid InvoiceGuestDto> guest) {
    this.guest = guest;
  }

  public InvoiceBookingDto iataNumber(Integer iataNumber) {
    this.iataNumber = iataNumber;
    return this;
  }

  /**
   * Get iataNumber
   * @return iataNumber
   */
  
  @Schema(name = "iataNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("iataNumber")
  public Integer getIataNumber() {
    return iataNumber;
  }

  public void setIataNumber(Integer iataNumber) {
    this.iataNumber = iataNumber;
  }

  public InvoiceBookingDto iataNumberString(String iataNumberString) {
    this.iataNumberString = iataNumberString;
    return this;
  }

  /**
   * Get iataNumberString
   * @return iataNumberString
   */
  
  @Schema(name = "iataNumberString", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("iataNumberString")
  public String getIataNumberString() {
    return iataNumberString;
  }

  public void setIataNumberString(String iataNumberString) {
    this.iataNumberString = iataNumberString;
  }

  public InvoiceBookingDto partnerId(String partnerId) {
    this.partnerId = partnerId;
    return this;
  }

  /**
   * Get partnerId
   * @return partnerId
   */
  
  @Schema(name = "partnerId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("partnerId")
  public String getPartnerId() {
    return partnerId;
  }

  public void setPartnerId(String partnerId) {
    this.partnerId = partnerId;
  }

  public InvoiceBookingDto reservationId(String reservationId) {
    this.reservationId = reservationId;
    return this;
  }

  /**
   * Get reservationId
   * @return reservationId
   */
  
  @Schema(name = "reservationId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationId")
  public String getReservationId() {
    return reservationId;
  }

  public void setReservationId(String reservationId) {
    this.reservationId = reservationId;
  }

  public InvoiceBookingDto roomNumber(String roomNumber) {
    this.roomNumber = roomNumber;
    return this;
  }

  /**
   * Get roomNumber
   * @return roomNumber
   */
  
  @Schema(name = "roomNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomNumber")
  public String getRoomNumber() {
    return roomNumber;
  }

  public void setRoomNumber(String roomNumber) {
    this.roomNumber = roomNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvoiceBookingDto invoiceBookingDto = (InvoiceBookingDto) o;
    return Objects.equals(this.arrivalDate, invoiceBookingDto.arrivalDate) &&
        Objects.equals(this.billingContact, invoiceBookingDto.billingContact) &&
        Objects.equals(this.bookingReference, invoiceBookingDto.bookingReference) &&
        Objects.equals(this.confirmationNumber, invoiceBookingDto.confirmationNumber) &&
        Objects.equals(this.dctmcUserNAme, invoiceBookingDto.dctmcUserNAme) &&
        Objects.equals(this.departureDate, invoiceBookingDto.departureDate) &&
        Objects.equals(this.externalReferences, invoiceBookingDto.externalReferences) &&
        Objects.equals(this.guest, invoiceBookingDto.guest) &&
        Objects.equals(this.iataNumber, invoiceBookingDto.iataNumber) &&
        Objects.equals(this.iataNumberString, invoiceBookingDto.iataNumberString) &&
        Objects.equals(this.partnerId, invoiceBookingDto.partnerId) &&
        Objects.equals(this.reservationId, invoiceBookingDto.reservationId) &&
        Objects.equals(this.roomNumber, invoiceBookingDto.roomNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrivalDate, billingContact, bookingReference, confirmationNumber, dctmcUserNAme, departureDate, externalReferences, guest, iataNumber, iataNumberString, partnerId, reservationId, roomNumber);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvoiceBookingDto {\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    billingContact: ").append(toIndentedString(billingContact)).append("\n");
    sb.append("    bookingReference: ").append(toIndentedString(bookingReference)).append("\n");
    sb.append("    confirmationNumber: ").append(toIndentedString(confirmationNumber)).append("\n");
    sb.append("    dctmcUserNAme: ").append(toIndentedString(dctmcUserNAme)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    externalReferences: ").append(toIndentedString(externalReferences)).append("\n");
    sb.append("    guest: ").append(toIndentedString(guest)).append("\n");
    sb.append("    iataNumber: ").append(toIndentedString(iataNumber)).append("\n");
    sb.append("    iataNumberString: ").append(toIndentedString(iataNumberString)).append("\n");
    sb.append("    partnerId: ").append(toIndentedString(partnerId)).append("\n");
    sb.append("    reservationId: ").append(toIndentedString(reservationId)).append("\n");
    sb.append("    roomNumber: ").append(toIndentedString(roomNumber)).append("\n");
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


package uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CdhReservationSearchCriteriaDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-09T08:37:59.335673+03:00[Europe/Bucharest]", comments = "Generator version: 7.14.0")
public class CdhReservationSearchCriteriaDto {

  private @Nullable String arrivalDateFrom;

  private @Nullable String arrivalDateTo;

  private @Nullable String bartGuestHistoryNumber;

  private @Nullable String bookingDate;

  private @Nullable String bookingReference;

  private @Nullable Boolean bookingsDatabaseSearch;

  private @Nullable String cancellationDate;

  private @Nullable String companyAccountId;

  private @Nullable String companyName;

  private @Nullable String continuationToken;

  private @Nullable String customerAccountId;

  private @Nullable String customerReference;

  private @Nullable String emailAddress;

  private @Nullable String employeeAccountId;

  private @Nullable String hotelCode;

  private @Nullable String hotelName;

  private @Nullable String lastName;

  private @Nullable Integer pageNumber;

  private @Nullable Integer pageSize;

  private @Nullable String postalCode;

  private @Nullable String purchaseOrder;

  private @Nullable Integer reservationId;

  private @Nullable String status;

  private @Nullable String telephone;

  private @Nullable String thirdPartyReference;

  private @Nullable Integer upcomingDays;

  private @Nullable Integer pastDays;

  private @Nullable Integer cancelledDays;

  public CdhReservationSearchCriteriaDto arrivalDateFrom(@Nullable String arrivalDateFrom) {
    this.arrivalDateFrom = arrivalDateFrom;
    return this;
  }

  /**
   * Get arrivalDateFrom
   * @return arrivalDateFrom
   */
  
  @Schema(name = "arrivalDateFrom", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("arrivalDateFrom")
  public @Nullable String getArrivalDateFrom() {
    return arrivalDateFrom;
  }

  public void setArrivalDateFrom(@Nullable String arrivalDateFrom) {
    this.arrivalDateFrom = arrivalDateFrom;
  }

  public CdhReservationSearchCriteriaDto arrivalDateTo(@Nullable String arrivalDateTo) {
    this.arrivalDateTo = arrivalDateTo;
    return this;
  }

  /**
   * Get arrivalDateTo
   * @return arrivalDateTo
   */
  
  @Schema(name = "arrivalDateTo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("arrivalDateTo")
  public @Nullable String getArrivalDateTo() {
    return arrivalDateTo;
  }

  public void setArrivalDateTo(@Nullable String arrivalDateTo) {
    this.arrivalDateTo = arrivalDateTo;
  }

  public CdhReservationSearchCriteriaDto bartGuestHistoryNumber(@Nullable String bartGuestHistoryNumber) {
    this.bartGuestHistoryNumber = bartGuestHistoryNumber;
    return this;
  }

  /**
   * Get bartGuestHistoryNumber
   * @return bartGuestHistoryNumber
   */
  
  @Schema(name = "bartGuestHistoryNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bartGuestHistoryNumber")
  public @Nullable String getBartGuestHistoryNumber() {
    return bartGuestHistoryNumber;
  }

  public void setBartGuestHistoryNumber(@Nullable String bartGuestHistoryNumber) {
    this.bartGuestHistoryNumber = bartGuestHistoryNumber;
  }

  public CdhReservationSearchCriteriaDto bookingDate(@Nullable String bookingDate) {
    this.bookingDate = bookingDate;
    return this;
  }

  /**
   * Get bookingDate
   * @return bookingDate
   */
  
  @Schema(name = "bookingDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingDate")
  public @Nullable String getBookingDate() {
    return bookingDate;
  }

  public void setBookingDate(@Nullable String bookingDate) {
    this.bookingDate = bookingDate;
  }

  public CdhReservationSearchCriteriaDto bookingReference(@Nullable String bookingReference) {
    this.bookingReference = bookingReference;
    return this;
  }

  /**
   * Get bookingReference
   * @return bookingReference
   */
  
  @Schema(name = "bookingReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingReference")
  public @Nullable String getBookingReference() {
    return bookingReference;
  }

  public void setBookingReference(@Nullable String bookingReference) {
    this.bookingReference = bookingReference;
  }

  public CdhReservationSearchCriteriaDto bookingsDatabaseSearch(@Nullable Boolean bookingsDatabaseSearch) {
    this.bookingsDatabaseSearch = bookingsDatabaseSearch;
    return this;
  }

  /**
   * Get bookingsDatabaseSearch
   * @return bookingsDatabaseSearch
   */
  
  @Schema(name = "bookingsDatabaseSearch", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingsDatabaseSearch")
  public @Nullable Boolean getBookingsDatabaseSearch() {
    return bookingsDatabaseSearch;
  }

  public void setBookingsDatabaseSearch(@Nullable Boolean bookingsDatabaseSearch) {
    this.bookingsDatabaseSearch = bookingsDatabaseSearch;
  }

  public CdhReservationSearchCriteriaDto cancellationDate(@Nullable String cancellationDate) {
    this.cancellationDate = cancellationDate;
    return this;
  }

  /**
   * Get cancellationDate
   * @return cancellationDate
   */
  
  @Schema(name = "cancellationDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cancellationDate")
  public @Nullable String getCancellationDate() {
    return cancellationDate;
  }

  public void setCancellationDate(@Nullable String cancellationDate) {
    this.cancellationDate = cancellationDate;
  }

  public CdhReservationSearchCriteriaDto companyAccountId(@Nullable String companyAccountId) {
    this.companyAccountId = companyAccountId;
    return this;
  }

  /**
   * Get companyAccountId
   * @return companyAccountId
   */
  
  @Schema(name = "companyAccountId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyAccountId")
  public @Nullable String getCompanyAccountId() {
    return companyAccountId;
  }

  public void setCompanyAccountId(@Nullable String companyAccountId) {
    this.companyAccountId = companyAccountId;
  }

  public CdhReservationSearchCriteriaDto companyName(@Nullable String companyName) {
    this.companyName = companyName;
    return this;
  }

  /**
   * Get companyName
   * @return companyName
   */
  
  @Schema(name = "companyName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyName")
  public @Nullable String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(@Nullable String companyName) {
    this.companyName = companyName;
  }

  public CdhReservationSearchCriteriaDto continuationToken(@Nullable String continuationToken) {
    this.continuationToken = continuationToken;
    return this;
  }

  /**
   * Get continuationToken
   * @return continuationToken
   */
  
  @Schema(name = "continuationToken", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("continuationToken")
  public @Nullable String getContinuationToken() {
    return continuationToken;
  }

  public void setContinuationToken(@Nullable String continuationToken) {
    this.continuationToken = continuationToken;
  }

  public CdhReservationSearchCriteriaDto customerAccountId(@Nullable String customerAccountId) {
    this.customerAccountId = customerAccountId;
    return this;
  }

  /**
   * Get customerAccountId
   * @return customerAccountId
   */
  
  @Schema(name = "customerAccountId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customerAccountId")
  public @Nullable String getCustomerAccountId() {
    return customerAccountId;
  }

  public void setCustomerAccountId(@Nullable String customerAccountId) {
    this.customerAccountId = customerAccountId;
  }

  public CdhReservationSearchCriteriaDto customerReference(@Nullable String customerReference) {
    this.customerReference = customerReference;
    return this;
  }

  /**
   * Get customerReference
   * @return customerReference
   */
  
  @Schema(name = "customerReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customerReference")
  public @Nullable String getCustomerReference() {
    return customerReference;
  }

  public void setCustomerReference(@Nullable String customerReference) {
    this.customerReference = customerReference;
  }

  public CdhReservationSearchCriteriaDto emailAddress(@Nullable String emailAddress) {
    this.emailAddress = emailAddress;
    return this;
  }

  /**
   * Get emailAddress
   * @return emailAddress
   */
  
  @Schema(name = "emailAddress", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emailAddress")
  public @Nullable String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(@Nullable String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public CdhReservationSearchCriteriaDto employeeAccountId(@Nullable String employeeAccountId) {
    this.employeeAccountId = employeeAccountId;
    return this;
  }

  /**
   * Get employeeAccountId
   * @return employeeAccountId
   */
  
  @Schema(name = "employeeAccountId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("employeeAccountId")
  public @Nullable String getEmployeeAccountId() {
    return employeeAccountId;
  }

  public void setEmployeeAccountId(@Nullable String employeeAccountId) {
    this.employeeAccountId = employeeAccountId;
  }

  public CdhReservationSearchCriteriaDto hotelCode(@Nullable String hotelCode) {
    this.hotelCode = hotelCode;
    return this;
  }

  /**
   * Get hotelCode
   * @return hotelCode
   */
  
  @Schema(name = "hotelCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelCode")
  public @Nullable String getHotelCode() {
    return hotelCode;
  }

  public void setHotelCode(@Nullable String hotelCode) {
    this.hotelCode = hotelCode;
  }

  public CdhReservationSearchCriteriaDto hotelName(@Nullable String hotelName) {
    this.hotelName = hotelName;
    return this;
  }

  /**
   * Get hotelName
   * @return hotelName
   */
  
  @Schema(name = "hotelName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelName")
  public @Nullable String getHotelName() {
    return hotelName;
  }

  public void setHotelName(@Nullable String hotelName) {
    this.hotelName = hotelName;
  }

  public CdhReservationSearchCriteriaDto lastName(@Nullable String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * Get lastName
   * @return lastName
   */
  
  @Schema(name = "lastName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastName")
  public @Nullable String getLastName() {
    return lastName;
  }

  public void setLastName(@Nullable String lastName) {
    this.lastName = lastName;
  }

  public CdhReservationSearchCriteriaDto pageNumber(@Nullable Integer pageNumber) {
    this.pageNumber = pageNumber;
    return this;
  }

  /**
   * Get pageNumber
   * @return pageNumber
   */
  
  @Schema(name = "pageNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageNumber")
  public @Nullable Integer getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(@Nullable Integer pageNumber) {
    this.pageNumber = pageNumber;
  }

  public CdhReservationSearchCriteriaDto pageSize(@Nullable Integer pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  /**
   * Get pageSize
   * @return pageSize
   */
  
  @Schema(name = "pageSize", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageSize")
  public @Nullable Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(@Nullable Integer pageSize) {
    this.pageSize = pageSize;
  }

  public CdhReservationSearchCriteriaDto postalCode(@Nullable String postalCode) {
    this.postalCode = postalCode;
    return this;
  }

  /**
   * Get postalCode
   * @return postalCode
   */
  
  @Schema(name = "postalCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("postalCode")
  public @Nullable String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(@Nullable String postalCode) {
    this.postalCode = postalCode;
  }

  public CdhReservationSearchCriteriaDto purchaseOrder(@Nullable String purchaseOrder) {
    this.purchaseOrder = purchaseOrder;
    return this;
  }

  /**
   * Get purchaseOrder
   * @return purchaseOrder
   */
  
  @Schema(name = "purchaseOrder", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("purchaseOrder")
  public @Nullable String getPurchaseOrder() {
    return purchaseOrder;
  }

  public void setPurchaseOrder(@Nullable String purchaseOrder) {
    this.purchaseOrder = purchaseOrder;
  }

  public CdhReservationSearchCriteriaDto reservationId(@Nullable Integer reservationId) {
    this.reservationId = reservationId;
    return this;
  }

  /**
   * Get reservationId
   * @return reservationId
   */
  
  @Schema(name = "reservationId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationId")
  public @Nullable Integer getReservationId() {
    return reservationId;
  }

  public void setReservationId(@Nullable Integer reservationId) {
    this.reservationId = reservationId;
  }

  public CdhReservationSearchCriteriaDto status(@Nullable String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public @Nullable String getStatus() {
    return status;
  }

  public void setStatus(@Nullable String status) {
    this.status = status;
  }

  public CdhReservationSearchCriteriaDto telephone(@Nullable String telephone) {
    this.telephone = telephone;
    return this;
  }

  /**
   * Get telephone
   * @return telephone
   */
  
  @Schema(name = "telephone", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("telephone")
  public @Nullable String getTelephone() {
    return telephone;
  }

  public void setTelephone(@Nullable String telephone) {
    this.telephone = telephone;
  }

  public CdhReservationSearchCriteriaDto thirdPartyReference(@Nullable String thirdPartyReference) {
    this.thirdPartyReference = thirdPartyReference;
    return this;
  }

  /**
   * Get thirdPartyReference
   * @return thirdPartyReference
   */
  
  @Schema(name = "thirdPartyReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("thirdPartyReference")
  public @Nullable String getThirdPartyReference() {
    return thirdPartyReference;
  }

  public void setThirdPartyReference(@Nullable String thirdPartyReference) {
    this.thirdPartyReference = thirdPartyReference;
  }

  public CdhReservationSearchCriteriaDto upcomingDays(@Nullable Integer upcomingDays) {
    this.upcomingDays = upcomingDays;
    return this;
  }

  /**
   * Get upcomingDays
   * @return upcomingDays
   */
  
  @Schema(name = "upcomingDays", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("upcomingDays")
  public @Nullable Integer getUpcomingDays() {
    return upcomingDays;
  }

  public void setUpcomingDays(@Nullable Integer upcomingDays) {
    this.upcomingDays = upcomingDays;
  }

  public CdhReservationSearchCriteriaDto pastDays(@Nullable Integer pastDays) {
    this.pastDays = pastDays;
    return this;
  }

  /**
   * Get pastDays
   * @return pastDays
   */
  
  @Schema(name = "pastDays", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pastDays")
  public @Nullable Integer getPastDays() {
    return pastDays;
  }

  public void setPastDays(@Nullable Integer pastDays) {
    this.pastDays = pastDays;
  }

  public CdhReservationSearchCriteriaDto cancelledDays(@Nullable Integer cancelledDays) {
    this.cancelledDays = cancelledDays;
    return this;
  }

  /**
   * Get cancelledDays
   * @return cancelledDays
   */
  
  @Schema(name = "cancelledDays", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cancelledDays")
  public @Nullable Integer getCancelledDays() {
    return cancelledDays;
  }

  public void setCancelledDays(@Nullable Integer cancelledDays) {
    this.cancelledDays = cancelledDays;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CdhReservationSearchCriteriaDto cdhReservationSearchCriteriaDto = (CdhReservationSearchCriteriaDto) o;
    return Objects.equals(this.arrivalDateFrom, cdhReservationSearchCriteriaDto.arrivalDateFrom) &&
        Objects.equals(this.arrivalDateTo, cdhReservationSearchCriteriaDto.arrivalDateTo) &&
        Objects.equals(this.bartGuestHistoryNumber, cdhReservationSearchCriteriaDto.bartGuestHistoryNumber) &&
        Objects.equals(this.bookingDate, cdhReservationSearchCriteriaDto.bookingDate) &&
        Objects.equals(this.bookingReference, cdhReservationSearchCriteriaDto.bookingReference) &&
        Objects.equals(this.bookingsDatabaseSearch, cdhReservationSearchCriteriaDto.bookingsDatabaseSearch) &&
        Objects.equals(this.cancellationDate, cdhReservationSearchCriteriaDto.cancellationDate) &&
        Objects.equals(this.companyAccountId, cdhReservationSearchCriteriaDto.companyAccountId) &&
        Objects.equals(this.companyName, cdhReservationSearchCriteriaDto.companyName) &&
        Objects.equals(this.continuationToken, cdhReservationSearchCriteriaDto.continuationToken) &&
        Objects.equals(this.customerAccountId, cdhReservationSearchCriteriaDto.customerAccountId) &&
        Objects.equals(this.customerReference, cdhReservationSearchCriteriaDto.customerReference) &&
        Objects.equals(this.emailAddress, cdhReservationSearchCriteriaDto.emailAddress) &&
        Objects.equals(this.employeeAccountId, cdhReservationSearchCriteriaDto.employeeAccountId) &&
        Objects.equals(this.hotelCode, cdhReservationSearchCriteriaDto.hotelCode) &&
        Objects.equals(this.hotelName, cdhReservationSearchCriteriaDto.hotelName) &&
        Objects.equals(this.lastName, cdhReservationSearchCriteriaDto.lastName) &&
        Objects.equals(this.pageNumber, cdhReservationSearchCriteriaDto.pageNumber) &&
        Objects.equals(this.pageSize, cdhReservationSearchCriteriaDto.pageSize) &&
        Objects.equals(this.postalCode, cdhReservationSearchCriteriaDto.postalCode) &&
        Objects.equals(this.purchaseOrder, cdhReservationSearchCriteriaDto.purchaseOrder) &&
        Objects.equals(this.reservationId, cdhReservationSearchCriteriaDto.reservationId) &&
        Objects.equals(this.status, cdhReservationSearchCriteriaDto.status) &&
        Objects.equals(this.telephone, cdhReservationSearchCriteriaDto.telephone) &&
        Objects.equals(this.thirdPartyReference, cdhReservationSearchCriteriaDto.thirdPartyReference) &&
        Objects.equals(this.upcomingDays, cdhReservationSearchCriteriaDto.upcomingDays) &&
        Objects.equals(this.pastDays, cdhReservationSearchCriteriaDto.pastDays) &&
        Objects.equals(this.cancelledDays, cdhReservationSearchCriteriaDto.cancelledDays);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrivalDateFrom, arrivalDateTo, bartGuestHistoryNumber, bookingDate, bookingReference, bookingsDatabaseSearch, cancellationDate, companyAccountId, companyName, continuationToken, customerAccountId, customerReference, emailAddress, employeeAccountId, hotelCode, hotelName, lastName, pageNumber, pageSize, postalCode, purchaseOrder, reservationId, status, telephone, thirdPartyReference, upcomingDays, pastDays, cancelledDays);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CdhReservationSearchCriteriaDto {\n");
    sb.append("    arrivalDateFrom: ").append(toIndentedString(arrivalDateFrom)).append("\n");
    sb.append("    arrivalDateTo: ").append(toIndentedString(arrivalDateTo)).append("\n");
    sb.append("    bartGuestHistoryNumber: ").append(toIndentedString(bartGuestHistoryNumber)).append("\n");
    sb.append("    bookingDate: ").append(toIndentedString(bookingDate)).append("\n");
    sb.append("    bookingReference: ").append(toIndentedString(bookingReference)).append("\n");
    sb.append("    bookingsDatabaseSearch: ").append(toIndentedString(bookingsDatabaseSearch)).append("\n");
    sb.append("    cancellationDate: ").append(toIndentedString(cancellationDate)).append("\n");
    sb.append("    companyAccountId: ").append(toIndentedString(companyAccountId)).append("\n");
    sb.append("    companyName: ").append(toIndentedString(companyName)).append("\n");
    sb.append("    continuationToken: ").append(toIndentedString(continuationToken)).append("\n");
    sb.append("    customerAccountId: ").append(toIndentedString(customerAccountId)).append("\n");
    sb.append("    customerReference: ").append(toIndentedString(customerReference)).append("\n");
    sb.append("    emailAddress: ").append(toIndentedString(emailAddress)).append("\n");
    sb.append("    employeeAccountId: ").append(toIndentedString(employeeAccountId)).append("\n");
    sb.append("    hotelCode: ").append(toIndentedString(hotelCode)).append("\n");
    sb.append("    hotelName: ").append(toIndentedString(hotelName)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    postalCode: ").append(toIndentedString(postalCode)).append("\n");
    sb.append("    purchaseOrder: ").append(toIndentedString(purchaseOrder)).append("\n");
    sb.append("    reservationId: ").append(toIndentedString(reservationId)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    telephone: ").append(toIndentedString(telephone)).append("\n");
    sb.append("    thirdPartyReference: ").append(toIndentedString(thirdPartyReference)).append("\n");
    sb.append("    upcomingDays: ").append(toIndentedString(upcomingDays)).append("\n");
    sb.append("    pastDays: ").append(toIndentedString(pastDays)).append("\n");
    sb.append("    cancelledDays: ").append(toIndentedString(cancelledDays)).append("\n");
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


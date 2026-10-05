package uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch;

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
 * CdhReservationSearchCriteriaDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:32.868523+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
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

  public CdhReservationSearchCriteriaDto arrivalDateFrom(String arrivalDateFrom) {
    this.arrivalDateFrom = arrivalDateFrom;
    return this;
  }

  /**
   * Get arrivalDateFrom
   * @return arrivalDateFrom
   */
  
  @Schema(name = "arrivalDateFrom", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("arrivalDateFrom")
  public String getArrivalDateFrom() {
    return arrivalDateFrom;
  }

  public void setArrivalDateFrom(String arrivalDateFrom) {
    this.arrivalDateFrom = arrivalDateFrom;
  }

  public CdhReservationSearchCriteriaDto arrivalDateTo(String arrivalDateTo) {
    this.arrivalDateTo = arrivalDateTo;
    return this;
  }

  /**
   * Get arrivalDateTo
   * @return arrivalDateTo
   */
  
  @Schema(name = "arrivalDateTo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("arrivalDateTo")
  public String getArrivalDateTo() {
    return arrivalDateTo;
  }

  public void setArrivalDateTo(String arrivalDateTo) {
    this.arrivalDateTo = arrivalDateTo;
  }

  public CdhReservationSearchCriteriaDto bartGuestHistoryNumber(String bartGuestHistoryNumber) {
    this.bartGuestHistoryNumber = bartGuestHistoryNumber;
    return this;
  }

  /**
   * Get bartGuestHistoryNumber
   * @return bartGuestHistoryNumber
   */
  
  @Schema(name = "bartGuestHistoryNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bartGuestHistoryNumber")
  public String getBartGuestHistoryNumber() {
    return bartGuestHistoryNumber;
  }

  public void setBartGuestHistoryNumber(String bartGuestHistoryNumber) {
    this.bartGuestHistoryNumber = bartGuestHistoryNumber;
  }

  public CdhReservationSearchCriteriaDto bookingDate(String bookingDate) {
    this.bookingDate = bookingDate;
    return this;
  }

  /**
   * Get bookingDate
   * @return bookingDate
   */
  
  @Schema(name = "bookingDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingDate")
  public String getBookingDate() {
    return bookingDate;
  }

  public void setBookingDate(String bookingDate) {
    this.bookingDate = bookingDate;
  }

  public CdhReservationSearchCriteriaDto bookingReference(String bookingReference) {
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

  public CdhReservationSearchCriteriaDto bookingsDatabaseSearch(Boolean bookingsDatabaseSearch) {
    this.bookingsDatabaseSearch = bookingsDatabaseSearch;
    return this;
  }

  /**
   * Get bookingsDatabaseSearch
   * @return bookingsDatabaseSearch
   */
  
  @Schema(name = "bookingsDatabaseSearch", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingsDatabaseSearch")
  public Boolean getBookingsDatabaseSearch() {
    return bookingsDatabaseSearch;
  }

  public void setBookingsDatabaseSearch(Boolean bookingsDatabaseSearch) {
    this.bookingsDatabaseSearch = bookingsDatabaseSearch;
  }

  public CdhReservationSearchCriteriaDto cancellationDate(String cancellationDate) {
    this.cancellationDate = cancellationDate;
    return this;
  }

  /**
   * Get cancellationDate
   * @return cancellationDate
   */
  
  @Schema(name = "cancellationDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cancellationDate")
  public String getCancellationDate() {
    return cancellationDate;
  }

  public void setCancellationDate(String cancellationDate) {
    this.cancellationDate = cancellationDate;
  }

  public CdhReservationSearchCriteriaDto companyAccountId(String companyAccountId) {
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

  public CdhReservationSearchCriteriaDto companyName(String companyName) {
    this.companyName = companyName;
    return this;
  }

  /**
   * Get companyName
   * @return companyName
   */
  
  @Schema(name = "companyName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyName")
  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public CdhReservationSearchCriteriaDto continuationToken(String continuationToken) {
    this.continuationToken = continuationToken;
    return this;
  }

  /**
   * Get continuationToken
   * @return continuationToken
   */
  
  @Schema(name = "continuationToken", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("continuationToken")
  public String getContinuationToken() {
    return continuationToken;
  }

  public void setContinuationToken(String continuationToken) {
    this.continuationToken = continuationToken;
  }

  public CdhReservationSearchCriteriaDto customerAccountId(String customerAccountId) {
    this.customerAccountId = customerAccountId;
    return this;
  }

  /**
   * Get customerAccountId
   * @return customerAccountId
   */
  
  @Schema(name = "customerAccountId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customerAccountId")
  public String getCustomerAccountId() {
    return customerAccountId;
  }

  public void setCustomerAccountId(String customerAccountId) {
    this.customerAccountId = customerAccountId;
  }

  public CdhReservationSearchCriteriaDto customerReference(String customerReference) {
    this.customerReference = customerReference;
    return this;
  }

  /**
   * Get customerReference
   * @return customerReference
   */
  
  @Schema(name = "customerReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customerReference")
  public String getCustomerReference() {
    return customerReference;
  }

  public void setCustomerReference(String customerReference) {
    this.customerReference = customerReference;
  }

  public CdhReservationSearchCriteriaDto emailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
    return this;
  }

  /**
   * Get emailAddress
   * @return emailAddress
   */
  
  @Schema(name = "emailAddress", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emailAddress")
  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public CdhReservationSearchCriteriaDto employeeAccountId(String employeeAccountId) {
    this.employeeAccountId = employeeAccountId;
    return this;
  }

  /**
   * Get employeeAccountId
   * @return employeeAccountId
   */
  
  @Schema(name = "employeeAccountId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("employeeAccountId")
  public String getEmployeeAccountId() {
    return employeeAccountId;
  }

  public void setEmployeeAccountId(String employeeAccountId) {
    this.employeeAccountId = employeeAccountId;
  }

  public CdhReservationSearchCriteriaDto hotelCode(String hotelCode) {
    this.hotelCode = hotelCode;
    return this;
  }

  /**
   * Get hotelCode
   * @return hotelCode
   */
  
  @Schema(name = "hotelCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelCode")
  public String getHotelCode() {
    return hotelCode;
  }

  public void setHotelCode(String hotelCode) {
    this.hotelCode = hotelCode;
  }

  public CdhReservationSearchCriteriaDto hotelName(String hotelName) {
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

  public CdhReservationSearchCriteriaDto lastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * Get lastName
   * @return lastName
   */
  
  @Schema(name = "lastName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastName")
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public CdhReservationSearchCriteriaDto pageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
    return this;
  }

  /**
   * Get pageNumber
   * @return pageNumber
   */
  
  @Schema(name = "pageNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageNumber")
  public Integer getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
  }

  public CdhReservationSearchCriteriaDto pageSize(Integer pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  /**
   * Get pageSize
   * @return pageSize
   */
  
  @Schema(name = "pageSize", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageSize")
  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public CdhReservationSearchCriteriaDto postalCode(String postalCode) {
    this.postalCode = postalCode;
    return this;
  }

  /**
   * Get postalCode
   * @return postalCode
   */
  
  @Schema(name = "postalCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("postalCode")
  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public CdhReservationSearchCriteriaDto purchaseOrder(String purchaseOrder) {
    this.purchaseOrder = purchaseOrder;
    return this;
  }

  /**
   * Get purchaseOrder
   * @return purchaseOrder
   */
  
  @Schema(name = "purchaseOrder", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("purchaseOrder")
  public String getPurchaseOrder() {
    return purchaseOrder;
  }

  public void setPurchaseOrder(String purchaseOrder) {
    this.purchaseOrder = purchaseOrder;
  }

  public CdhReservationSearchCriteriaDto reservationId(Integer reservationId) {
    this.reservationId = reservationId;
    return this;
  }

  /**
   * Get reservationId
   * @return reservationId
   */
  
  @Schema(name = "reservationId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationId")
  public Integer getReservationId() {
    return reservationId;
  }

  public void setReservationId(Integer reservationId) {
    this.reservationId = reservationId;
  }

  public CdhReservationSearchCriteriaDto status(String status) {
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

  public CdhReservationSearchCriteriaDto telephone(String telephone) {
    this.telephone = telephone;
    return this;
  }

  /**
   * Get telephone
   * @return telephone
   */
  
  @Schema(name = "telephone", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("telephone")
  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  public CdhReservationSearchCriteriaDto thirdPartyReference(String thirdPartyReference) {
    this.thirdPartyReference = thirdPartyReference;
    return this;
  }

  /**
   * Get thirdPartyReference
   * @return thirdPartyReference
   */
  
  @Schema(name = "thirdPartyReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("thirdPartyReference")
  public String getThirdPartyReference() {
    return thirdPartyReference;
  }

  public void setThirdPartyReference(String thirdPartyReference) {
    this.thirdPartyReference = thirdPartyReference;
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
        Objects.equals(this.thirdPartyReference, cdhReservationSearchCriteriaDto.thirdPartyReference);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrivalDateFrom, arrivalDateTo, bartGuestHistoryNumber, bookingDate, bookingReference, bookingsDatabaseSearch, cancellationDate, companyAccountId, companyName, continuationToken, customerAccountId, customerReference, emailAddress, employeeAccountId, hotelCode, hotelName, lastName, pageNumber, pageSize, postalCode, purchaseOrder, reservationId, status, telephone, thirdPartyReference);
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


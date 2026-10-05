package uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.AddressDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.BookingPreferenceDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.BusinessDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.EmployeeAnswersDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.PaymentPreferenceDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * GetEmployeeResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-09T08:37:59.335673+03:00[Europe/Bucharest]", comments = "Generator version: 7.14.0")
public class GetEmployeeResponseDto {

  private @Nullable String accessLevel;

  private @Nullable String activationKey;

  private @Nullable AddressDto address;

  private @Nullable String approxAnnualUKHotelSpend;

  private @Nullable Boolean awaitingApproval;

  private @Nullable String bartEmployeeId;

  private @Nullable String bartGuestHistoryCreation;

  private @Nullable String bartGuestHistoryNumber;

  private @Nullable BookingPreferenceDto bookingPreference;

  private @Nullable BusinessDto business;

  private @Nullable String carRegistration;

  private @Nullable String centralCardId;

  private @Nullable String centralCardIdString;

  private @Nullable String companyAccountId;

  private @Nullable String emailAddress;

  private @Nullable String employeeAccountId;

  private @Nullable EmployeeAnswersDto employeeAnswers;

  private @Nullable String employeeStatus;

  private @Nullable String firstName;

  private @Nullable String globalCompanyId;

  private @Nullable String lastName;

  private @Nullable String latestKeyCreatedDateTime;

  private @Nullable Boolean lockedForEditing;

  private @Nullable Boolean mainEmployee;

  private @Nullable String mobileNumber;

  private @Nullable PaymentPreferenceDto paymentPreference;

  private @Nullable String phoneNumber;

  private @Nullable String position;

  private @Nullable Boolean textConfirmation;

  private @Nullable String title;

  public GetEmployeeResponseDto accessLevel(@Nullable String accessLevel) {
    this.accessLevel = accessLevel;
    return this;
  }

  /**
   * Get accessLevel
   * @return accessLevel
   */
  
  @Schema(name = "accessLevel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accessLevel")
  public @Nullable String getAccessLevel() {
    return accessLevel;
  }

  public void setAccessLevel(@Nullable String accessLevel) {
    this.accessLevel = accessLevel;
  }

  public GetEmployeeResponseDto activationKey(@Nullable String activationKey) {
    this.activationKey = activationKey;
    return this;
  }

  /**
   * Get activationKey
   * @return activationKey
   */
  
  @Schema(name = "activationKey", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("activationKey")
  public @Nullable String getActivationKey() {
    return activationKey;
  }

  public void setActivationKey(@Nullable String activationKey) {
    this.activationKey = activationKey;
  }

  public GetEmployeeResponseDto address(@Nullable AddressDto address) {
    this.address = address;
    return this;
  }

  /**
   * Get address
   * @return address
   */
  @Valid 
  @Schema(name = "address", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("address")
  public @Nullable AddressDto getAddress() {
    return address;
  }

  public void setAddress(@Nullable AddressDto address) {
    this.address = address;
  }

  public GetEmployeeResponseDto approxAnnualUKHotelSpend(@Nullable String approxAnnualUKHotelSpend) {
    this.approxAnnualUKHotelSpend = approxAnnualUKHotelSpend;
    return this;
  }

  /**
   * Get approxAnnualUKHotelSpend
   * @return approxAnnualUKHotelSpend
   */
  
  @Schema(name = "approxAnnualUKHotelSpend", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("approxAnnualUKHotelSpend")
  public @Nullable String getApproxAnnualUKHotelSpend() {
    return approxAnnualUKHotelSpend;
  }

  public void setApproxAnnualUKHotelSpend(@Nullable String approxAnnualUKHotelSpend) {
    this.approxAnnualUKHotelSpend = approxAnnualUKHotelSpend;
  }

  public GetEmployeeResponseDto awaitingApproval(@Nullable Boolean awaitingApproval) {
    this.awaitingApproval = awaitingApproval;
    return this;
  }

  /**
   * Get awaitingApproval
   * @return awaitingApproval
   */
  
  @Schema(name = "awaitingApproval", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("awaitingApproval")
  public @Nullable Boolean getAwaitingApproval() {
    return awaitingApproval;
  }

  public void setAwaitingApproval(@Nullable Boolean awaitingApproval) {
    this.awaitingApproval = awaitingApproval;
  }

  public GetEmployeeResponseDto bartEmployeeId(@Nullable String bartEmployeeId) {
    this.bartEmployeeId = bartEmployeeId;
    return this;
  }

  /**
   * Get bartEmployeeId
   * @return bartEmployeeId
   */
  
  @Schema(name = "bartEmployeeId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bartEmployeeId")
  public @Nullable String getBartEmployeeId() {
    return bartEmployeeId;
  }

  public void setBartEmployeeId(@Nullable String bartEmployeeId) {
    this.bartEmployeeId = bartEmployeeId;
  }

  public GetEmployeeResponseDto bartGuestHistoryCreation(@Nullable String bartGuestHistoryCreation) {
    this.bartGuestHistoryCreation = bartGuestHistoryCreation;
    return this;
  }

  /**
   * Get bartGuestHistoryCreation
   * @return bartGuestHistoryCreation
   */
  
  @Schema(name = "bartGuestHistoryCreation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bartGuestHistoryCreation")
  public @Nullable String getBartGuestHistoryCreation() {
    return bartGuestHistoryCreation;
  }

  public void setBartGuestHistoryCreation(@Nullable String bartGuestHistoryCreation) {
    this.bartGuestHistoryCreation = bartGuestHistoryCreation;
  }

  public GetEmployeeResponseDto bartGuestHistoryNumber(@Nullable String bartGuestHistoryNumber) {
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

  public GetEmployeeResponseDto bookingPreference(@Nullable BookingPreferenceDto bookingPreference) {
    this.bookingPreference = bookingPreference;
    return this;
  }

  /**
   * Get bookingPreference
   * @return bookingPreference
   */
  @Valid 
  @Schema(name = "bookingPreference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingPreference")
  public @Nullable BookingPreferenceDto getBookingPreference() {
    return bookingPreference;
  }

  public void setBookingPreference(@Nullable BookingPreferenceDto bookingPreference) {
    this.bookingPreference = bookingPreference;
  }

  public GetEmployeeResponseDto business(@Nullable BusinessDto business) {
    this.business = business;
    return this;
  }

  /**
   * Get business
   * @return business
   */
  @Valid 
  @Schema(name = "business", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("business")
  public @Nullable BusinessDto getBusiness() {
    return business;
  }

  public void setBusiness(@Nullable BusinessDto business) {
    this.business = business;
  }

  public GetEmployeeResponseDto carRegistration(@Nullable String carRegistration) {
    this.carRegistration = carRegistration;
    return this;
  }

  /**
   * Get carRegistration
   * @return carRegistration
   */
  
  @Schema(name = "carRegistration", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("carRegistration")
  public @Nullable String getCarRegistration() {
    return carRegistration;
  }

  public void setCarRegistration(@Nullable String carRegistration) {
    this.carRegistration = carRegistration;
  }

  public GetEmployeeResponseDto centralCardId(@Nullable String centralCardId) {
    this.centralCardId = centralCardId;
    return this;
  }

  /**
   * Get centralCardId
   * @return centralCardId
   */
  
  @Schema(name = "centralCardId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("centralCardId")
  public @Nullable String getCentralCardId() {
    return centralCardId;
  }

  public void setCentralCardId(@Nullable String centralCardId) {
    this.centralCardId = centralCardId;
  }

  public GetEmployeeResponseDto centralCardIdString(@Nullable String centralCardIdString) {
    this.centralCardIdString = centralCardIdString;
    return this;
  }

  /**
   * Get centralCardIdString
   * @return centralCardIdString
   */
  
  @Schema(name = "centralCardIdString", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("centralCardIdString")
  public @Nullable String getCentralCardIdString() {
    return centralCardIdString;
  }

  public void setCentralCardIdString(@Nullable String centralCardIdString) {
    this.centralCardIdString = centralCardIdString;
  }

  public GetEmployeeResponseDto companyAccountId(@Nullable String companyAccountId) {
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

  public GetEmployeeResponseDto emailAddress(@Nullable String emailAddress) {
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

  public GetEmployeeResponseDto employeeAccountId(@Nullable String employeeAccountId) {
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

  public GetEmployeeResponseDto employeeAnswers(@Nullable EmployeeAnswersDto employeeAnswers) {
    this.employeeAnswers = employeeAnswers;
    return this;
  }

  /**
   * Get employeeAnswers
   * @return employeeAnswers
   */
  @Valid 
  @Schema(name = "employeeAnswers", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("employeeAnswers")
  public @Nullable EmployeeAnswersDto getEmployeeAnswers() {
    return employeeAnswers;
  }

  public void setEmployeeAnswers(@Nullable EmployeeAnswersDto employeeAnswers) {
    this.employeeAnswers = employeeAnswers;
  }

  public GetEmployeeResponseDto employeeStatus(@Nullable String employeeStatus) {
    this.employeeStatus = employeeStatus;
    return this;
  }

  /**
   * Get employeeStatus
   * @return employeeStatus
   */
  
  @Schema(name = "employeeStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("employeeStatus")
  public @Nullable String getEmployeeStatus() {
    return employeeStatus;
  }

  public void setEmployeeStatus(@Nullable String employeeStatus) {
    this.employeeStatus = employeeStatus;
  }

  public GetEmployeeResponseDto firstName(@Nullable String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * Get firstName
   * @return firstName
   */
  
  @Schema(name = "firstName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("firstName")
  public @Nullable String getFirstName() {
    return firstName;
  }

  public void setFirstName(@Nullable String firstName) {
    this.firstName = firstName;
  }

  public GetEmployeeResponseDto globalCompanyId(@Nullable String globalCompanyId) {
    this.globalCompanyId = globalCompanyId;
    return this;
  }

  /**
   * Get globalCompanyId
   * @return globalCompanyId
   */
  
  @Schema(name = "globalCompanyId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("globalCompanyId")
  public @Nullable String getGlobalCompanyId() {
    return globalCompanyId;
  }

  public void setGlobalCompanyId(@Nullable String globalCompanyId) {
    this.globalCompanyId = globalCompanyId;
  }

  public GetEmployeeResponseDto lastName(@Nullable String lastName) {
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

  public GetEmployeeResponseDto latestKeyCreatedDateTime(@Nullable String latestKeyCreatedDateTime) {
    this.latestKeyCreatedDateTime = latestKeyCreatedDateTime;
    return this;
  }

  /**
   * Get latestKeyCreatedDateTime
   * @return latestKeyCreatedDateTime
   */
  
  @Schema(name = "latestKeyCreatedDateTime", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("latestKeyCreatedDateTime")
  public @Nullable String getLatestKeyCreatedDateTime() {
    return latestKeyCreatedDateTime;
  }

  public void setLatestKeyCreatedDateTime(@Nullable String latestKeyCreatedDateTime) {
    this.latestKeyCreatedDateTime = latestKeyCreatedDateTime;
  }

  public GetEmployeeResponseDto lockedForEditing(@Nullable Boolean lockedForEditing) {
    this.lockedForEditing = lockedForEditing;
    return this;
  }

  /**
   * Get lockedForEditing
   * @return lockedForEditing
   */
  
  @Schema(name = "lockedForEditing", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lockedForEditing")
  public @Nullable Boolean getLockedForEditing() {
    return lockedForEditing;
  }

  public void setLockedForEditing(@Nullable Boolean lockedForEditing) {
    this.lockedForEditing = lockedForEditing;
  }

  public GetEmployeeResponseDto mainEmployee(@Nullable Boolean mainEmployee) {
    this.mainEmployee = mainEmployee;
    return this;
  }

  /**
   * Get mainEmployee
   * @return mainEmployee
   */
  
  @Schema(name = "mainEmployee", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mainEmployee")
  public @Nullable Boolean getMainEmployee() {
    return mainEmployee;
  }

  public void setMainEmployee(@Nullable Boolean mainEmployee) {
    this.mainEmployee = mainEmployee;
  }

  public GetEmployeeResponseDto mobileNumber(@Nullable String mobileNumber) {
    this.mobileNumber = mobileNumber;
    return this;
  }

  /**
   * Get mobileNumber
   * @return mobileNumber
   */
  
  @Schema(name = "mobileNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mobileNumber")
  public @Nullable String getMobileNumber() {
    return mobileNumber;
  }

  public void setMobileNumber(@Nullable String mobileNumber) {
    this.mobileNumber = mobileNumber;
  }

  public GetEmployeeResponseDto paymentPreference(@Nullable PaymentPreferenceDto paymentPreference) {
    this.paymentPreference = paymentPreference;
    return this;
  }

  /**
   * Get paymentPreference
   * @return paymentPreference
   */
  @Valid 
  @Schema(name = "paymentPreference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentPreference")
  public @Nullable PaymentPreferenceDto getPaymentPreference() {
    return paymentPreference;
  }

  public void setPaymentPreference(@Nullable PaymentPreferenceDto paymentPreference) {
    this.paymentPreference = paymentPreference;
  }

  public GetEmployeeResponseDto phoneNumber(@Nullable String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  /**
   * Get phoneNumber
   * @return phoneNumber
   */
  
  @Schema(name = "phoneNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("phoneNumber")
  public @Nullable String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(@Nullable String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public GetEmployeeResponseDto position(@Nullable String position) {
    this.position = position;
    return this;
  }

  /**
   * Get position
   * @return position
   */
  
  @Schema(name = "position", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("position")
  public @Nullable String getPosition() {
    return position;
  }

  public void setPosition(@Nullable String position) {
    this.position = position;
  }

  public GetEmployeeResponseDto textConfirmation(@Nullable Boolean textConfirmation) {
    this.textConfirmation = textConfirmation;
    return this;
  }

  /**
   * Get textConfirmation
   * @return textConfirmation
   */
  
  @Schema(name = "textConfirmation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("textConfirmation")
  public @Nullable Boolean getTextConfirmation() {
    return textConfirmation;
  }

  public void setTextConfirmation(@Nullable Boolean textConfirmation) {
    this.textConfirmation = textConfirmation;
  }

  public GetEmployeeResponseDto title(@Nullable String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
   */
  
  @Schema(name = "title", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public @Nullable String getTitle() {
    return title;
  }

  public void setTitle(@Nullable String title) {
    this.title = title;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GetEmployeeResponseDto getEmployeeResponseDto = (GetEmployeeResponseDto) o;
    return Objects.equals(this.accessLevel, getEmployeeResponseDto.accessLevel) &&
        Objects.equals(this.activationKey, getEmployeeResponseDto.activationKey) &&
        Objects.equals(this.address, getEmployeeResponseDto.address) &&
        Objects.equals(this.approxAnnualUKHotelSpend, getEmployeeResponseDto.approxAnnualUKHotelSpend) &&
        Objects.equals(this.awaitingApproval, getEmployeeResponseDto.awaitingApproval) &&
        Objects.equals(this.bartEmployeeId, getEmployeeResponseDto.bartEmployeeId) &&
        Objects.equals(this.bartGuestHistoryCreation, getEmployeeResponseDto.bartGuestHistoryCreation) &&
        Objects.equals(this.bartGuestHistoryNumber, getEmployeeResponseDto.bartGuestHistoryNumber) &&
        Objects.equals(this.bookingPreference, getEmployeeResponseDto.bookingPreference) &&
        Objects.equals(this.business, getEmployeeResponseDto.business) &&
        Objects.equals(this.carRegistration, getEmployeeResponseDto.carRegistration) &&
        Objects.equals(this.centralCardId, getEmployeeResponseDto.centralCardId) &&
        Objects.equals(this.centralCardIdString, getEmployeeResponseDto.centralCardIdString) &&
        Objects.equals(this.companyAccountId, getEmployeeResponseDto.companyAccountId) &&
        Objects.equals(this.emailAddress, getEmployeeResponseDto.emailAddress) &&
        Objects.equals(this.employeeAccountId, getEmployeeResponseDto.employeeAccountId) &&
        Objects.equals(this.employeeAnswers, getEmployeeResponseDto.employeeAnswers) &&
        Objects.equals(this.employeeStatus, getEmployeeResponseDto.employeeStatus) &&
        Objects.equals(this.firstName, getEmployeeResponseDto.firstName) &&
        Objects.equals(this.globalCompanyId, getEmployeeResponseDto.globalCompanyId) &&
        Objects.equals(this.lastName, getEmployeeResponseDto.lastName) &&
        Objects.equals(this.latestKeyCreatedDateTime, getEmployeeResponseDto.latestKeyCreatedDateTime) &&
        Objects.equals(this.lockedForEditing, getEmployeeResponseDto.lockedForEditing) &&
        Objects.equals(this.mainEmployee, getEmployeeResponseDto.mainEmployee) &&
        Objects.equals(this.mobileNumber, getEmployeeResponseDto.mobileNumber) &&
        Objects.equals(this.paymentPreference, getEmployeeResponseDto.paymentPreference) &&
        Objects.equals(this.phoneNumber, getEmployeeResponseDto.phoneNumber) &&
        Objects.equals(this.position, getEmployeeResponseDto.position) &&
        Objects.equals(this.textConfirmation, getEmployeeResponseDto.textConfirmation) &&
        Objects.equals(this.title, getEmployeeResponseDto.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessLevel, activationKey, address, approxAnnualUKHotelSpend, awaitingApproval, bartEmployeeId, bartGuestHistoryCreation, bartGuestHistoryNumber, bookingPreference, business, carRegistration, centralCardId, centralCardIdString, companyAccountId, emailAddress, employeeAccountId, employeeAnswers, employeeStatus, firstName, globalCompanyId, lastName, latestKeyCreatedDateTime, lockedForEditing, mainEmployee, mobileNumber, paymentPreference, phoneNumber, position, textConfirmation, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetEmployeeResponseDto {\n");
    sb.append("    accessLevel: ").append(toIndentedString(accessLevel)).append("\n");
    sb.append("    activationKey: ").append(toIndentedString(activationKey)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    approxAnnualUKHotelSpend: ").append(toIndentedString(approxAnnualUKHotelSpend)).append("\n");
    sb.append("    awaitingApproval: ").append(toIndentedString(awaitingApproval)).append("\n");
    sb.append("    bartEmployeeId: ").append(toIndentedString(bartEmployeeId)).append("\n");
    sb.append("    bartGuestHistoryCreation: ").append(toIndentedString(bartGuestHistoryCreation)).append("\n");
    sb.append("    bartGuestHistoryNumber: ").append(toIndentedString(bartGuestHistoryNumber)).append("\n");
    sb.append("    bookingPreference: ").append(toIndentedString(bookingPreference)).append("\n");
    sb.append("    business: ").append(toIndentedString(business)).append("\n");
    sb.append("    carRegistration: ").append(toIndentedString(carRegistration)).append("\n");
    sb.append("    centralCardId: ").append(toIndentedString(centralCardId)).append("\n");
    sb.append("    centralCardIdString: ").append(toIndentedString(centralCardIdString)).append("\n");
    sb.append("    companyAccountId: ").append(toIndentedString(companyAccountId)).append("\n");
    sb.append("    emailAddress: ").append(toIndentedString(emailAddress)).append("\n");
    sb.append("    employeeAccountId: ").append(toIndentedString(employeeAccountId)).append("\n");
    sb.append("    employeeAnswers: ").append(toIndentedString(employeeAnswers)).append("\n");
    sb.append("    employeeStatus: ").append(toIndentedString(employeeStatus)).append("\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    globalCompanyId: ").append(toIndentedString(globalCompanyId)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    latestKeyCreatedDateTime: ").append(toIndentedString(latestKeyCreatedDateTime)).append("\n");
    sb.append("    lockedForEditing: ").append(toIndentedString(lockedForEditing)).append("\n");
    sb.append("    mainEmployee: ").append(toIndentedString(mainEmployee)).append("\n");
    sb.append("    mobileNumber: ").append(toIndentedString(mobileNumber)).append("\n");
    sb.append("    paymentPreference: ").append(toIndentedString(paymentPreference)).append("\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
    sb.append("    position: ").append(toIndentedString(position)).append("\n");
    sb.append("    textConfirmation: ").append(toIndentedString(textConfirmation)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
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


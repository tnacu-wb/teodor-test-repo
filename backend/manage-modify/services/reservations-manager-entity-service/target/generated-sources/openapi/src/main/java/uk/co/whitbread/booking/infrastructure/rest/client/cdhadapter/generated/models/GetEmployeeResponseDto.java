package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.AddressDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.BookingPreferenceDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.BusinessDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.EmployeeAnswersDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.PaymentPreferenceDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * GetEmployeeResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:45.778333+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
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

  public GetEmployeeResponseDto accessLevel(String accessLevel) {
    this.accessLevel = accessLevel;
    return this;
  }

  /**
   * Get accessLevel
   * @return accessLevel
   */
  
  @Schema(name = "accessLevel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accessLevel")
  public String getAccessLevel() {
    return accessLevel;
  }

  public void setAccessLevel(String accessLevel) {
    this.accessLevel = accessLevel;
  }

  public GetEmployeeResponseDto activationKey(String activationKey) {
    this.activationKey = activationKey;
    return this;
  }

  /**
   * Get activationKey
   * @return activationKey
   */
  
  @Schema(name = "activationKey", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("activationKey")
  public String getActivationKey() {
    return activationKey;
  }

  public void setActivationKey(String activationKey) {
    this.activationKey = activationKey;
  }

  public GetEmployeeResponseDto address(AddressDto address) {
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
  public AddressDto getAddress() {
    return address;
  }

  public void setAddress(AddressDto address) {
    this.address = address;
  }

  public GetEmployeeResponseDto approxAnnualUKHotelSpend(String approxAnnualUKHotelSpend) {
    this.approxAnnualUKHotelSpend = approxAnnualUKHotelSpend;
    return this;
  }

  /**
   * Get approxAnnualUKHotelSpend
   * @return approxAnnualUKHotelSpend
   */
  
  @Schema(name = "approxAnnualUKHotelSpend", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("approxAnnualUKHotelSpend")
  public String getApproxAnnualUKHotelSpend() {
    return approxAnnualUKHotelSpend;
  }

  public void setApproxAnnualUKHotelSpend(String approxAnnualUKHotelSpend) {
    this.approxAnnualUKHotelSpend = approxAnnualUKHotelSpend;
  }

  public GetEmployeeResponseDto awaitingApproval(Boolean awaitingApproval) {
    this.awaitingApproval = awaitingApproval;
    return this;
  }

  /**
   * Get awaitingApproval
   * @return awaitingApproval
   */
  
  @Schema(name = "awaitingApproval", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("awaitingApproval")
  public Boolean getAwaitingApproval() {
    return awaitingApproval;
  }

  public void setAwaitingApproval(Boolean awaitingApproval) {
    this.awaitingApproval = awaitingApproval;
  }

  public GetEmployeeResponseDto bartEmployeeId(String bartEmployeeId) {
    this.bartEmployeeId = bartEmployeeId;
    return this;
  }

  /**
   * Get bartEmployeeId
   * @return bartEmployeeId
   */
  
  @Schema(name = "bartEmployeeId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bartEmployeeId")
  public String getBartEmployeeId() {
    return bartEmployeeId;
  }

  public void setBartEmployeeId(String bartEmployeeId) {
    this.bartEmployeeId = bartEmployeeId;
  }

  public GetEmployeeResponseDto bartGuestHistoryCreation(String bartGuestHistoryCreation) {
    this.bartGuestHistoryCreation = bartGuestHistoryCreation;
    return this;
  }

  /**
   * Get bartGuestHistoryCreation
   * @return bartGuestHistoryCreation
   */
  
  @Schema(name = "bartGuestHistoryCreation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bartGuestHistoryCreation")
  public String getBartGuestHistoryCreation() {
    return bartGuestHistoryCreation;
  }

  public void setBartGuestHistoryCreation(String bartGuestHistoryCreation) {
    this.bartGuestHistoryCreation = bartGuestHistoryCreation;
  }

  public GetEmployeeResponseDto bartGuestHistoryNumber(String bartGuestHistoryNumber) {
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

  public GetEmployeeResponseDto bookingPreference(BookingPreferenceDto bookingPreference) {
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
  public BookingPreferenceDto getBookingPreference() {
    return bookingPreference;
  }

  public void setBookingPreference(BookingPreferenceDto bookingPreference) {
    this.bookingPreference = bookingPreference;
  }

  public GetEmployeeResponseDto business(BusinessDto business) {
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
  public BusinessDto getBusiness() {
    return business;
  }

  public void setBusiness(BusinessDto business) {
    this.business = business;
  }

  public GetEmployeeResponseDto carRegistration(String carRegistration) {
    this.carRegistration = carRegistration;
    return this;
  }

  /**
   * Get carRegistration
   * @return carRegistration
   */
  
  @Schema(name = "carRegistration", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("carRegistration")
  public String getCarRegistration() {
    return carRegistration;
  }

  public void setCarRegistration(String carRegistration) {
    this.carRegistration = carRegistration;
  }

  public GetEmployeeResponseDto centralCardId(String centralCardId) {
    this.centralCardId = centralCardId;
    return this;
  }

  /**
   * Get centralCardId
   * @return centralCardId
   */
  
  @Schema(name = "centralCardId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("centralCardId")
  public String getCentralCardId() {
    return centralCardId;
  }

  public void setCentralCardId(String centralCardId) {
    this.centralCardId = centralCardId;
  }

  public GetEmployeeResponseDto centralCardIdString(String centralCardIdString) {
    this.centralCardIdString = centralCardIdString;
    return this;
  }

  /**
   * Get centralCardIdString
   * @return centralCardIdString
   */
  
  @Schema(name = "centralCardIdString", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("centralCardIdString")
  public String getCentralCardIdString() {
    return centralCardIdString;
  }

  public void setCentralCardIdString(String centralCardIdString) {
    this.centralCardIdString = centralCardIdString;
  }

  public GetEmployeeResponseDto companyAccountId(String companyAccountId) {
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

  public GetEmployeeResponseDto emailAddress(String emailAddress) {
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

  public GetEmployeeResponseDto employeeAccountId(String employeeAccountId) {
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

  public GetEmployeeResponseDto employeeAnswers(EmployeeAnswersDto employeeAnswers) {
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
  public EmployeeAnswersDto getEmployeeAnswers() {
    return employeeAnswers;
  }

  public void setEmployeeAnswers(EmployeeAnswersDto employeeAnswers) {
    this.employeeAnswers = employeeAnswers;
  }

  public GetEmployeeResponseDto employeeStatus(String employeeStatus) {
    this.employeeStatus = employeeStatus;
    return this;
  }

  /**
   * Get employeeStatus
   * @return employeeStatus
   */
  
  @Schema(name = "employeeStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("employeeStatus")
  public String getEmployeeStatus() {
    return employeeStatus;
  }

  public void setEmployeeStatus(String employeeStatus) {
    this.employeeStatus = employeeStatus;
  }

  public GetEmployeeResponseDto firstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * Get firstName
   * @return firstName
   */
  
  @Schema(name = "firstName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("firstName")
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public GetEmployeeResponseDto globalCompanyId(String globalCompanyId) {
    this.globalCompanyId = globalCompanyId;
    return this;
  }

  /**
   * Get globalCompanyId
   * @return globalCompanyId
   */
  
  @Schema(name = "globalCompanyId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("globalCompanyId")
  public String getGlobalCompanyId() {
    return globalCompanyId;
  }

  public void setGlobalCompanyId(String globalCompanyId) {
    this.globalCompanyId = globalCompanyId;
  }

  public GetEmployeeResponseDto lastName(String lastName) {
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

  public GetEmployeeResponseDto latestKeyCreatedDateTime(String latestKeyCreatedDateTime) {
    this.latestKeyCreatedDateTime = latestKeyCreatedDateTime;
    return this;
  }

  /**
   * Get latestKeyCreatedDateTime
   * @return latestKeyCreatedDateTime
   */
  
  @Schema(name = "latestKeyCreatedDateTime", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("latestKeyCreatedDateTime")
  public String getLatestKeyCreatedDateTime() {
    return latestKeyCreatedDateTime;
  }

  public void setLatestKeyCreatedDateTime(String latestKeyCreatedDateTime) {
    this.latestKeyCreatedDateTime = latestKeyCreatedDateTime;
  }

  public GetEmployeeResponseDto lockedForEditing(Boolean lockedForEditing) {
    this.lockedForEditing = lockedForEditing;
    return this;
  }

  /**
   * Get lockedForEditing
   * @return lockedForEditing
   */
  
  @Schema(name = "lockedForEditing", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lockedForEditing")
  public Boolean getLockedForEditing() {
    return lockedForEditing;
  }

  public void setLockedForEditing(Boolean lockedForEditing) {
    this.lockedForEditing = lockedForEditing;
  }

  public GetEmployeeResponseDto mainEmployee(Boolean mainEmployee) {
    this.mainEmployee = mainEmployee;
    return this;
  }

  /**
   * Get mainEmployee
   * @return mainEmployee
   */
  
  @Schema(name = "mainEmployee", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mainEmployee")
  public Boolean getMainEmployee() {
    return mainEmployee;
  }

  public void setMainEmployee(Boolean mainEmployee) {
    this.mainEmployee = mainEmployee;
  }

  public GetEmployeeResponseDto mobileNumber(String mobileNumber) {
    this.mobileNumber = mobileNumber;
    return this;
  }

  /**
   * Get mobileNumber
   * @return mobileNumber
   */
  
  @Schema(name = "mobileNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mobileNumber")
  public String getMobileNumber() {
    return mobileNumber;
  }

  public void setMobileNumber(String mobileNumber) {
    this.mobileNumber = mobileNumber;
  }

  public GetEmployeeResponseDto paymentPreference(PaymentPreferenceDto paymentPreference) {
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
  public PaymentPreferenceDto getPaymentPreference() {
    return paymentPreference;
  }

  public void setPaymentPreference(PaymentPreferenceDto paymentPreference) {
    this.paymentPreference = paymentPreference;
  }

  public GetEmployeeResponseDto phoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  /**
   * Get phoneNumber
   * @return phoneNumber
   */
  
  @Schema(name = "phoneNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("phoneNumber")
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public GetEmployeeResponseDto position(String position) {
    this.position = position;
    return this;
  }

  /**
   * Get position
   * @return position
   */
  
  @Schema(name = "position", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("position")
  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public GetEmployeeResponseDto textConfirmation(Boolean textConfirmation) {
    this.textConfirmation = textConfirmation;
    return this;
  }

  /**
   * Get textConfirmation
   * @return textConfirmation
   */
  
  @Schema(name = "textConfirmation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("textConfirmation")
  public Boolean getTextConfirmation() {
    return textConfirmation;
  }

  public void setTextConfirmation(Boolean textConfirmation) {
    this.textConfirmation = textConfirmation;
  }

  public GetEmployeeResponseDto title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
   */
  
  @Schema(name = "title", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
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


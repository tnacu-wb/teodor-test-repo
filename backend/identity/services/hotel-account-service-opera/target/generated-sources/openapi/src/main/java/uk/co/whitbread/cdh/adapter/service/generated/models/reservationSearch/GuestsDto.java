package uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.AddressDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * GuestsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-09T08:37:59.335673+03:00[Europe/Bucharest]", comments = "Generator version: 7.14.0")
public class GuestsDto {

  private @Nullable AddressDto address;

  private @Nullable String bartEmployeeId;

  private @Nullable String bartGuestHistoryNumber;

  private @Nullable String companyName;

  private @Nullable String emailAddress;

  private @Nullable String employeeAccountId;

  private @Nullable String firstName;

  private @Nullable String globalCompanyId;

  private @Nullable String initials;

  private @Nullable String lastName;

  private @Nullable Boolean leadGuest;

  private @Nullable String mobileNumber;

  private @Nullable String nationality;

  private @Nullable String telephoneNumber;

  private @Nullable String testName;

  private @Nullable String title;

  public GuestsDto address(@Nullable AddressDto address) {
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

  public GuestsDto bartEmployeeId(@Nullable String bartEmployeeId) {
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

  public GuestsDto bartGuestHistoryNumber(@Nullable String bartGuestHistoryNumber) {
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

  public GuestsDto companyName(@Nullable String companyName) {
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

  public GuestsDto emailAddress(@Nullable String emailAddress) {
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

  public GuestsDto employeeAccountId(@Nullable String employeeAccountId) {
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

  public GuestsDto firstName(@Nullable String firstName) {
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

  public GuestsDto globalCompanyId(@Nullable String globalCompanyId) {
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

  public GuestsDto initials(@Nullable String initials) {
    this.initials = initials;
    return this;
  }

  /**
   * Get initials
   * @return initials
   */
  
  @Schema(name = "initials", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("initials")
  public @Nullable String getInitials() {
    return initials;
  }

  public void setInitials(@Nullable String initials) {
    this.initials = initials;
  }

  public GuestsDto lastName(@Nullable String lastName) {
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

  public GuestsDto leadGuest(@Nullable Boolean leadGuest) {
    this.leadGuest = leadGuest;
    return this;
  }

  /**
   * Get leadGuest
   * @return leadGuest
   */
  
  @Schema(name = "leadGuest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("leadGuest")
  public @Nullable Boolean getLeadGuest() {
    return leadGuest;
  }

  public void setLeadGuest(@Nullable Boolean leadGuest) {
    this.leadGuest = leadGuest;
  }

  public GuestsDto mobileNumber(@Nullable String mobileNumber) {
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

  public GuestsDto nationality(@Nullable String nationality) {
    this.nationality = nationality;
    return this;
  }

  /**
   * Get nationality
   * @return nationality
   */
  
  @Schema(name = "nationality", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nationality")
  public @Nullable String getNationality() {
    return nationality;
  }

  public void setNationality(@Nullable String nationality) {
    this.nationality = nationality;
  }

  public GuestsDto telephoneNumber(@Nullable String telephoneNumber) {
    this.telephoneNumber = telephoneNumber;
    return this;
  }

  /**
   * Get telephoneNumber
   * @return telephoneNumber
   */
  
  @Schema(name = "telephoneNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("telephoneNumber")
  public @Nullable String getTelephoneNumber() {
    return telephoneNumber;
  }

  public void setTelephoneNumber(@Nullable String telephoneNumber) {
    this.telephoneNumber = telephoneNumber;
  }

  public GuestsDto testName(@Nullable String testName) {
    this.testName = testName;
    return this;
  }

  /**
   * Get testName
   * @return testName
   */
  
  @Schema(name = "testName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("testName")
  public @Nullable String getTestName() {
    return testName;
  }

  public void setTestName(@Nullable String testName) {
    this.testName = testName;
  }

  public GuestsDto title(@Nullable String title) {
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
    GuestsDto guestsDto = (GuestsDto) o;
    return Objects.equals(this.address, guestsDto.address) &&
        Objects.equals(this.bartEmployeeId, guestsDto.bartEmployeeId) &&
        Objects.equals(this.bartGuestHistoryNumber, guestsDto.bartGuestHistoryNumber) &&
        Objects.equals(this.companyName, guestsDto.companyName) &&
        Objects.equals(this.emailAddress, guestsDto.emailAddress) &&
        Objects.equals(this.employeeAccountId, guestsDto.employeeAccountId) &&
        Objects.equals(this.firstName, guestsDto.firstName) &&
        Objects.equals(this.globalCompanyId, guestsDto.globalCompanyId) &&
        Objects.equals(this.initials, guestsDto.initials) &&
        Objects.equals(this.lastName, guestsDto.lastName) &&
        Objects.equals(this.leadGuest, guestsDto.leadGuest) &&
        Objects.equals(this.mobileNumber, guestsDto.mobileNumber) &&
        Objects.equals(this.nationality, guestsDto.nationality) &&
        Objects.equals(this.telephoneNumber, guestsDto.telephoneNumber) &&
        Objects.equals(this.testName, guestsDto.testName) &&
        Objects.equals(this.title, guestsDto.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(address, bartEmployeeId, bartGuestHistoryNumber, companyName, emailAddress, employeeAccountId, firstName, globalCompanyId, initials, lastName, leadGuest, mobileNumber, nationality, telephoneNumber, testName, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GuestsDto {\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    bartEmployeeId: ").append(toIndentedString(bartEmployeeId)).append("\n");
    sb.append("    bartGuestHistoryNumber: ").append(toIndentedString(bartGuestHistoryNumber)).append("\n");
    sb.append("    companyName: ").append(toIndentedString(companyName)).append("\n");
    sb.append("    emailAddress: ").append(toIndentedString(emailAddress)).append("\n");
    sb.append("    employeeAccountId: ").append(toIndentedString(employeeAccountId)).append("\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    globalCompanyId: ").append(toIndentedString(globalCompanyId)).append("\n");
    sb.append("    initials: ").append(toIndentedString(initials)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    leadGuest: ").append(toIndentedString(leadGuest)).append("\n");
    sb.append("    mobileNumber: ").append(toIndentedString(mobileNumber)).append("\n");
    sb.append("    nationality: ").append(toIndentedString(nationality)).append("\n");
    sb.append("    telephoneNumber: ").append(toIndentedString(telephoneNumber)).append("\n");
    sb.append("    testName: ").append(toIndentedString(testName)).append("\n");
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


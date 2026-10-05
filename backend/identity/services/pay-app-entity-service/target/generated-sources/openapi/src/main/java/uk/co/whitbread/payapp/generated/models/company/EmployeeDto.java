package uk.co.whitbread.payapp.generated.models.company;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import uk.co.whitbread.payapp.generated.models.company.AddressDto;
import uk.co.whitbread.payapp.generated.models.company.EmployeeAnswersDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * EmployeeDto
 */

@JsonTypeName("Employee")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:38.523560+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class EmployeeDto {

  /**
   * Gets or Sets accessLevel
   */
  public enum AccessLevelEnum {
    STAYER("STAYER"),
    
    SELF("SELF"),
    
    BOOKER("BOOKER"),
    
    SUPER("SUPER");

    private String value;

    AccessLevelEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static AccessLevelEnum fromValue(String value) {
      for (AccessLevelEnum b : AccessLevelEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable AccessLevelEnum accessLevel;

  private @Nullable AddressDto address;

  private @Nullable String centralCardId;

  private @Nullable String dialingCode;

  private String emailAddress;

  private @Nullable EmployeeAnswersDto employeeAnswers;

  /**
   * Gets or Sets employeeStatus
   */
  public enum EmployeeStatusEnum {
    ACTIVE("ACTIVE"),
    
    INACTIVE("INACTIVE"),
    
    SUSPENDED("SUSPENDED"),
    
    DEACTIVATED("DEACTIVATED"),
    
    PURGED("PURGED");

    private String value;

    EmployeeStatusEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static EmployeeStatusEnum fromValue(String value) {
      for (EmployeeStatusEnum b : EmployeeStatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable EmployeeStatusEnum employeeStatus;

  private String firstName;

  private @Nullable String ghNumber;

  private @Nullable String id;

  private String lastName;

  private String mobileNumber;

  private String phoneNumber;

  private String position;

  private @Nullable Boolean textConfirmation;

  private String title;

  public EmployeeDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EmployeeDto(String emailAddress, String firstName, String lastName, String mobileNumber, String phoneNumber, String position, String title) {
    this.emailAddress = emailAddress;
    this.firstName = firstName;
    this.lastName = lastName;
    this.mobileNumber = mobileNumber;
    this.phoneNumber = phoneNumber;
    this.position = position;
    this.title = title;
  }

  public EmployeeDto accessLevel(AccessLevelEnum accessLevel) {
    this.accessLevel = accessLevel;
    return this;
  }

  /**
   * Get accessLevel
   * @return accessLevel
   */
  
  @Schema(name = "accessLevel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accessLevel")
  public AccessLevelEnum getAccessLevel() {
    return accessLevel;
  }

  public void setAccessLevel(AccessLevelEnum accessLevel) {
    this.accessLevel = accessLevel;
  }

  public EmployeeDto address(AddressDto address) {
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

  public EmployeeDto centralCardId(String centralCardId) {
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

  public EmployeeDto dialingCode(String dialingCode) {
    this.dialingCode = dialingCode;
    return this;
  }

  /**
   * Get dialingCode
   * @return dialingCode
   */
  
  @Schema(name = "dialingCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dialingCode")
  public String getDialingCode() {
    return dialingCode;
  }

  public void setDialingCode(String dialingCode) {
    this.dialingCode = dialingCode;
  }

  public EmployeeDto emailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
    return this;
  }

  /**
   * Get emailAddress
   * @return emailAddress
   */
  @NotNull 
  @Schema(name = "emailAddress", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("emailAddress")
  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public EmployeeDto employeeAnswers(EmployeeAnswersDto employeeAnswers) {
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

  public EmployeeDto employeeStatus(EmployeeStatusEnum employeeStatus) {
    this.employeeStatus = employeeStatus;
    return this;
  }

  /**
   * Get employeeStatus
   * @return employeeStatus
   */
  
  @Schema(name = "employeeStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("employeeStatus")
  public EmployeeStatusEnum getEmployeeStatus() {
    return employeeStatus;
  }

  public void setEmployeeStatus(EmployeeStatusEnum employeeStatus) {
    this.employeeStatus = employeeStatus;
  }

  public EmployeeDto firstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * Get firstName
   * @return firstName
   */
  @NotNull 
  @Schema(name = "firstName", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("firstName")
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public EmployeeDto ghNumber(String ghNumber) {
    this.ghNumber = ghNumber;
    return this;
  }

  /**
   * Get ghNumber
   * @return ghNumber
   */
  
  @Schema(name = "ghNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ghNumber")
  public String getGhNumber() {
    return ghNumber;
  }

  public void setGhNumber(String ghNumber) {
    this.ghNumber = ghNumber;
  }

  public EmployeeDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public EmployeeDto lastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * Get lastName
   * @return lastName
   */
  @NotNull 
  @Schema(name = "lastName", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("lastName")
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public EmployeeDto mobileNumber(String mobileNumber) {
    this.mobileNumber = mobileNumber;
    return this;
  }

  /**
   * Get mobileNumber
   * @return mobileNumber
   */
  @NotNull 
  @Schema(name = "mobileNumber", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("mobileNumber")
  public String getMobileNumber() {
    return mobileNumber;
  }

  public void setMobileNumber(String mobileNumber) {
    this.mobileNumber = mobileNumber;
  }

  public EmployeeDto phoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  /**
   * Get phoneNumber
   * @return phoneNumber
   */
  @NotNull 
  @Schema(name = "phoneNumber", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("phoneNumber")
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public EmployeeDto position(String position) {
    this.position = position;
    return this;
  }

  /**
   * Get position
   * @return position
   */
  @NotNull 
  @Schema(name = "position", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("position")
  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public EmployeeDto textConfirmation(Boolean textConfirmation) {
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

  public EmployeeDto title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
   */
  @NotNull 
  @Schema(name = "title", requiredMode = Schema.RequiredMode.REQUIRED)
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
    EmployeeDto employee = (EmployeeDto) o;
    return Objects.equals(this.accessLevel, employee.accessLevel) &&
        Objects.equals(this.address, employee.address) &&
        Objects.equals(this.centralCardId, employee.centralCardId) &&
        Objects.equals(this.dialingCode, employee.dialingCode) &&
        Objects.equals(this.emailAddress, employee.emailAddress) &&
        Objects.equals(this.employeeAnswers, employee.employeeAnswers) &&
        Objects.equals(this.employeeStatus, employee.employeeStatus) &&
        Objects.equals(this.firstName, employee.firstName) &&
        Objects.equals(this.ghNumber, employee.ghNumber) &&
        Objects.equals(this.id, employee.id) &&
        Objects.equals(this.lastName, employee.lastName) &&
        Objects.equals(this.mobileNumber, employee.mobileNumber) &&
        Objects.equals(this.phoneNumber, employee.phoneNumber) &&
        Objects.equals(this.position, employee.position) &&
        Objects.equals(this.textConfirmation, employee.textConfirmation) &&
        Objects.equals(this.title, employee.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessLevel, address, centralCardId, dialingCode, emailAddress, employeeAnswers, employeeStatus, firstName, ghNumber, id, lastName, mobileNumber, phoneNumber, position, textConfirmation, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EmployeeDto {\n");
    sb.append("    accessLevel: ").append(toIndentedString(accessLevel)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    centralCardId: ").append(toIndentedString(centralCardId)).append("\n");
    sb.append("    dialingCode: ").append(toIndentedString(dialingCode)).append("\n");
    sb.append("    emailAddress: ").append(toIndentedString(emailAddress)).append("\n");
    sb.append("    employeeAnswers: ").append(toIndentedString(employeeAnswers)).append("\n");
    sb.append("    employeeStatus: ").append(toIndentedString(employeeStatus)).append("\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    ghNumber: ").append(toIndentedString(ghNumber)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    mobileNumber: ").append(toIndentedString(mobileNumber)).append("\n");
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


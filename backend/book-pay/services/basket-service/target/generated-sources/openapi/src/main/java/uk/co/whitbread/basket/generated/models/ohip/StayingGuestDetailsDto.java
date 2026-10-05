package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.StayingGuestAdditionalDetailsDto;
import uk.co.whitbread.basket.generated.models.ohip.StayingGuestAddressDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * StayingGuestDetailsDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class StayingGuestDetailsDto {

  private @Nullable StayingGuestAdditionalDetailsDto additionalDetails;

  private @Nullable StayingGuestAddressDto address;

  private @Nullable String emailAddress;

  private @Nullable String employeeAccountId;

  private String firstName;

  private String lastName;

  private @Nullable String profileId;

  private @Nullable String title;

  public StayingGuestDetailsDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public StayingGuestDetailsDto(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public StayingGuestDetailsDto additionalDetails(StayingGuestAdditionalDetailsDto additionalDetails) {
    this.additionalDetails = additionalDetails;
    return this;
  }

  /**
   * Get additionalDetails
   * @return additionalDetails
   */
  @Valid 
  @Schema(name = "additionalDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("additionalDetails")
  public StayingGuestAdditionalDetailsDto getAdditionalDetails() {
    return additionalDetails;
  }

  public void setAdditionalDetails(StayingGuestAdditionalDetailsDto additionalDetails) {
    this.additionalDetails = additionalDetails;
  }

  public StayingGuestDetailsDto address(StayingGuestAddressDto address) {
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
  public StayingGuestAddressDto getAddress() {
    return address;
  }

  public void setAddress(StayingGuestAddressDto address) {
    this.address = address;
  }

  public StayingGuestDetailsDto emailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
    return this;
  }

  /**
   * Get emailAddress
   * @return emailAddress
   */
  
  @Schema(name = "emailAddress", example = "john.carry@email.com", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emailAddress")
  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public StayingGuestDetailsDto employeeAccountId(String employeeAccountId) {
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

  public StayingGuestDetailsDto firstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * Get firstName
   * @return firstName
   */
  @NotNull 
  @Schema(name = "firstName", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("firstName")
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public StayingGuestDetailsDto lastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * Get lastName
   * @return lastName
   */
  @NotNull 
  @Schema(name = "lastName", example = "Carry", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("lastName")
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public StayingGuestDetailsDto profileId(String profileId) {
    this.profileId = profileId;
    return this;
  }

  /**
   * Get profileId
   * @return profileId
   */
  
  @Schema(name = "profileId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("profileId")
  public String getProfileId() {
    return profileId;
  }

  public void setProfileId(String profileId) {
    this.profileId = profileId;
  }

  public StayingGuestDetailsDto title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
   */
  
  @Schema(name = "title", example = "Mrs", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    StayingGuestDetailsDto stayingGuestDetailsDto = (StayingGuestDetailsDto) o;
    return Objects.equals(this.additionalDetails, stayingGuestDetailsDto.additionalDetails) &&
        Objects.equals(this.address, stayingGuestDetailsDto.address) &&
        Objects.equals(this.emailAddress, stayingGuestDetailsDto.emailAddress) &&
        Objects.equals(this.employeeAccountId, stayingGuestDetailsDto.employeeAccountId) &&
        Objects.equals(this.firstName, stayingGuestDetailsDto.firstName) &&
        Objects.equals(this.lastName, stayingGuestDetailsDto.lastName) &&
        Objects.equals(this.profileId, stayingGuestDetailsDto.profileId) &&
        Objects.equals(this.title, stayingGuestDetailsDto.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(additionalDetails, address, emailAddress, employeeAccountId, firstName, lastName, profileId, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StayingGuestDetailsDto {\n");
    sb.append("    additionalDetails: ").append(toIndentedString(additionalDetails)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    emailAddress: ").append(toIndentedString(emailAddress)).append("\n");
    sb.append("    employeeAccountId: ").append(toIndentedString(employeeAccountId)).append("\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    profileId: ").append(toIndentedString(profileId)).append("\n");
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


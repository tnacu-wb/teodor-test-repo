package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.StayingGuestAdditionalDetails;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AccompanyingGuestDetails
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AccompanyingGuestDetails {

  private @Nullable StayingGuestAdditionalDetails additionalDetails;

  private @Nullable String emailAddress;

  private @Nullable String employeeAccountId;

  private @Nullable String firstName;

  private @Nullable String lastName;

  private @Nullable String profileId;

  private @Nullable String title;

  public AccompanyingGuestDetails additionalDetails(StayingGuestAdditionalDetails additionalDetails) {
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
  public StayingGuestAdditionalDetails getAdditionalDetails() {
    return additionalDetails;
  }

  public void setAdditionalDetails(StayingGuestAdditionalDetails additionalDetails) {
    this.additionalDetails = additionalDetails;
  }

  public AccompanyingGuestDetails emailAddress(String emailAddress) {
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

  public AccompanyingGuestDetails employeeAccountId(String employeeAccountId) {
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

  public AccompanyingGuestDetails firstName(String firstName) {
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

  public AccompanyingGuestDetails lastName(String lastName) {
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

  public AccompanyingGuestDetails profileId(String profileId) {
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

  public AccompanyingGuestDetails title(String title) {
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
    AccompanyingGuestDetails accompanyingGuestDetails = (AccompanyingGuestDetails) o;
    return Objects.equals(this.additionalDetails, accompanyingGuestDetails.additionalDetails) &&
        Objects.equals(this.emailAddress, accompanyingGuestDetails.emailAddress) &&
        Objects.equals(this.employeeAccountId, accompanyingGuestDetails.employeeAccountId) &&
        Objects.equals(this.firstName, accompanyingGuestDetails.firstName) &&
        Objects.equals(this.lastName, accompanyingGuestDetails.lastName) &&
        Objects.equals(this.profileId, accompanyingGuestDetails.profileId) &&
        Objects.equals(this.title, accompanyingGuestDetails.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(additionalDetails, emailAddress, employeeAccountId, firstName, lastName, profileId, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AccompanyingGuestDetails {\n");
    sb.append("    additionalDetails: ").append(toIndentedString(additionalDetails)).append("\n");
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


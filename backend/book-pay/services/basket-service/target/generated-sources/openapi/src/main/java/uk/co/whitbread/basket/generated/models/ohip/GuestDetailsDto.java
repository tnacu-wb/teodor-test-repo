package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.KioskAddressDto;
import uk.co.whitbread.basket.generated.models.ohip.PassportDetailsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * GuestDetailsDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class GuestDetailsDto {

  private @Nullable String emailAddress;

  private @Nullable String givenName;

  private @Nullable KioskAddressDto kioskAddress;

  private @Nullable String nameTitle;

  private @Nullable String nameType;

  private @Nullable String nationality;

  private @Nullable PassportDetailsDto passportDetails;

  private @Nullable String phoneNumber;

  private @Nullable String surname;

  public GuestDetailsDto emailAddress(String emailAddress) {
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

  public GuestDetailsDto givenName(String givenName) {
    this.givenName = givenName;
    return this;
  }

  /**
   * Get givenName
   * @return givenName
   */
  
  @Schema(name = "givenName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("givenName")
  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public GuestDetailsDto kioskAddress(KioskAddressDto kioskAddress) {
    this.kioskAddress = kioskAddress;
    return this;
  }

  /**
   * Get kioskAddress
   * @return kioskAddress
   */
  @Valid 
  @Schema(name = "kioskAddress", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("kioskAddress")
  public KioskAddressDto getKioskAddress() {
    return kioskAddress;
  }

  public void setKioskAddress(KioskAddressDto kioskAddress) {
    this.kioskAddress = kioskAddress;
  }

  public GuestDetailsDto nameTitle(String nameTitle) {
    this.nameTitle = nameTitle;
    return this;
  }

  /**
   * Get nameTitle
   * @return nameTitle
   */
  
  @Schema(name = "nameTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nameTitle")
  public String getNameTitle() {
    return nameTitle;
  }

  public void setNameTitle(String nameTitle) {
    this.nameTitle = nameTitle;
  }

  public GuestDetailsDto nameType(String nameType) {
    this.nameType = nameType;
    return this;
  }

  /**
   * Get nameType
   * @return nameType
   */
  
  @Schema(name = "nameType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nameType")
  public String getNameType() {
    return nameType;
  }

  public void setNameType(String nameType) {
    this.nameType = nameType;
  }

  public GuestDetailsDto nationality(String nationality) {
    this.nationality = nationality;
    return this;
  }

  /**
   * Get nationality
   * @return nationality
   */
  
  @Schema(name = "nationality", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nationality")
  public String getNationality() {
    return nationality;
  }

  public void setNationality(String nationality) {
    this.nationality = nationality;
  }

  public GuestDetailsDto passportDetails(PassportDetailsDto passportDetails) {
    this.passportDetails = passportDetails;
    return this;
  }

  /**
   * Get passportDetails
   * @return passportDetails
   */
  @Valid 
  @Schema(name = "passportDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("passportDetails")
  public PassportDetailsDto getPassportDetails() {
    return passportDetails;
  }

  public void setPassportDetails(PassportDetailsDto passportDetails) {
    this.passportDetails = passportDetails;
  }

  public GuestDetailsDto phoneNumber(String phoneNumber) {
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

  public GuestDetailsDto surname(String surname) {
    this.surname = surname;
    return this;
  }

  /**
   * Get surname
   * @return surname
   */
  
  @Schema(name = "surname", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("surname")
  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GuestDetailsDto guestDetailsDto = (GuestDetailsDto) o;
    return Objects.equals(this.emailAddress, guestDetailsDto.emailAddress) &&
        Objects.equals(this.givenName, guestDetailsDto.givenName) &&
        Objects.equals(this.kioskAddress, guestDetailsDto.kioskAddress) &&
        Objects.equals(this.nameTitle, guestDetailsDto.nameTitle) &&
        Objects.equals(this.nameType, guestDetailsDto.nameType) &&
        Objects.equals(this.nationality, guestDetailsDto.nationality) &&
        Objects.equals(this.passportDetails, guestDetailsDto.passportDetails) &&
        Objects.equals(this.phoneNumber, guestDetailsDto.phoneNumber) &&
        Objects.equals(this.surname, guestDetailsDto.surname);
  }

  @Override
  public int hashCode() {
    return Objects.hash(emailAddress, givenName, kioskAddress, nameTitle, nameType, nationality, passportDetails, phoneNumber, surname);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GuestDetailsDto {\n");
    sb.append("    emailAddress: ").append(toIndentedString(emailAddress)).append("\n");
    sb.append("    givenName: ").append(toIndentedString(givenName)).append("\n");
    sb.append("    kioskAddress: ").append(toIndentedString(kioskAddress)).append("\n");
    sb.append("    nameTitle: ").append(toIndentedString(nameTitle)).append("\n");
    sb.append("    nameType: ").append(toIndentedString(nameType)).append("\n");
    sb.append("    nationality: ").append(toIndentedString(nationality)).append("\n");
    sb.append("    passportDetails: ").append(toIndentedString(passportDetails)).append("\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
    sb.append("    surname: ").append(toIndentedString(surname)).append("\n");
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


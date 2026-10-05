package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.GuestAddress;
import uk.co.whitbread.basket.generated.models.ohip.StayingGuestAdditionalDetails;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ReservationGuest
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationGuest {

  private @Nullable StayingGuestAdditionalDetails additionalDetails;

  private @Nullable GuestAddress address;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate birthDate;

  private @Nullable String email;

  private @Nullable String fullName;

  private @Nullable String givenName;

  private @Nullable Boolean guestRestricted;

  private @Nullable GuestAddress homeAddress;

  private @Nullable String id;

  private @Nullable Boolean isAccompanyingGuest;

  private @Nullable String language;

  private @Nullable String nameTitle;

  private @Nullable String phoneNumber;

  private @Nullable String profileId;

  private @Nullable String surname;

  private @Nullable String type;

  public ReservationGuest additionalDetails(StayingGuestAdditionalDetails additionalDetails) {
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

  public ReservationGuest address(GuestAddress address) {
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
  public GuestAddress getAddress() {
    return address;
  }

  public void setAddress(GuestAddress address) {
    this.address = address;
  }

  public ReservationGuest birthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
    return this;
  }

  /**
   * Get birthDate
   * @return birthDate
   */
  @Valid 
  @Schema(name = "birthDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("birthDate")
  public LocalDate getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  public ReservationGuest email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
   */
  
  @Schema(name = "email", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public ReservationGuest fullName(String fullName) {
    this.fullName = fullName;
    return this;
  }

  /**
   * Get fullName
   * @return fullName
   */
  
  @Schema(name = "fullName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fullName")
  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public ReservationGuest givenName(String givenName) {
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

  public ReservationGuest guestRestricted(Boolean guestRestricted) {
    this.guestRestricted = guestRestricted;
    return this;
  }

  /**
   * Get guestRestricted
   * @return guestRestricted
   */
  
  @Schema(name = "guestRestricted", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("guestRestricted")
  public Boolean getGuestRestricted() {
    return guestRestricted;
  }

  public void setGuestRestricted(Boolean guestRestricted) {
    this.guestRestricted = guestRestricted;
  }

  public ReservationGuest homeAddress(GuestAddress homeAddress) {
    this.homeAddress = homeAddress;
    return this;
  }

  /**
   * Get homeAddress
   * @return homeAddress
   */
  @Valid 
  @Schema(name = "homeAddress", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("homeAddress")
  public GuestAddress getHomeAddress() {
    return homeAddress;
  }

  public void setHomeAddress(GuestAddress homeAddress) {
    this.homeAddress = homeAddress;
  }

  public ReservationGuest id(String id) {
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

  public ReservationGuest isAccompanyingGuest(Boolean isAccompanyingGuest) {
    this.isAccompanyingGuest = isAccompanyingGuest;
    return this;
  }

  /**
   * Get isAccompanyingGuest
   * @return isAccompanyingGuest
   */
  
  @Schema(name = "isAccompanyingGuest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isAccompanyingGuest")
  public Boolean getIsAccompanyingGuest() {
    return isAccompanyingGuest;
  }

  public void setIsAccompanyingGuest(Boolean isAccompanyingGuest) {
    this.isAccompanyingGuest = isAccompanyingGuest;
  }

  public ReservationGuest language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
   */
  
  @Schema(name = "language", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("language")
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public ReservationGuest nameTitle(String nameTitle) {
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

  public ReservationGuest phoneNumber(String phoneNumber) {
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

  public ReservationGuest profileId(String profileId) {
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

  public ReservationGuest surname(String surname) {
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

  public ReservationGuest type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   */
  
  @Schema(name = "type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationGuest reservationGuest = (ReservationGuest) o;
    return Objects.equals(this.additionalDetails, reservationGuest.additionalDetails) &&
        Objects.equals(this.address, reservationGuest.address) &&
        Objects.equals(this.birthDate, reservationGuest.birthDate) &&
        Objects.equals(this.email, reservationGuest.email) &&
        Objects.equals(this.fullName, reservationGuest.fullName) &&
        Objects.equals(this.givenName, reservationGuest.givenName) &&
        Objects.equals(this.guestRestricted, reservationGuest.guestRestricted) &&
        Objects.equals(this.homeAddress, reservationGuest.homeAddress) &&
        Objects.equals(this.id, reservationGuest.id) &&
        Objects.equals(this.isAccompanyingGuest, reservationGuest.isAccompanyingGuest) &&
        Objects.equals(this.language, reservationGuest.language) &&
        Objects.equals(this.nameTitle, reservationGuest.nameTitle) &&
        Objects.equals(this.phoneNumber, reservationGuest.phoneNumber) &&
        Objects.equals(this.profileId, reservationGuest.profileId) &&
        Objects.equals(this.surname, reservationGuest.surname) &&
        Objects.equals(this.type, reservationGuest.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(additionalDetails, address, birthDate, email, fullName, givenName, guestRestricted, homeAddress, id, isAccompanyingGuest, language, nameTitle, phoneNumber, profileId, surname, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationGuest {\n");
    sb.append("    additionalDetails: ").append(toIndentedString(additionalDetails)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    birthDate: ").append(toIndentedString(birthDate)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    fullName: ").append(toIndentedString(fullName)).append("\n");
    sb.append("    givenName: ").append(toIndentedString(givenName)).append("\n");
    sb.append("    guestRestricted: ").append(toIndentedString(guestRestricted)).append("\n");
    sb.append("    homeAddress: ").append(toIndentedString(homeAddress)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    isAccompanyingGuest: ").append(toIndentedString(isAccompanyingGuest)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    nameTitle: ").append(toIndentedString(nameTitle)).append("\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
    sb.append("    profileId: ").append(toIndentedString(profileId)).append("\n");
    sb.append("    surname: ").append(toIndentedString(surname)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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


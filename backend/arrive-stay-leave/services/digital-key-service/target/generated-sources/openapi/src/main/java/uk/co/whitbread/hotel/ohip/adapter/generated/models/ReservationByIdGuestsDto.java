package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationByIdGuestAddressDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.StayingGuestAdditionalDetailsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ReservationByIdGuestsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationByIdGuestsDto {

  private @Nullable StayingGuestAdditionalDetailsDto additionalDetails;

  private @Nullable ReservationByIdGuestAddressDto address;

  private @Nullable ReservationByIdGuestAddressDto homeAddress;

  private @Nullable String email;

  private @Nullable String givenName;

  private @Nullable Boolean isAccompanyingGuest;

  private @Nullable String nameTitle;

  private @Nullable String profileId;

  private @Nullable String surName;

  private @Nullable String type;

  public ReservationByIdGuestsDto additionalDetails(StayingGuestAdditionalDetailsDto additionalDetails) {
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

  public ReservationByIdGuestsDto address(ReservationByIdGuestAddressDto address) {
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
  public ReservationByIdGuestAddressDto getAddress() {
    return address;
  }

  public void setAddress(ReservationByIdGuestAddressDto address) {
    this.address = address;
  }

  public ReservationByIdGuestsDto homeAddress(ReservationByIdGuestAddressDto homeAddress) {
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
  public ReservationByIdGuestAddressDto getHomeAddress() {
    return homeAddress;
  }

  public void setHomeAddress(ReservationByIdGuestAddressDto homeAddress) {
    this.homeAddress = homeAddress;
  }

  public ReservationByIdGuestsDto email(String email) {
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

  public ReservationByIdGuestsDto givenName(String givenName) {
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

  public ReservationByIdGuestsDto isAccompanyingGuest(Boolean isAccompanyingGuest) {
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

  public ReservationByIdGuestsDto nameTitle(String nameTitle) {
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

  public ReservationByIdGuestsDto profileId(String profileId) {
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

  public ReservationByIdGuestsDto surName(String surName) {
    this.surName = surName;
    return this;
  }

  /**
   * Get surName
   * @return surName
   */
  
  @Schema(name = "surName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("surName")
  public String getSurName() {
    return surName;
  }

  public void setSurName(String surName) {
    this.surName = surName;
  }

  public ReservationByIdGuestsDto type(String type) {
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
    ReservationByIdGuestsDto reservationByIdGuestsDto = (ReservationByIdGuestsDto) o;
    return Objects.equals(this.additionalDetails, reservationByIdGuestsDto.additionalDetails) &&
        Objects.equals(this.address, reservationByIdGuestsDto.address) &&
        Objects.equals(this.homeAddress, reservationByIdGuestsDto.homeAddress) &&
        Objects.equals(this.email, reservationByIdGuestsDto.email) &&
        Objects.equals(this.givenName, reservationByIdGuestsDto.givenName) &&
        Objects.equals(this.isAccompanyingGuest, reservationByIdGuestsDto.isAccompanyingGuest) &&
        Objects.equals(this.nameTitle, reservationByIdGuestsDto.nameTitle) &&
        Objects.equals(this.profileId, reservationByIdGuestsDto.profileId) &&
        Objects.equals(this.surName, reservationByIdGuestsDto.surName) &&
        Objects.equals(this.type, reservationByIdGuestsDto.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(additionalDetails, address, homeAddress, email, givenName, isAccompanyingGuest, nameTitle, profileId, surName, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationByIdGuestsDto {\n");
    sb.append("    additionalDetails: ").append(toIndentedString(additionalDetails)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    homeAddress: ").append(toIndentedString(homeAddress)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    givenName: ").append(toIndentedString(givenName)).append("\n");
    sb.append("    isAccompanyingGuest: ").append(toIndentedString(isAccompanyingGuest)).append("\n");
    sb.append("    nameTitle: ").append(toIndentedString(nameTitle)).append("\n");
    sb.append("    profileId: ").append(toIndentedString(profileId)).append("\n");
    sb.append("    surName: ").append(toIndentedString(surName)).append("\n");
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


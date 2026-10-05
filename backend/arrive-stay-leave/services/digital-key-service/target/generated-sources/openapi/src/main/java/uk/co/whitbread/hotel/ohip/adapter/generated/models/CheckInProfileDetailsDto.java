package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AddressesDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CheckInCustomerDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CheckInTelephonesDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.EmailsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CheckInProfileDetailsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CheckInProfileDetailsDto {

  private @Nullable AddressesDto addresses;

  private @Nullable CheckInCustomerDto customer;

  private @Nullable EmailsDto emails;

  private @Nullable String profileType;

  private @Nullable CheckInTelephonesDto telephones;

  public CheckInProfileDetailsDto addresses(AddressesDto addresses) {
    this.addresses = addresses;
    return this;
  }

  /**
   * Get addresses
   * @return addresses
   */
  @Valid 
  @Schema(name = "addresses", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addresses")
  public AddressesDto getAddresses() {
    return addresses;
  }

  public void setAddresses(AddressesDto addresses) {
    this.addresses = addresses;
  }

  public CheckInProfileDetailsDto customer(CheckInCustomerDto customer) {
    this.customer = customer;
    return this;
  }

  /**
   * Get customer
   * @return customer
   */
  @Valid 
  @Schema(name = "customer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customer")
  public CheckInCustomerDto getCustomer() {
    return customer;
  }

  public void setCustomer(CheckInCustomerDto customer) {
    this.customer = customer;
  }

  public CheckInProfileDetailsDto emails(EmailsDto emails) {
    this.emails = emails;
    return this;
  }

  /**
   * Get emails
   * @return emails
   */
  @Valid 
  @Schema(name = "emails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emails")
  public EmailsDto getEmails() {
    return emails;
  }

  public void setEmails(EmailsDto emails) {
    this.emails = emails;
  }

  public CheckInProfileDetailsDto profileType(String profileType) {
    this.profileType = profileType;
    return this;
  }

  /**
   * Get profileType
   * @return profileType
   */
  
  @Schema(name = "profileType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("profileType")
  public String getProfileType() {
    return profileType;
  }

  public void setProfileType(String profileType) {
    this.profileType = profileType;
  }

  public CheckInProfileDetailsDto telephones(CheckInTelephonesDto telephones) {
    this.telephones = telephones;
    return this;
  }

  /**
   * Get telephones
   * @return telephones
   */
  @Valid 
  @Schema(name = "telephones", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("telephones")
  public CheckInTelephonesDto getTelephones() {
    return telephones;
  }

  public void setTelephones(CheckInTelephonesDto telephones) {
    this.telephones = telephones;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CheckInProfileDetailsDto checkInProfileDetailsDto = (CheckInProfileDetailsDto) o;
    return Objects.equals(this.addresses, checkInProfileDetailsDto.addresses) &&
        Objects.equals(this.customer, checkInProfileDetailsDto.customer) &&
        Objects.equals(this.emails, checkInProfileDetailsDto.emails) &&
        Objects.equals(this.profileType, checkInProfileDetailsDto.profileType) &&
        Objects.equals(this.telephones, checkInProfileDetailsDto.telephones);
  }

  @Override
  public int hashCode() {
    return Objects.hash(addresses, customer, emails, profileType, telephones);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CheckInProfileDetailsDto {\n");
    sb.append("    addresses: ").append(toIndentedString(addresses)).append("\n");
    sb.append("    customer: ").append(toIndentedString(customer)).append("\n");
    sb.append("    emails: ").append(toIndentedString(emails)).append("\n");
    sb.append("    profileType: ").append(toIndentedString(profileType)).append("\n");
    sb.append("    telephones: ").append(toIndentedString(telephones)).append("\n");
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


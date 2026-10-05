package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CustomerTypeDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileTypeAddressesDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileTypeEmailsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ProfileTypeDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ProfileTypeDto {

  private @Nullable ProfileTypeAddressesDto addresses;

  private @Nullable CustomerTypeDto customer;

  private @Nullable ProfileTypeEmailsDto emails;

  public ProfileTypeDto addresses(ProfileTypeAddressesDto addresses) {
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
  public ProfileTypeAddressesDto getAddresses() {
    return addresses;
  }

  public void setAddresses(ProfileTypeAddressesDto addresses) {
    this.addresses = addresses;
  }

  public ProfileTypeDto customer(CustomerTypeDto customer) {
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
  public CustomerTypeDto getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerTypeDto customer) {
    this.customer = customer;
  }

  public ProfileTypeDto emails(ProfileTypeEmailsDto emails) {
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
  public ProfileTypeEmailsDto getEmails() {
    return emails;
  }

  public void setEmails(ProfileTypeEmailsDto emails) {
    this.emails = emails;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProfileTypeDto profileTypeDto = (ProfileTypeDto) o;
    return Objects.equals(this.addresses, profileTypeDto.addresses) &&
        Objects.equals(this.customer, profileTypeDto.customer) &&
        Objects.equals(this.emails, profileTypeDto.emails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(addresses, customer, emails);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProfileTypeDto {\n");
    sb.append("    addresses: ").append(toIndentedString(addresses)).append("\n");
    sb.append("    customer: ").append(toIndentedString(customer)).append("\n");
    sb.append("    emails: ").append(toIndentedString(emails)).append("\n");
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


package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.CustomerTypeDto;
import uk.co.whitbread.basket.generated.models.ohip.ProfileTypeAddressesDto;
import uk.co.whitbread.basket.generated.models.ohip.ProfileTypeEmailsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ProfileTypeDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
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


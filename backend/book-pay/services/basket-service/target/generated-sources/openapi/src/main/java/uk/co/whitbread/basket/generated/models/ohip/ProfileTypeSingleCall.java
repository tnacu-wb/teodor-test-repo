package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.CustomerTypeSingleCall;
import uk.co.whitbread.basket.generated.models.ohip.ProfileTypeEmailsSingleCall;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ProfileTypeSingleCall
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ProfileTypeSingleCall {

  private @Nullable CustomerTypeSingleCall customer;

  private @Nullable ProfileTypeEmailsSingleCall emails;

  public ProfileTypeSingleCall customer(CustomerTypeSingleCall customer) {
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
  public CustomerTypeSingleCall getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerTypeSingleCall customer) {
    this.customer = customer;
  }

  public ProfileTypeSingleCall emails(ProfileTypeEmailsSingleCall emails) {
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
  public ProfileTypeEmailsSingleCall getEmails() {
    return emails;
  }

  public void setEmails(ProfileTypeEmailsSingleCall emails) {
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
    ProfileTypeSingleCall profileTypeSingleCall = (ProfileTypeSingleCall) o;
    return Objects.equals(this.customer, profileTypeSingleCall.customer) &&
        Objects.equals(this.emails, profileTypeSingleCall.emails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(customer, emails);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProfileTypeSingleCall {\n");
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


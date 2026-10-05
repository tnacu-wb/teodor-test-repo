package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * InvoiceGuestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:45.778333+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class InvoiceGuestDto {

  private @Nullable String emailAddress;

  private @Nullable Boolean isLeadGuest;

  private @Nullable String name;

  public InvoiceGuestDto emailAddress(String emailAddress) {
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

  public InvoiceGuestDto isLeadGuest(Boolean isLeadGuest) {
    this.isLeadGuest = isLeadGuest;
    return this;
  }

  /**
   * Get isLeadGuest
   * @return isLeadGuest
   */
  
  @Schema(name = "isLeadGuest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isLeadGuest")
  public Boolean getIsLeadGuest() {
    return isLeadGuest;
  }

  public void setIsLeadGuest(Boolean isLeadGuest) {
    this.isLeadGuest = isLeadGuest;
  }

  public InvoiceGuestDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  
  @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvoiceGuestDto invoiceGuestDto = (InvoiceGuestDto) o;
    return Objects.equals(this.emailAddress, invoiceGuestDto.emailAddress) &&
        Objects.equals(this.isLeadGuest, invoiceGuestDto.isLeadGuest) &&
        Objects.equals(this.name, invoiceGuestDto.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(emailAddress, isLeadGuest, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvoiceGuestDto {\n");
    sb.append("    emailAddress: ").append(toIndentedString(emailAddress)).append("\n");
    sb.append("    isLeadGuest: ").append(toIndentedString(isLeadGuest)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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


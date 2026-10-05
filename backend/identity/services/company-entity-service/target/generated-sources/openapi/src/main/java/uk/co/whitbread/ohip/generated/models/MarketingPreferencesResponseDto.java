package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.CustomerDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * MarketingPreferencesResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MarketingPreferencesResponseDto {

  private @Nullable String contactValue;

  private @Nullable CustomerDto customer;

  private @Nullable Boolean optIn;

  public MarketingPreferencesResponseDto contactValue(String contactValue) {
    this.contactValue = contactValue;
    return this;
  }

  /**
   * Get contactValue
   * @return contactValue
   */
  
  @Schema(name = "contactValue", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("contactValue")
  public String getContactValue() {
    return contactValue;
  }

  public void setContactValue(String contactValue) {
    this.contactValue = contactValue;
  }

  public MarketingPreferencesResponseDto customer(CustomerDto customer) {
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
  public CustomerDto getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerDto customer) {
    this.customer = customer;
  }

  public MarketingPreferencesResponseDto optIn(Boolean optIn) {
    this.optIn = optIn;
    return this;
  }

  /**
   * Get optIn
   * @return optIn
   */
  
  @Schema(name = "optIn", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("optIn")
  public Boolean getOptIn() {
    return optIn;
  }

  public void setOptIn(Boolean optIn) {
    this.optIn = optIn;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MarketingPreferencesResponseDto marketingPreferencesResponseDto = (MarketingPreferencesResponseDto) o;
    return Objects.equals(this.contactValue, marketingPreferencesResponseDto.contactValue) &&
        Objects.equals(this.customer, marketingPreferencesResponseDto.customer) &&
        Objects.equals(this.optIn, marketingPreferencesResponseDto.optIn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contactValue, customer, optIn);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MarketingPreferencesResponseDto {\n");
    sb.append("    contactValue: ").append(toIndentedString(contactValue)).append("\n");
    sb.append("    customer: ").append(toIndentedString(customer)).append("\n");
    sb.append("    optIn: ").append(toIndentedString(optIn)).append("\n");
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


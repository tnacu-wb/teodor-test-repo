package uk.co.whitbread.hotel.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.basket.AddressCcuiDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BillingCcuiDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:22.312200+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BillingCcuiDto {

  private @Nullable AddressCcuiDto address;

  private @Nullable Boolean differentBillingAddress;

  private @Nullable String email;

  private @Nullable String firstName;

  private @Nullable String lastName;

  private @Nullable String telephone;

  private @Nullable String title;

  public BillingCcuiDto address(AddressCcuiDto address) {
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
  public AddressCcuiDto getAddress() {
    return address;
  }

  public void setAddress(AddressCcuiDto address) {
    this.address = address;
  }

  public BillingCcuiDto differentBillingAddress(Boolean differentBillingAddress) {
    this.differentBillingAddress = differentBillingAddress;
    return this;
  }

  /**
   * Get differentBillingAddress
   * @return differentBillingAddress
   */
  
  @Schema(name = "differentBillingAddress", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("differentBillingAddress")
  public Boolean getDifferentBillingAddress() {
    return differentBillingAddress;
  }

  public void setDifferentBillingAddress(Boolean differentBillingAddress) {
    this.differentBillingAddress = differentBillingAddress;
  }

  public BillingCcuiDto email(String email) {
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

  public BillingCcuiDto firstName(String firstName) {
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

  public BillingCcuiDto lastName(String lastName) {
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

  public BillingCcuiDto telephone(String telephone) {
    this.telephone = telephone;
    return this;
  }

  /**
   * Get telephone
   * @return telephone
   */
  
  @Schema(name = "telephone", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("telephone")
  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  public BillingCcuiDto title(String title) {
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
    BillingCcuiDto billingCcuiDto = (BillingCcuiDto) o;
    return Objects.equals(this.address, billingCcuiDto.address) &&
        Objects.equals(this.differentBillingAddress, billingCcuiDto.differentBillingAddress) &&
        Objects.equals(this.email, billingCcuiDto.email) &&
        Objects.equals(this.firstName, billingCcuiDto.firstName) &&
        Objects.equals(this.lastName, billingCcuiDto.lastName) &&
        Objects.equals(this.telephone, billingCcuiDto.telephone) &&
        Objects.equals(this.title, billingCcuiDto.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(address, differentBillingAddress, email, firstName, lastName, telephone, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BillingCcuiDto {\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    differentBillingAddress: ").append(toIndentedString(differentBillingAddress)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    telephone: ").append(toIndentedString(telephone)).append("\n");
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


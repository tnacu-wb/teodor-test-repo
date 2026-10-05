package uk.co.whitbread.refund.processor.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import uk.co.whitbread.refund.processor.generated.models.payments.AddressDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Information regarding the booking being made.
 */

@Schema(name = "Billing", description = "Information regarding the booking being made.")
@JsonTypeName("Billing")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:12:20.597747+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BillingDto {

  private @Nullable String title;

  private @Nullable String firstName;

  private @Nullable String lastName;

  private @Nullable String email;

  private @Nullable String telephone;

  private @Nullable AddressDto address;

  public BillingDto title(String title) {
    this.title = title;
    return this;
  }

  /**
   * name title.
   * @return title
   */
  
  @Schema(name = "title", example = "Mr", description = "name title.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public BillingDto firstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * Card holder first name.
   * @return firstName
   */
  
  @Schema(name = "firstName", example = "James", description = "Card holder first name.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("firstName")
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public BillingDto lastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * Card holder last name.
   * @return lastName
   */
  
  @Schema(name = "lastName", example = "Bond", description = "Card holder last name.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastName")
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public BillingDto email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Booker email address.
   * @return email
   */
  
  @Schema(name = "email", example = "example@email.com", description = "Booker email address.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public BillingDto telephone(String telephone) {
    this.telephone = telephone;
    return this;
  }

  /**
   * Booker telephone number.
   * @return telephone
   */
  
  @Schema(name = "telephone", example = "0789110237", description = "Booker telephone number.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("telephone")
  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  public BillingDto address(AddressDto address) {
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
  public AddressDto getAddress() {
    return address;
  }

  public void setAddress(AddressDto address) {
    this.address = address;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BillingDto billing = (BillingDto) o;
    return Objects.equals(this.title, billing.title) &&
        Objects.equals(this.firstName, billing.firstName) &&
        Objects.equals(this.lastName, billing.lastName) &&
        Objects.equals(this.email, billing.email) &&
        Objects.equals(this.telephone, billing.telephone) &&
        Objects.equals(this.address, billing.address);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, firstName, lastName, email, telephone, address);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BillingDto {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    telephone: ").append(toIndentedString(telephone)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
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


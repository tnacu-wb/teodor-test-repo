package uk.co.whitbread.hotel.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.basket.AddressDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BillingDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:22.312200+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BillingDto {

  private @Nullable AddressDto address;

  private @Nullable Boolean bookerIsNotGuest;

  private @Nullable Boolean differentBillingAddress;

  private @Nullable String email;

  private @Nullable String firstName;

  private @Nullable String lastName;

  private @Nullable String telephone;

  private @Nullable String title;

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

  public BillingDto bookerIsNotGuest(Boolean bookerIsNotGuest) {
    this.bookerIsNotGuest = bookerIsNotGuest;
    return this;
  }

  /**
   * Get bookerIsNotGuest
   * @return bookerIsNotGuest
   */
  
  @Schema(name = "bookerIsNotGuest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookerIsNotGuest")
  public Boolean getBookerIsNotGuest() {
    return bookerIsNotGuest;
  }

  public void setBookerIsNotGuest(Boolean bookerIsNotGuest) {
    this.bookerIsNotGuest = bookerIsNotGuest;
  }

  public BillingDto differentBillingAddress(Boolean differentBillingAddress) {
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

  public BillingDto email(String email) {
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

  public BillingDto firstName(String firstName) {
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

  public BillingDto lastName(String lastName) {
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

  public BillingDto telephone(String telephone) {
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

  public BillingDto title(String title) {
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
    BillingDto billingDto = (BillingDto) o;
    return Objects.equals(this.address, billingDto.address) &&
        Objects.equals(this.bookerIsNotGuest, billingDto.bookerIsNotGuest) &&
        Objects.equals(this.differentBillingAddress, billingDto.differentBillingAddress) &&
        Objects.equals(this.email, billingDto.email) &&
        Objects.equals(this.firstName, billingDto.firstName) &&
        Objects.equals(this.lastName, billingDto.lastName) &&
        Objects.equals(this.telephone, billingDto.telephone) &&
        Objects.equals(this.title, billingDto.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(address, bookerIsNotGuest, differentBillingAddress, email, firstName, lastName, telephone, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BillingDto {\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    bookerIsNotGuest: ").append(toIndentedString(bookerIsNotGuest)).append("\n");
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


package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BookerAddressDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BookerDetailsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BookerDetailsDto {

  private @Nullable Boolean acceptFutureMailing;

  private @Nullable BookerAddressDto address;

  private @Nullable String emailAddress;

  private String firstName;

  private @Nullable String landline;

  private @Nullable String language;

  private String lastName;

  private @Nullable String mobile;

  private @Nullable String title;

  public BookerDetailsDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BookerDetailsDto(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public BookerDetailsDto acceptFutureMailing(Boolean acceptFutureMailing) {
    this.acceptFutureMailing = acceptFutureMailing;
    return this;
  }

  /**
   * Get acceptFutureMailing
   * @return acceptFutureMailing
   */
  
  @Schema(name = "acceptFutureMailing", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("acceptFutureMailing")
  public Boolean getAcceptFutureMailing() {
    return acceptFutureMailing;
  }

  public void setAcceptFutureMailing(Boolean acceptFutureMailing) {
    this.acceptFutureMailing = acceptFutureMailing;
  }

  public BookerDetailsDto address(BookerAddressDto address) {
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
  public BookerAddressDto getAddress() {
    return address;
  }

  public void setAddress(BookerAddressDto address) {
    this.address = address;
  }

  public BookerDetailsDto emailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
    return this;
  }

  /**
   * Get emailAddress
   * @return emailAddress
   */
  
  @Schema(name = "emailAddress", example = "john.carry@email.com", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emailAddress")
  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public BookerDetailsDto firstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * Get firstName
   * @return firstName
   */
  @NotNull 
  @Schema(name = "firstName", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("firstName")
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public BookerDetailsDto landline(String landline) {
    this.landline = landline;
    return this;
  }

  /**
   * Get landline
   * @return landline
   */
  
  @Schema(name = "landline", example = "+3905678754", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("landline")
  public String getLandline() {
    return landline;
  }

  public void setLandline(String landline) {
    this.landline = landline;
  }

  public BookerDetailsDto language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
   */
  
  @Schema(name = "language", example = "en", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("language")
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public BookerDetailsDto lastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * Get lastName
   * @return lastName
   */
  @NotNull 
  @Schema(name = "lastName", example = "Carry", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("lastName")
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public BookerDetailsDto mobile(String mobile) {
    this.mobile = mobile;
    return this;
  }

  /**
   * Get mobile
   * @return mobile
   */
  
  @Schema(name = "mobile", example = "+3905678754", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mobile")
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public BookerDetailsDto title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
   */
  
  @Schema(name = "title", example = "Mrs", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    BookerDetailsDto bookerDetailsDto = (BookerDetailsDto) o;
    return Objects.equals(this.acceptFutureMailing, bookerDetailsDto.acceptFutureMailing) &&
        Objects.equals(this.address, bookerDetailsDto.address) &&
        Objects.equals(this.emailAddress, bookerDetailsDto.emailAddress) &&
        Objects.equals(this.firstName, bookerDetailsDto.firstName) &&
        Objects.equals(this.landline, bookerDetailsDto.landline) &&
        Objects.equals(this.language, bookerDetailsDto.language) &&
        Objects.equals(this.lastName, bookerDetailsDto.lastName) &&
        Objects.equals(this.mobile, bookerDetailsDto.mobile) &&
        Objects.equals(this.title, bookerDetailsDto.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(acceptFutureMailing, address, emailAddress, firstName, landline, language, lastName, mobile, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookerDetailsDto {\n");
    sb.append("    acceptFutureMailing: ").append(toIndentedString(acceptFutureMailing)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    emailAddress: ").append(toIndentedString(emailAddress)).append("\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    landline: ").append(toIndentedString(landline)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    mobile: ").append(toIndentedString(mobile)).append("\n");
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


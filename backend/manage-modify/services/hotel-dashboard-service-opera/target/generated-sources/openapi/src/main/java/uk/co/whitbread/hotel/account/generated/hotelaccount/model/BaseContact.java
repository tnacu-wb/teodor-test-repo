package uk.co.whitbread.hotel.account.generated.hotelaccount.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.jspecify.annotations.Nullable;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.Passport;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * BaseContact
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:33.863804+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BaseContact {

  private @Nullable String carRegistration;

  private String email;

  private String firstName;

  private String lastName;

  private @Nullable String mobile;

  private @Nullable String nationality;

  private @Nullable Passport passport;

  private @Nullable String telephone;

  private String title;

  public BaseContact() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BaseContact(String email, String firstName, String lastName, String title) {
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.title = title;
  }

  public BaseContact carRegistration(String carRegistration) {
    this.carRegistration = carRegistration;
    return this;
  }

  /**
   * Get carRegistration
   * @return carRegistration
   */
  
  @Schema(name = "carRegistration", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("carRegistration")
  public String getCarRegistration() {
    return carRegistration;
  }

  public void setCarRegistration(String carRegistration) {
    this.carRegistration = carRegistration;
  }

  public BaseContact email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
   */
  @NotNull 
  @Schema(name = "email", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public BaseContact firstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * Get firstName
   * @return firstName
   */
  @NotNull 
  @Schema(name = "firstName", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("firstName")
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public BaseContact lastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * Get lastName
   * @return lastName
   */
  @NotNull 
  @Schema(name = "lastName", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("lastName")
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public BaseContact mobile(String mobile) {
    this.mobile = mobile;
    return this;
  }

  /**
   * Get mobile
   * @return mobile
   */
  
  @Schema(name = "mobile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mobile")
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public BaseContact nationality(String nationality) {
    this.nationality = nationality;
    return this;
  }

  /**
   * Get nationality
   * @return nationality
   */
  @Size(min = 0, max = 3) 
  @Schema(name = "nationality", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nationality")
  public String getNationality() {
    return nationality;
  }

  public void setNationality(String nationality) {
    this.nationality = nationality;
  }

  public BaseContact passport(Passport passport) {
    this.passport = passport;
    return this;
  }

  /**
   * Get passport
   * @return passport
   */
  @Valid 
  @Schema(name = "passport", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("passport")
  public Passport getPassport() {
    return passport;
  }

  public void setPassport(Passport passport) {
    this.passport = passport;
  }

  public BaseContact telephone(String telephone) {
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

  public BaseContact title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
   */
  @NotNull 
  @Schema(name = "title", requiredMode = Schema.RequiredMode.REQUIRED)
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
    BaseContact baseContact = (BaseContact) o;
    return Objects.equals(this.carRegistration, baseContact.carRegistration) &&
        Objects.equals(this.email, baseContact.email) &&
        Objects.equals(this.firstName, baseContact.firstName) &&
        Objects.equals(this.lastName, baseContact.lastName) &&
        Objects.equals(this.mobile, baseContact.mobile) &&
        Objects.equals(this.nationality, baseContact.nationality) &&
        Objects.equals(this.passport, baseContact.passport) &&
        Objects.equals(this.telephone, baseContact.telephone) &&
        Objects.equals(this.title, baseContact.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(carRegistration, email, firstName, lastName, mobile, nationality, passport, telephone, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BaseContact {\n");
    sb.append("    carRegistration: ").append(toIndentedString(carRegistration)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    mobile: ").append(toIndentedString(mobile)).append("\n");
    sb.append("    nationality: ").append(toIndentedString(nationality)).append("\n");
    sb.append("    passport: ").append(toIndentedString(passport)).append("\n");
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


package uk.co.whitbread.hotel.account.generated.hotelaccount.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.jspecify.annotations.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Passport
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:33.863804+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Passport {

  private @Nullable String countryOfIssue;

  private @Nullable String number;

  public Passport countryOfIssue(String countryOfIssue) {
    this.countryOfIssue = countryOfIssue;
    return this;
  }

  /**
   * Get countryOfIssue
   * @return countryOfIssue
   */
  
  @Schema(name = "countryOfIssue", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("countryOfIssue")
  public String getCountryOfIssue() {
    return countryOfIssue;
  }

  public void setCountryOfIssue(String countryOfIssue) {
    this.countryOfIssue = countryOfIssue;
  }

  public Passport number(String number) {
    this.number = number;
    return this;
  }

  /**
   * Get number
   * @return number
   */
  
  @Schema(name = "number", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("number")
  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Passport passport = (Passport) o;
    return Objects.equals(this.countryOfIssue, passport.countryOfIssue) &&
        Objects.equals(this.number, passport.number);
  }

  @Override
  public int hashCode() {
    return Objects.hash(countryOfIssue, number);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Passport {\n");
    sb.append("    countryOfIssue: ").append(toIndentedString(countryOfIssue)).append("\n");
    sb.append("    number: ").append(toIndentedString(number)).append("\n");
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


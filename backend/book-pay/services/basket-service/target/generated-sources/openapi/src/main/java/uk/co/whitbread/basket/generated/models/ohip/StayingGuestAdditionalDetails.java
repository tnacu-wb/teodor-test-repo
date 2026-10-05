package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * StayingGuestAdditionalDetails
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class StayingGuestAdditionalDetails {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate dob;

  private @Nullable String nationality;

  private @Nullable String passportNumber;

  public StayingGuestAdditionalDetails dob(LocalDate dob) {
    this.dob = dob;
    return this;
  }

  /**
   * Get dob
   * @return dob
   */
  @Valid 
  @Schema(name = "dob", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dob")
  public LocalDate getDob() {
    return dob;
  }

  public void setDob(LocalDate dob) {
    this.dob = dob;
  }

  public StayingGuestAdditionalDetails nationality(String nationality) {
    this.nationality = nationality;
    return this;
  }

  /**
   * Get nationality
   * @return nationality
   */
  @Size(min = 0, max = 20) 
  @Schema(name = "nationality", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nationality")
  public String getNationality() {
    return nationality;
  }

  public void setNationality(String nationality) {
    this.nationality = nationality;
  }

  public StayingGuestAdditionalDetails passportNumber(String passportNumber) {
    this.passportNumber = passportNumber;
    return this;
  }

  /**
   * Get passportNumber
   * @return passportNumber
   */
  @Pattern(regexp = "^[a-zA-Z0-9]+$") 
  @Schema(name = "passportNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("passportNumber")
  public String getPassportNumber() {
    return passportNumber;
  }

  public void setPassportNumber(String passportNumber) {
    this.passportNumber = passportNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StayingGuestAdditionalDetails stayingGuestAdditionalDetails = (StayingGuestAdditionalDetails) o;
    return Objects.equals(this.dob, stayingGuestAdditionalDetails.dob) &&
        Objects.equals(this.nationality, stayingGuestAdditionalDetails.nationality) &&
        Objects.equals(this.passportNumber, stayingGuestAdditionalDetails.passportNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dob, nationality, passportNumber);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StayingGuestAdditionalDetails {\n");
    sb.append("    dob: ").append(toIndentedString(dob)).append("\n");
    sb.append("    nationality: ").append(toIndentedString(nationality)).append("\n");
    sb.append("    passportNumber: ").append(toIndentedString(passportNumber)).append("\n");
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


package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PassportDetailsDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PassportDetailsDto {

  private @Nullable String nextDestination;

  private @Nullable String passportNumber;

  private @Nullable String placeOfIssue;

  public PassportDetailsDto nextDestination(String nextDestination) {
    this.nextDestination = nextDestination;
    return this;
  }

  /**
   * Get nextDestination
   * @return nextDestination
   */
  
  @Schema(name = "nextDestination", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nextDestination")
  public String getNextDestination() {
    return nextDestination;
  }

  public void setNextDestination(String nextDestination) {
    this.nextDestination = nextDestination;
  }

  public PassportDetailsDto passportNumber(String passportNumber) {
    this.passportNumber = passportNumber;
    return this;
  }

  /**
   * Get passportNumber
   * @return passportNumber
   */
  
  @Schema(name = "passportNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("passportNumber")
  public String getPassportNumber() {
    return passportNumber;
  }

  public void setPassportNumber(String passportNumber) {
    this.passportNumber = passportNumber;
  }

  public PassportDetailsDto placeOfIssue(String placeOfIssue) {
    this.placeOfIssue = placeOfIssue;
    return this;
  }

  /**
   * Get placeOfIssue
   * @return placeOfIssue
   */
  
  @Schema(name = "placeOfIssue", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("placeOfIssue")
  public String getPlaceOfIssue() {
    return placeOfIssue;
  }

  public void setPlaceOfIssue(String placeOfIssue) {
    this.placeOfIssue = placeOfIssue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PassportDetailsDto passportDetailsDto = (PassportDetailsDto) o;
    return Objects.equals(this.nextDestination, passportDetailsDto.nextDestination) &&
        Objects.equals(this.passportNumber, passportDetailsDto.passportNumber) &&
        Objects.equals(this.placeOfIssue, passportDetailsDto.placeOfIssue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nextDestination, passportNumber, placeOfIssue);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PassportDetailsDto {\n");
    sb.append("    nextDestination: ").append(toIndentedString(nextDestination)).append("\n");
    sb.append("    passportNumber: ").append(toIndentedString(passportNumber)).append("\n");
    sb.append("    placeOfIssue: ").append(toIndentedString(placeOfIssue)).append("\n");
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


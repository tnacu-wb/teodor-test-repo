package uk.co.whitbread.basket.generated.models.cdh;

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
 * PassportDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:16.272141+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PassportDto {

  private @Nullable String passportNumber;

  private @Nullable String placeOfIssue;

  public PassportDto passportNumber(String passportNumber) {
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

  public PassportDto placeOfIssue(String placeOfIssue) {
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
    PassportDto passportDto = (PassportDto) o;
    return Objects.equals(this.passportNumber, passportDto.passportNumber) &&
        Objects.equals(this.placeOfIssue, passportDto.placeOfIssue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(passportNumber, placeOfIssue);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PassportDto {\n");
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


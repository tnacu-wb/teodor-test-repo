package uk.co.whitbread.ohip.generated.models;

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
 * PersonNameTypeDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PersonNameTypeDto {

  private @Nullable String givenName;

  private @Nullable String nameTitle;

  private @Nullable String nameType;

  private @Nullable String surname;

  public PersonNameTypeDto givenName(String givenName) {
    this.givenName = givenName;
    return this;
  }

  /**
   * Get givenName
   * @return givenName
   */
  
  @Schema(name = "givenName", example = "John", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("givenName")
  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public PersonNameTypeDto nameTitle(String nameTitle) {
    this.nameTitle = nameTitle;
    return this;
  }

  /**
   * Get nameTitle
   * @return nameTitle
   */
  
  @Schema(name = "nameTitle", example = "mr", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nameTitle")
  public String getNameTitle() {
    return nameTitle;
  }

  public void setNameTitle(String nameTitle) {
    this.nameTitle = nameTitle;
  }

  public PersonNameTypeDto nameType(String nameType) {
    this.nameType = nameType;
    return this;
  }

  /**
   * Get nameType
   * @return nameType
   */
  
  @Schema(name = "nameType", example = "Primary", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nameType")
  public String getNameType() {
    return nameType;
  }

  public void setNameType(String nameType) {
    this.nameType = nameType;
  }

  public PersonNameTypeDto surname(String surname) {
    this.surname = surname;
    return this;
  }

  /**
   * Get surname
   * @return surname
   */
  
  @Schema(name = "surname", example = "Doe", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("surname")
  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PersonNameTypeDto personNameTypeDto = (PersonNameTypeDto) o;
    return Objects.equals(this.givenName, personNameTypeDto.givenName) &&
        Objects.equals(this.nameTitle, personNameTypeDto.nameTitle) &&
        Objects.equals(this.nameType, personNameTypeDto.nameType) &&
        Objects.equals(this.surname, personNameTypeDto.surname);
  }

  @Override
  public int hashCode() {
    return Objects.hash(givenName, nameTitle, nameType, surname);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PersonNameTypeDto {\n");
    sb.append("    givenName: ").append(toIndentedString(givenName)).append("\n");
    sb.append("    nameTitle: ").append(toIndentedString(nameTitle)).append("\n");
    sb.append("    nameType: ").append(toIndentedString(nameType)).append("\n");
    sb.append("    surname: ").append(toIndentedString(surname)).append("\n");
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


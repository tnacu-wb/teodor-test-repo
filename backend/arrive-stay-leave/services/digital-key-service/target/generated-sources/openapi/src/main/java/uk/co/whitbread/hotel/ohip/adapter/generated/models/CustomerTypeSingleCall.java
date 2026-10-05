package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PersonNameTypeSingleCall;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CustomerTypeSingleCall
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CustomerTypeSingleCall {

  @Valid
  private @Nullable List<@Valid PersonNameTypeSingleCall> personName;

  public CustomerTypeSingleCall personName(List<@Valid PersonNameTypeSingleCall> personName) {
    this.personName = personName;
    return this;
  }

  public CustomerTypeSingleCall addPersonNameItem(PersonNameTypeSingleCall personNameItem) {
    if (this.personName == null) {
      this.personName = new ArrayList<>();
    }
    this.personName.add(personNameItem);
    return this;
  }

  /**
   * Get personName
   * @return personName
   */
  @Valid 
  @Schema(name = "personName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("personName")
  public List<@Valid PersonNameTypeSingleCall> getPersonName() {
    return personName;
  }

  public void setPersonName(List<@Valid PersonNameTypeSingleCall> personName) {
    this.personName = personName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CustomerTypeSingleCall customerTypeSingleCall = (CustomerTypeSingleCall) o;
    return Objects.equals(this.personName, customerTypeSingleCall.personName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(personName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CustomerTypeSingleCall {\n");
    sb.append("    personName: ").append(toIndentedString(personName)).append("\n");
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


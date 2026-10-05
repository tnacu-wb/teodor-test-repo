package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PersonNameDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CheckInCustomerDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CheckInCustomerDto {

  @Valid
  private List<@Valid PersonNameDto> personName = new ArrayList<>();

  public CheckInCustomerDto personName(List<@Valid PersonNameDto> personName) {
    this.personName = personName;
    return this;
  }

  public CheckInCustomerDto addPersonNameItem(PersonNameDto personNameItem) {
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
  public List<@Valid PersonNameDto> getPersonName() {
    return personName;
  }

  public void setPersonName(List<@Valid PersonNameDto> personName) {
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
    CheckInCustomerDto checkInCustomerDto = (CheckInCustomerDto) o;
    return Objects.equals(this.personName, checkInCustomerDto.personName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(personName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CheckInCustomerDto {\n");
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


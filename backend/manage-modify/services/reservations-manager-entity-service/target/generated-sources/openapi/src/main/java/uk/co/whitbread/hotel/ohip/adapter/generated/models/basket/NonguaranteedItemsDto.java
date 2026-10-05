package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * NonguaranteedItemsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:44.119190+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class NonguaranteedItemsDto {

  private @Nullable String typeOfCaller;

  public NonguaranteedItemsDto typeOfCaller(String typeOfCaller) {
    this.typeOfCaller = typeOfCaller;
    return this;
  }

  /**
   * Get typeOfCaller
   * @return typeOfCaller
   */
  
  @Schema(name = "typeOfCaller", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("typeOfCaller")
  public String getTypeOfCaller() {
    return typeOfCaller;
  }

  public void setTypeOfCaller(String typeOfCaller) {
    this.typeOfCaller = typeOfCaller;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NonguaranteedItemsDto nonguaranteedItemsDto = (NonguaranteedItemsDto) o;
    return Objects.equals(this.typeOfCaller, nonguaranteedItemsDto.typeOfCaller);
  }

  @Override
  public int hashCode() {
    return Objects.hash(typeOfCaller);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NonguaranteedItemsDto {\n");
    sb.append("    typeOfCaller: ").append(toIndentedString(typeOfCaller)).append("\n");
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


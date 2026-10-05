package uk.co.whitbread.hotel.ohip.adapter.generated.models;

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
 * CompAccountingDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CompAccountingDto {

  private @Nullable String compPostings;

  public CompAccountingDto compPostings(String compPostings) {
    this.compPostings = compPostings;
    return this;
  }

  /**
   * Get compPostings
   * @return compPostings
   */
  
  @Schema(name = "compPostings", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("compPostings")
  public String getCompPostings() {
    return compPostings;
  }

  public void setCompPostings(String compPostings) {
    this.compPostings = compPostings;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompAccountingDto compAccountingDto = (CompAccountingDto) o;
    return Objects.equals(this.compPostings, compAccountingDto.compPostings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(compPostings);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CompAccountingDto {\n");
    sb.append("    compPostings: ").append(toIndentedString(compPostings)).append("\n");
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


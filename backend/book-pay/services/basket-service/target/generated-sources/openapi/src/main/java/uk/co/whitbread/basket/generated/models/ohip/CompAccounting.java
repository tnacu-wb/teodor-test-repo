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
 * CompAccounting
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CompAccounting {

  private @Nullable String compPostings;

  public CompAccounting compPostings(String compPostings) {
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
    CompAccounting compAccounting = (CompAccounting) o;
    return Objects.equals(this.compPostings, compAccounting.compPostings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(compPostings);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CompAccounting {\n");
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


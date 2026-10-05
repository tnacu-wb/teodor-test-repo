package uk.co.whitbread.basket.generated.models.content;

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
 * InfoDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:06.327224+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class InfoDto {

  private @Nullable String lift;

  public InfoDto lift(String lift) {
    this.lift = lift;
    return this;
  }

  /**
   * Get lift
   * @return lift
   */
  
  @Schema(name = "lift", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lift")
  public String getLift() {
    return lift;
  }

  public void setLift(String lift) {
    this.lift = lift;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InfoDto infoDto = (InfoDto) o;
    return Objects.equals(this.lift, infoDto.lift);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lift);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InfoDto {\n");
    sb.append("    lift: ").append(toIndentedString(lift)).append("\n");
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


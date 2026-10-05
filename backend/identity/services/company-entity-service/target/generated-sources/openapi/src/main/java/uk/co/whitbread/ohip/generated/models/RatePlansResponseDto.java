package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.RatePlanDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RatePlansResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RatePlansResponseDto {

  @Valid
  private List<@Valid RatePlanDto> ratePlans = new ArrayList<>();

  public RatePlansResponseDto ratePlans(List<@Valid RatePlanDto> ratePlans) {
    this.ratePlans = ratePlans;
    return this;
  }

  public RatePlansResponseDto addRatePlansItem(RatePlanDto ratePlansItem) {
    if (this.ratePlans == null) {
      this.ratePlans = new ArrayList<>();
    }
    this.ratePlans.add(ratePlansItem);
    return this;
  }

  /**
   * Get ratePlans
   * @return ratePlans
   */
  @Valid 
  @Schema(name = "ratePlans", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlans")
  public List<@Valid RatePlanDto> getRatePlans() {
    return ratePlans;
  }

  public void setRatePlans(List<@Valid RatePlanDto> ratePlans) {
    this.ratePlans = ratePlans;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RatePlansResponseDto ratePlansResponseDto = (RatePlansResponseDto) o;
    return Objects.equals(this.ratePlans, ratePlansResponseDto.ratePlans);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ratePlans);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RatePlansResponseDto {\n");
    sb.append("    ratePlans: ").append(toIndentedString(ratePlans)).append("\n");
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


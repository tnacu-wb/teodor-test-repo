package uk.co.whitbread.hotel.generated.models.basket;

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
 * PreAuthChargesDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:22.312200+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PreAuthChargesDto {

  private @Nullable String preAuthCharges;

  public PreAuthChargesDto preAuthCharges(String preAuthCharges) {
    this.preAuthCharges = preAuthCharges;
    return this;
  }

  /**
   * Get preAuthCharges
   * @return preAuthCharges
   */
  
  @Schema(name = "preAuthCharges", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("preAuthCharges")
  public String getPreAuthCharges() {
    return preAuthCharges;
  }

  public void setPreAuthCharges(String preAuthCharges) {
    this.preAuthCharges = preAuthCharges;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PreAuthChargesDto preAuthChargesDto = (PreAuthChargesDto) o;
    return Objects.equals(this.preAuthCharges, preAuthChargesDto.preAuthCharges);
  }

  @Override
  public int hashCode() {
    return Objects.hash(preAuthCharges);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PreAuthChargesDto {\n");
    sb.append("    preAuthCharges: ").append(toIndentedString(preAuthCharges)).append("\n");
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


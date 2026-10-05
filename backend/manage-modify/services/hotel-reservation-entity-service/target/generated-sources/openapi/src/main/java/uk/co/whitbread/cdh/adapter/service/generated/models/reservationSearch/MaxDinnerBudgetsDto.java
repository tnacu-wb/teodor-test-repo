package uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.GreaterLondonDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.IrelandDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.UkWideDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * MaxDinnerBudgetsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:32.868523+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MaxDinnerBudgetsDto {

  private @Nullable GreaterLondonDto greaterLondon;

  private @Nullable IrelandDto ireland;

  private @Nullable UkWideDto ukWide;

  public MaxDinnerBudgetsDto greaterLondon(GreaterLondonDto greaterLondon) {
    this.greaterLondon = greaterLondon;
    return this;
  }

  /**
   * Get greaterLondon
   * @return greaterLondon
   */
  @Valid 
  @Schema(name = "greaterLondon", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("greaterLondon")
  public GreaterLondonDto getGreaterLondon() {
    return greaterLondon;
  }

  public void setGreaterLondon(GreaterLondonDto greaterLondon) {
    this.greaterLondon = greaterLondon;
  }

  public MaxDinnerBudgetsDto ireland(IrelandDto ireland) {
    this.ireland = ireland;
    return this;
  }

  /**
   * Get ireland
   * @return ireland
   */
  @Valid 
  @Schema(name = "ireland", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ireland")
  public IrelandDto getIreland() {
    return ireland;
  }

  public void setIreland(IrelandDto ireland) {
    this.ireland = ireland;
  }

  public MaxDinnerBudgetsDto ukWide(UkWideDto ukWide) {
    this.ukWide = ukWide;
    return this;
  }

  /**
   * Get ukWide
   * @return ukWide
   */
  @Valid 
  @Schema(name = "ukWide", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ukWide")
  public UkWideDto getUkWide() {
    return ukWide;
  }

  public void setUkWide(UkWideDto ukWide) {
    this.ukWide = ukWide;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MaxDinnerBudgetsDto maxDinnerBudgetsDto = (MaxDinnerBudgetsDto) o;
    return Objects.equals(this.greaterLondon, maxDinnerBudgetsDto.greaterLondon) &&
        Objects.equals(this.ireland, maxDinnerBudgetsDto.ireland) &&
        Objects.equals(this.ukWide, maxDinnerBudgetsDto.ukWide);
  }

  @Override
  public int hashCode() {
    return Objects.hash(greaterLondon, ireland, ukWide);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MaxDinnerBudgetsDto {\n");
    sb.append("    greaterLondon: ").append(toIndentedString(greaterLondon)).append("\n");
    sb.append("    ireland: ").append(toIndentedString(ireland)).append("\n");
    sb.append("    ukWide: ").append(toIndentedString(ukWide)).append("\n");
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


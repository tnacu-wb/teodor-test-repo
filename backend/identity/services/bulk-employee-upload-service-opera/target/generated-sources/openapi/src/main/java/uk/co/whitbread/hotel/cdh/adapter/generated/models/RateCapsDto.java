package uk.co.whitbread.hotel.cdh.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.cdh.adapter.generated.models.GreaterLondonDto;
import uk.co.whitbread.hotel.cdh.adapter.generated.models.IrelandDto;
import uk.co.whitbread.hotel.cdh.adapter.generated.models.UkWideDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RateCapsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:35.320601+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RateCapsDto {

  private @Nullable GreaterLondonDto greaterLondon;

  private @Nullable IrelandDto ireland;

  private @Nullable UkWideDto ukWide;

  public RateCapsDto greaterLondon(GreaterLondonDto greaterLondon) {
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

  public RateCapsDto ireland(IrelandDto ireland) {
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

  public RateCapsDto ukWide(UkWideDto ukWide) {
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
    RateCapsDto rateCapsDto = (RateCapsDto) o;
    return Objects.equals(this.greaterLondon, rateCapsDto.greaterLondon) &&
        Objects.equals(this.ireland, rateCapsDto.ireland) &&
        Objects.equals(this.ukWide, rateCapsDto.ukWide);
  }

  @Override
  public int hashCode() {
    return Objects.hash(greaterLondon, ireland, ukWide);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateCapsDto {\n");
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


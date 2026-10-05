package uk.co.whitbread.payapp.generated.models.company;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import uk.co.whitbread.payapp.generated.models.company.PriceDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PriceCapLocationsDto
 */

@JsonTypeName("PriceCapLocations")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:38.523560+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PriceCapLocationsDto {

  private @Nullable PriceDto getuKWide;

  private @Nullable PriceDto greaterLondon;

  private @Nullable PriceDto ireland;

  public PriceCapLocationsDto getuKWide(PriceDto getuKWide) {
    this.getuKWide = getuKWide;
    return this;
  }

  /**
   * Get getuKWide
   * @return getuKWide
   */
  @Valid 
  @Schema(name = "getuKWide", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("getuKWide")
  public PriceDto getGetuKWide() {
    return getuKWide;
  }

  public void setGetuKWide(PriceDto getuKWide) {
    this.getuKWide = getuKWide;
  }

  public PriceCapLocationsDto greaterLondon(PriceDto greaterLondon) {
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
  public PriceDto getGreaterLondon() {
    return greaterLondon;
  }

  public void setGreaterLondon(PriceDto greaterLondon) {
    this.greaterLondon = greaterLondon;
  }

  public PriceCapLocationsDto ireland(PriceDto ireland) {
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
  public PriceDto getIreland() {
    return ireland;
  }

  public void setIreland(PriceDto ireland) {
    this.ireland = ireland;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PriceCapLocationsDto priceCapLocations = (PriceCapLocationsDto) o;
    return Objects.equals(this.getuKWide, priceCapLocations.getuKWide) &&
        Objects.equals(this.greaterLondon, priceCapLocations.greaterLondon) &&
        Objects.equals(this.ireland, priceCapLocations.ireland);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getuKWide, greaterLondon, ireland);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PriceCapLocationsDto {\n");
    sb.append("    getuKWide: ").append(toIndentedString(getuKWide)).append("\n");
    sb.append("    greaterLondon: ").append(toIndentedString(greaterLondon)).append("\n");
    sb.append("    ireland: ").append(toIndentedString(ireland)).append("\n");
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


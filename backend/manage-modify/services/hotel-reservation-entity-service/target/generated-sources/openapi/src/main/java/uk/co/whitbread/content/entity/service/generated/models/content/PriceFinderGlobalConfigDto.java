package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.PriceFinderConfigDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PriceFinderGlobalConfigDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PriceFinderGlobalConfigDto {

  private @Nullable PriceFinderConfigDto priceFinderConfig;

  public PriceFinderGlobalConfigDto priceFinderConfig(PriceFinderConfigDto priceFinderConfig) {
    this.priceFinderConfig = priceFinderConfig;
    return this;
  }

  /**
   * Get priceFinderConfig
   * @return priceFinderConfig
   */
  @Valid 
  @Schema(name = "priceFinderConfig", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("priceFinderConfig")
  public PriceFinderConfigDto getPriceFinderConfig() {
    return priceFinderConfig;
  }

  public void setPriceFinderConfig(PriceFinderConfigDto priceFinderConfig) {
    this.priceFinderConfig = priceFinderConfig;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PriceFinderGlobalConfigDto priceFinderGlobalConfigDto = (PriceFinderGlobalConfigDto) o;
    return Objects.equals(this.priceFinderConfig, priceFinderGlobalConfigDto.priceFinderConfig);
  }

  @Override
  public int hashCode() {
    return Objects.hash(priceFinderConfig);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PriceFinderGlobalConfigDto {\n");
    sb.append("    priceFinderConfig: ").append(toIndentedString(priceFinderConfig)).append("\n");
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


package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.PriceFinderViewsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PriceFinderConfigDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PriceFinderConfigDto {

  @Valid
  private List<@Valid PriceFinderViewsDto> priceFinderViews = new ArrayList<>();

  public PriceFinderConfigDto priceFinderViews(List<@Valid PriceFinderViewsDto> priceFinderViews) {
    this.priceFinderViews = priceFinderViews;
    return this;
  }

  public PriceFinderConfigDto addPriceFinderViewsItem(PriceFinderViewsDto priceFinderViewsItem) {
    if (this.priceFinderViews == null) {
      this.priceFinderViews = new ArrayList<>();
    }
    this.priceFinderViews.add(priceFinderViewsItem);
    return this;
  }

  /**
   * Get priceFinderViews
   * @return priceFinderViews
   */
  @Valid 
  @Schema(name = "priceFinderViews", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("priceFinderViews")
  public List<@Valid PriceFinderViewsDto> getPriceFinderViews() {
    return priceFinderViews;
  }

  public void setPriceFinderViews(List<@Valid PriceFinderViewsDto> priceFinderViews) {
    this.priceFinderViews = priceFinderViews;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PriceFinderConfigDto priceFinderConfigDto = (PriceFinderConfigDto) o;
    return Objects.equals(this.priceFinderViews, priceFinderConfigDto.priceFinderViews);
  }

  @Override
  public int hashCode() {
    return Objects.hash(priceFinderViews);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PriceFinderConfigDto {\n");
    sb.append("    priceFinderViews: ").append(toIndentedString(priceFinderViews)).append("\n");
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


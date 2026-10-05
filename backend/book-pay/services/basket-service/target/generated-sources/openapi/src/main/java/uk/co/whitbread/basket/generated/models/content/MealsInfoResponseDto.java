package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.content.UpsellItemsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * MealsInfoResponseDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:06.327224+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MealsInfoResponseDto {

  @Valid
  private List<@Valid UpsellItemsDto> upsellItems = new ArrayList<>();

  public MealsInfoResponseDto upsellItems(List<@Valid UpsellItemsDto> upsellItems) {
    this.upsellItems = upsellItems;
    return this;
  }

  public MealsInfoResponseDto addUpsellItemsItem(UpsellItemsDto upsellItemsItem) {
    if (this.upsellItems == null) {
      this.upsellItems = new ArrayList<>();
    }
    this.upsellItems.add(upsellItemsItem);
    return this;
  }

  /**
   * Get upsellItems
   * @return upsellItems
   */
  @Valid 
  @Schema(name = "upsellItems", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("upsellItems")
  public List<@Valid UpsellItemsDto> getUpsellItems() {
    return upsellItems;
  }

  public void setUpsellItems(List<@Valid UpsellItemsDto> upsellItems) {
    this.upsellItems = upsellItems;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MealsInfoResponseDto mealsInfoResponseDto = (MealsInfoResponseDto) o;
    return Objects.equals(this.upsellItems, mealsInfoResponseDto.upsellItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(upsellItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MealsInfoResponseDto {\n");
    sb.append("    upsellItems: ").append(toIndentedString(upsellItems)).append("\n");
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


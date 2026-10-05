package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.PromotionItemsResponseDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PromotionsConfigResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PromotionsConfigResponseDto {

  @Valid
  private List<@Valid PromotionItemsResponseDto> promoItems = new ArrayList<>();

  public PromotionsConfigResponseDto promoItems(List<@Valid PromotionItemsResponseDto> promoItems) {
    this.promoItems = promoItems;
    return this;
  }

  public PromotionsConfigResponseDto addPromoItemsItem(PromotionItemsResponseDto promoItemsItem) {
    if (this.promoItems == null) {
      this.promoItems = new ArrayList<>();
    }
    this.promoItems.add(promoItemsItem);
    return this;
  }

  /**
   * Get promoItems
   * @return promoItems
   */
  @Valid 
  @Schema(name = "promoItems", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promoItems")
  public List<@Valid PromotionItemsResponseDto> getPromoItems() {
    return promoItems;
  }

  public void setPromoItems(List<@Valid PromotionItemsResponseDto> promoItems) {
    this.promoItems = promoItems;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PromotionsConfigResponseDto promotionsConfigResponseDto = (PromotionsConfigResponseDto) o;
    return Objects.equals(this.promoItems, promotionsConfigResponseDto.promoItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(promoItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PromotionsConfigResponseDto {\n");
    sb.append("    promoItems: ").append(toIndentedString(promoItems)).append("\n");
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


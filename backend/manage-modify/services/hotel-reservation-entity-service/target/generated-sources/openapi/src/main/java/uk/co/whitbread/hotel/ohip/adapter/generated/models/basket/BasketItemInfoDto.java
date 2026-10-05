package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.AddBasketItemTypeDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketItemDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BasketItemInfoDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BasketItemInfoDto {

  @Valid
  private List<@Valid AddBasketItemTypeDto> basketItemTypes = new ArrayList<>();

  @Valid
  private List<@Valid BasketItemDto> basketItems = new ArrayList<>();

  public BasketItemInfoDto basketItemTypes(List<@Valid AddBasketItemTypeDto> basketItemTypes) {
    this.basketItemTypes = basketItemTypes;
    return this;
  }

  public BasketItemInfoDto addBasketItemTypesItem(AddBasketItemTypeDto basketItemTypesItem) {
    if (this.basketItemTypes == null) {
      this.basketItemTypes = new ArrayList<>();
    }
    this.basketItemTypes.add(basketItemTypesItem);
    return this;
  }

  /**
   * Get basketItemTypes
   * @return basketItemTypes
   */
  @Valid 
  @Schema(name = "basketItemTypes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("basketItemTypes")
  public List<@Valid AddBasketItemTypeDto> getBasketItemTypes() {
    return basketItemTypes;
  }

  public void setBasketItemTypes(List<@Valid AddBasketItemTypeDto> basketItemTypes) {
    this.basketItemTypes = basketItemTypes;
  }

  public BasketItemInfoDto basketItems(List<@Valid BasketItemDto> basketItems) {
    this.basketItems = basketItems;
    return this;
  }

  public BasketItemInfoDto addBasketItemsItem(BasketItemDto basketItemsItem) {
    if (this.basketItems == null) {
      this.basketItems = new ArrayList<>();
    }
    this.basketItems.add(basketItemsItem);
    return this;
  }

  /**
   * Get basketItems
   * @return basketItems
   */
  @Valid 
  @Schema(name = "basketItems", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("basketItems")
  public List<@Valid BasketItemDto> getBasketItems() {
    return basketItems;
  }

  public void setBasketItems(List<@Valid BasketItemDto> basketItems) {
    this.basketItems = basketItems;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BasketItemInfoDto basketItemInfoDto = (BasketItemInfoDto) o;
    return Objects.equals(this.basketItemTypes, basketItemInfoDto.basketItemTypes) &&
        Objects.equals(this.basketItems, basketItemInfoDto.basketItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(basketItemTypes, basketItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BasketItemInfoDto {\n");
    sb.append("    basketItemTypes: ").append(toIndentedString(basketItemTypes)).append("\n");
    sb.append("    basketItems: ").append(toIndentedString(basketItems)).append("\n");
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


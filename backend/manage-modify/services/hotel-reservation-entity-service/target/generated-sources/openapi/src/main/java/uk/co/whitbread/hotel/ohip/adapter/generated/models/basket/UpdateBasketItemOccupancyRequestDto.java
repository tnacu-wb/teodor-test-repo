package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.UpdateBasketItemSupplementDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * UpdateBasketItemOccupancyRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateBasketItemOccupancyRequestDto {

  @Valid
  private List<@Valid UpdateBasketItemSupplementDto> items = new ArrayList<>();

  public UpdateBasketItemOccupancyRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdateBasketItemOccupancyRequestDto(List<@Valid UpdateBasketItemSupplementDto> items) {
    this.items = items;
  }

  public UpdateBasketItemOccupancyRequestDto items(List<@Valid UpdateBasketItemSupplementDto> items) {
    this.items = items;
    return this;
  }

  public UpdateBasketItemOccupancyRequestDto addItemsItem(UpdateBasketItemSupplementDto itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }

  /**
   * Get items
   * @return items
   */
  @NotNull @Valid 
  @Schema(name = "items", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("items")
  public List<@Valid UpdateBasketItemSupplementDto> getItems() {
    return items;
  }

  public void setItems(List<@Valid UpdateBasketItemSupplementDto> items) {
    this.items = items;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateBasketItemOccupancyRequestDto updateBasketItemOccupancyRequestDto = (UpdateBasketItemOccupancyRequestDto) o;
    return Objects.equals(this.items, updateBasketItemOccupancyRequestDto.items);
  }

  @Override
  public int hashCode() {
    return Objects.hash(items);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateBasketItemOccupancyRequestDto {\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
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


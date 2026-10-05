package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.InventoryAvailabilityDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ItemInventoryDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ItemInventoryDto {

  private @Nullable String code;

  private @Nullable String description;

  @Valid
  private List<@Valid InventoryAvailabilityDto> inventories = new ArrayList<>();

  private @Nullable String name;

  public ItemInventoryDto code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
   */
  
  @Schema(name = "code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public ItemInventoryDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ItemInventoryDto inventories(List<@Valid InventoryAvailabilityDto> inventories) {
    this.inventories = inventories;
    return this;
  }

  public ItemInventoryDto addInventoriesItem(InventoryAvailabilityDto inventoriesItem) {
    if (this.inventories == null) {
      this.inventories = new ArrayList<>();
    }
    this.inventories.add(inventoriesItem);
    return this;
  }

  /**
   * Get inventories
   * @return inventories
   */
  @Valid 
  @Schema(name = "inventories", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("inventories")
  public List<@Valid InventoryAvailabilityDto> getInventories() {
    return inventories;
  }

  public void setInventories(List<@Valid InventoryAvailabilityDto> inventories) {
    this.inventories = inventories;
  }

  public ItemInventoryDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  
  @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ItemInventoryDto itemInventoryDto = (ItemInventoryDto) o;
    return Objects.equals(this.code, itemInventoryDto.code) &&
        Objects.equals(this.description, itemInventoryDto.description) &&
        Objects.equals(this.inventories, itemInventoryDto.inventories) &&
        Objects.equals(this.name, itemInventoryDto.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, description, inventories, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ItemInventoryDto {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    inventories: ").append(toIndentedString(inventories)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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


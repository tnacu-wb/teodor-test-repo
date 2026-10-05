package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.AddBasketItemDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.AddBasketItemTypeDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * AddBasketItemRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AddBasketItemRequestDto {

  @Valid
  private List<@Valid AddBasketItemTypeDto> itemTypes = new ArrayList<>();

  @Valid
  private List<@Valid AddBasketItemDto> items = new ArrayList<>();

  private @Nullable String lockingTime;

  private @Nullable Boolean migratedReservation;

  private String reference;

  public AddBasketItemRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AddBasketItemRequestDto(List<@Valid AddBasketItemTypeDto> itemTypes, List<@Valid AddBasketItemDto> items, String reference) {
    this.itemTypes = itemTypes;
    this.items = items;
    this.reference = reference;
  }

  public AddBasketItemRequestDto itemTypes(List<@Valid AddBasketItemTypeDto> itemTypes) {
    this.itemTypes = itemTypes;
    return this;
  }

  public AddBasketItemRequestDto addItemTypesItem(AddBasketItemTypeDto itemTypesItem) {
    if (this.itemTypes == null) {
      this.itemTypes = new ArrayList<>();
    }
    this.itemTypes.add(itemTypesItem);
    return this;
  }

  /**
   * Get itemTypes
   * @return itemTypes
   */
  @NotNull @Valid 
  @Schema(name = "itemTypes", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("itemTypes")
  public List<@Valid AddBasketItemTypeDto> getItemTypes() {
    return itemTypes;
  }

  public void setItemTypes(List<@Valid AddBasketItemTypeDto> itemTypes) {
    this.itemTypes = itemTypes;
  }

  public AddBasketItemRequestDto items(List<@Valid AddBasketItemDto> items) {
    this.items = items;
    return this;
  }

  public AddBasketItemRequestDto addItemsItem(AddBasketItemDto itemsItem) {
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
  public List<@Valid AddBasketItemDto> getItems() {
    return items;
  }

  public void setItems(List<@Valid AddBasketItemDto> items) {
    this.items = items;
  }

  public AddBasketItemRequestDto lockingTime(String lockingTime) {
    this.lockingTime = lockingTime;
    return this;
  }

  /**
   * Get lockingTime
   * @return lockingTime
   */
  
  @Schema(name = "lockingTime", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lockingTime")
  public String getLockingTime() {
    return lockingTime;
  }

  public void setLockingTime(String lockingTime) {
    this.lockingTime = lockingTime;
  }

  public AddBasketItemRequestDto migratedReservation(Boolean migratedReservation) {
    this.migratedReservation = migratedReservation;
    return this;
  }

  /**
   * Get migratedReservation
   * @return migratedReservation
   */
  
  @Schema(name = "migratedReservation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("migratedReservation")
  public Boolean getMigratedReservation() {
    return migratedReservation;
  }

  public void setMigratedReservation(Boolean migratedReservation) {
    this.migratedReservation = migratedReservation;
  }

  public AddBasketItemRequestDto reference(String reference) {
    this.reference = reference;
    return this;
  }

  /**
   * Get reference
   * @return reference
   */
  @NotNull 
  @Schema(name = "reference", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reference")
  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AddBasketItemRequestDto addBasketItemRequestDto = (AddBasketItemRequestDto) o;
    return Objects.equals(this.itemTypes, addBasketItemRequestDto.itemTypes) &&
        Objects.equals(this.items, addBasketItemRequestDto.items) &&
        Objects.equals(this.lockingTime, addBasketItemRequestDto.lockingTime) &&
        Objects.equals(this.migratedReservation, addBasketItemRequestDto.migratedReservation) &&
        Objects.equals(this.reference, addBasketItemRequestDto.reference);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemTypes, items, lockingTime, migratedReservation, reference);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AddBasketItemRequestDto {\n");
    sb.append("    itemTypes: ").append(toIndentedString(itemTypes)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
    sb.append("    lockingTime: ").append(toIndentedString(lockingTime)).append("\n");
    sb.append("    migratedReservation: ").append(toIndentedString(migratedReservation)).append("\n");
    sb.append("    reference: ").append(toIndentedString(reference)).append("\n");
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


package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketItemDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CreateBasketResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:44.119190+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CreateBasketResponseDto {

  private @Nullable String bookingReference;

  private @Nullable String channel;

  private @Nullable String createdAt;

  @Valid
  private List<@Valid BasketItemDto> items = new ArrayList<>();

  @Valid
  private Set<String> itemsTypes = new LinkedHashSet<>();

  private @Nullable String originalBasketId;

  private @Nullable String reference;

  private @Nullable String status;

  private @Nullable String userId;

  public CreateBasketResponseDto bookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
    return this;
  }

  /**
   * Get bookingReference
   * @return bookingReference
   */
  
  @Schema(name = "bookingReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingReference")
  public String getBookingReference() {
    return bookingReference;
  }

  public void setBookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
  }

  public CreateBasketResponseDto channel(String channel) {
    this.channel = channel;
    return this;
  }

  /**
   * Get channel
   * @return channel
   */
  
  @Schema(name = "channel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("channel")
  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public CreateBasketResponseDto createdAt(String createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
   */
  
  @Schema(name = "createdAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("createdAt")
  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public CreateBasketResponseDto items(List<@Valid BasketItemDto> items) {
    this.items = items;
    return this;
  }

  public CreateBasketResponseDto addItemsItem(BasketItemDto itemsItem) {
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
  @Valid 
  @Schema(name = "items", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("items")
  public List<@Valid BasketItemDto> getItems() {
    return items;
  }

  public void setItems(List<@Valid BasketItemDto> items) {
    this.items = items;
  }

  public CreateBasketResponseDto itemsTypes(Set<String> itemsTypes) {
    this.itemsTypes = itemsTypes;
    return this;
  }

  public CreateBasketResponseDto addItemsTypesItem(String itemsTypesItem) {
    if (this.itemsTypes == null) {
      this.itemsTypes = new LinkedHashSet<>();
    }
    this.itemsTypes.add(itemsTypesItem);
    return this;
  }

  /**
   * Get itemsTypes
   * @return itemsTypes
   */
  
  @Schema(name = "itemsTypes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("itemsTypes")
  public Set<String> getItemsTypes() {
    return itemsTypes;
  }

  @JsonDeserialize(as = LinkedHashSet.class)
  public void setItemsTypes(Set<String> itemsTypes) {
    this.itemsTypes = itemsTypes;
  }

  public CreateBasketResponseDto originalBasketId(String originalBasketId) {
    this.originalBasketId = originalBasketId;
    return this;
  }

  /**
   * Get originalBasketId
   * @return originalBasketId
   */
  
  @Schema(name = "originalBasketId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("originalBasketId")
  public String getOriginalBasketId() {
    return originalBasketId;
  }

  public void setOriginalBasketId(String originalBasketId) {
    this.originalBasketId = originalBasketId;
  }

  public CreateBasketResponseDto reference(String reference) {
    this.reference = reference;
    return this;
  }

  /**
   * Get reference
   * @return reference
   */
  
  @Schema(name = "reference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reference")
  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public CreateBasketResponseDto status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public CreateBasketResponseDto userId(String userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
   */
  
  @Schema(name = "userId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userId")
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateBasketResponseDto createBasketResponseDto = (CreateBasketResponseDto) o;
    return Objects.equals(this.bookingReference, createBasketResponseDto.bookingReference) &&
        Objects.equals(this.channel, createBasketResponseDto.channel) &&
        Objects.equals(this.createdAt, createBasketResponseDto.createdAt) &&
        Objects.equals(this.items, createBasketResponseDto.items) &&
        Objects.equals(this.itemsTypes, createBasketResponseDto.itemsTypes) &&
        Objects.equals(this.originalBasketId, createBasketResponseDto.originalBasketId) &&
        Objects.equals(this.reference, createBasketResponseDto.reference) &&
        Objects.equals(this.status, createBasketResponseDto.status) &&
        Objects.equals(this.userId, createBasketResponseDto.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingReference, channel, createdAt, items, itemsTypes, originalBasketId, reference, status, userId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateBasketResponseDto {\n");
    sb.append("    bookingReference: ").append(toIndentedString(bookingReference)).append("\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
    sb.append("    itemsTypes: ").append(toIndentedString(itemsTypes)).append("\n");
    sb.append("    originalBasketId: ").append(toIndentedString(originalBasketId)).append("\n");
    sb.append("    reference: ").append(toIndentedString(reference)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
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


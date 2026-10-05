package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.content.BookingFlowItemDto;
import uk.co.whitbread.basket.generated.models.content.TargetBookingFlowItemDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BookingFlowDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:24.587145+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BookingFlowDto {

  @Valid
  private List<@Valid BookingFlowItemDto> bookingFlowItems = new ArrayList<>();

  @Valid
  private List<@Valid TargetBookingFlowItemDto> targetBookingFlowItems = new ArrayList<>();

  public BookingFlowDto bookingFlowItems(List<@Valid BookingFlowItemDto> bookingFlowItems) {
    this.bookingFlowItems = bookingFlowItems;
    return this;
  }

  public BookingFlowDto addBookingFlowItemsItem(BookingFlowItemDto bookingFlowItemsItem) {
    if (this.bookingFlowItems == null) {
      this.bookingFlowItems = new ArrayList<>();
    }
    this.bookingFlowItems.add(bookingFlowItemsItem);
    return this;
  }

  /**
   * Get bookingFlowItems
   * @return bookingFlowItems
   */
  @Valid 
  @Schema(name = "bookingFlowItems", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingFlowItems")
  public List<@Valid BookingFlowItemDto> getBookingFlowItems() {
    return bookingFlowItems;
  }

  public void setBookingFlowItems(List<@Valid BookingFlowItemDto> bookingFlowItems) {
    this.bookingFlowItems = bookingFlowItems;
  }

  public BookingFlowDto targetBookingFlowItems(List<@Valid TargetBookingFlowItemDto> targetBookingFlowItems) {
    this.targetBookingFlowItems = targetBookingFlowItems;
    return this;
  }

  public BookingFlowDto addTargetBookingFlowItemsItem(TargetBookingFlowItemDto targetBookingFlowItemsItem) {
    if (this.targetBookingFlowItems == null) {
      this.targetBookingFlowItems = new ArrayList<>();
    }
    this.targetBookingFlowItems.add(targetBookingFlowItemsItem);
    return this;
  }

  /**
   * Get targetBookingFlowItems
   * @return targetBookingFlowItems
   */
  @Valid 
  @Schema(name = "targetBookingFlowItems", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("targetBookingFlowItems")
  public List<@Valid TargetBookingFlowItemDto> getTargetBookingFlowItems() {
    return targetBookingFlowItems;
  }

  public void setTargetBookingFlowItems(List<@Valid TargetBookingFlowItemDto> targetBookingFlowItems) {
    this.targetBookingFlowItems = targetBookingFlowItems;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookingFlowDto bookingFlowDto = (BookingFlowDto) o;
    return Objects.equals(this.bookingFlowItems, bookingFlowDto.bookingFlowItems) &&
        Objects.equals(this.targetBookingFlowItems, bookingFlowDto.targetBookingFlowItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingFlowItems, targetBookingFlowItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookingFlowDto {\n");
    sb.append("    bookingFlowItems: ").append(toIndentedString(bookingFlowItems)).append("\n");
    sb.append("    targetBookingFlowItems: ").append(toIndentedString(targetBookingFlowItems)).append("\n");
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


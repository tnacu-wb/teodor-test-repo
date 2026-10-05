package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.HotelAvailabilityStatus;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferCalendarItem;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.PropertySearchPropertyInfo;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PropertyCalendarRoomStay
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PropertyCalendarRoomStay {

  private @Nullable PropertySearchPropertyInfo propertyInfo;

  private @Nullable HotelAvailabilityStatus availability;

  @Valid
  private List<@Valid OfferCalendarItem> calendarItems = new ArrayList<>();

  public PropertyCalendarRoomStay propertyInfo(PropertySearchPropertyInfo propertyInfo) {
    this.propertyInfo = propertyInfo;
    return this;
  }

  /**
   * Get propertyInfo
   * @return propertyInfo
   */
  @Valid 
  @Schema(name = "propertyInfo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("propertyInfo")
  public PropertySearchPropertyInfo getPropertyInfo() {
    return propertyInfo;
  }

  public void setPropertyInfo(PropertySearchPropertyInfo propertyInfo) {
    this.propertyInfo = propertyInfo;
  }

  public PropertyCalendarRoomStay availability(HotelAvailabilityStatus availability) {
    this.availability = availability;
    return this;
  }

  /**
   * Get availability
   * @return availability
   */
  @Valid 
  @Schema(name = "availability", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("availability")
  public HotelAvailabilityStatus getAvailability() {
    return availability;
  }

  public void setAvailability(HotelAvailabilityStatus availability) {
    this.availability = availability;
  }

  public PropertyCalendarRoomStay calendarItems(List<@Valid OfferCalendarItem> calendarItems) {
    this.calendarItems = calendarItems;
    return this;
  }

  public PropertyCalendarRoomStay addCalendarItemsItem(OfferCalendarItem calendarItemsItem) {
    if (this.calendarItems == null) {
      this.calendarItems = new ArrayList<>();
    }
    this.calendarItems.add(calendarItemsItem);
    return this;
  }

  /**
   * List of rates starting from arrival date provided on the request, availability status of the rate, and rate detail.
   * @return calendarItems
   */
  @Valid 
  @Schema(name = "calendarItems", description = "List of rates starting from arrival date provided on the request, availability status of the rate, and rate detail.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("calendarItems")
  public List<@Valid OfferCalendarItem> getCalendarItems() {
    return calendarItems;
  }

  public void setCalendarItems(List<@Valid OfferCalendarItem> calendarItems) {
    this.calendarItems = calendarItems;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PropertyCalendarRoomStay propertyCalendarRoomStay = (PropertyCalendarRoomStay) o;
    return Objects.equals(this.propertyInfo, propertyCalendarRoomStay.propertyInfo) &&
        Objects.equals(this.availability, propertyCalendarRoomStay.availability) &&
        Objects.equals(this.calendarItems, propertyCalendarRoomStay.calendarItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(propertyInfo, availability, calendarItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PropertyCalendarRoomStay {\n");
    sb.append("    propertyInfo: ").append(toIndentedString(propertyInfo)).append("\n");
    sb.append("    availability: ").append(toIndentedString(availability)).append("\n");
    sb.append("    calendarItems: ").append(toIndentedString(calendarItems)).append("\n");
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


package uk.co.whitbread.hotel.entity.service.generated.models.hotel;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.entity.service.generated.models.hotel.RoomRateV2Dto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * RoomTypeV2Dto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:33.749132+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomTypeV2Dto {

  private @Nullable String adults;

  private @Nullable String children;

  private @Nullable String numberOfRooms;

  @Valid
  private List<@Valid RoomRateV2Dto> roomRates = new ArrayList<>();

  private @Nullable String roomType;

  @Valid
  private List<String> specialRequests = new ArrayList<>();

  private @Nullable String tag;

  public RoomTypeV2Dto adults(String adults) {
    this.adults = adults;
    return this;
  }

  /**
   * Get adults
   * @return adults
   */
  
  @Schema(name = "adults", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adults")
  public String getAdults() {
    return adults;
  }

  public void setAdults(String adults) {
    this.adults = adults;
  }

  public RoomTypeV2Dto children(String children) {
    this.children = children;
    return this;
  }

  /**
   * Get children
   * @return children
   */
  
  @Schema(name = "children", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("children")
  public String getChildren() {
    return children;
  }

  public void setChildren(String children) {
    this.children = children;
  }

  public RoomTypeV2Dto numberOfRooms(String numberOfRooms) {
    this.numberOfRooms = numberOfRooms;
    return this;
  }

  /**
   * Get numberOfRooms
   * @return numberOfRooms
   */
  
  @Schema(name = "numberOfRooms", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("numberOfRooms")
  public String getNumberOfRooms() {
    return numberOfRooms;
  }

  public void setNumberOfRooms(String numberOfRooms) {
    this.numberOfRooms = numberOfRooms;
  }

  public RoomTypeV2Dto roomRates(List<@Valid RoomRateV2Dto> roomRates) {
    this.roomRates = roomRates;
    return this;
  }

  public RoomTypeV2Dto addRoomRatesItem(RoomRateV2Dto roomRatesItem) {
    if (this.roomRates == null) {
      this.roomRates = new ArrayList<>();
    }
    this.roomRates.add(roomRatesItem);
    return this;
  }

  /**
   * Get roomRates
   * @return roomRates
   */
  @Valid 
  @Schema(name = "roomRates", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomRates")
  public List<@Valid RoomRateV2Dto> getRoomRates() {
    return roomRates;
  }

  public void setRoomRates(List<@Valid RoomRateV2Dto> roomRates) {
    this.roomRates = roomRates;
  }

  public RoomTypeV2Dto roomType(String roomType) {
    this.roomType = roomType;
    return this;
  }

  /**
   * Get roomType
   * @return roomType
   */
  
  @Schema(name = "roomType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomType")
  public String getRoomType() {
    return roomType;
  }

  public void setRoomType(String roomType) {
    this.roomType = roomType;
  }

  public RoomTypeV2Dto specialRequests(List<String> specialRequests) {
    this.specialRequests = specialRequests;
    return this;
  }

  public RoomTypeV2Dto addSpecialRequestsItem(String specialRequestsItem) {
    if (this.specialRequests == null) {
      this.specialRequests = new ArrayList<>();
    }
    this.specialRequests.add(specialRequestsItem);
    return this;
  }

  /**
   * Get specialRequests
   * @return specialRequests
   */
  
  @Schema(name = "specialRequests", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("specialRequests")
  public List<String> getSpecialRequests() {
    return specialRequests;
  }

  public void setSpecialRequests(List<String> specialRequests) {
    this.specialRequests = specialRequests;
  }

  public RoomTypeV2Dto tag(String tag) {
    this.tag = tag;
    return this;
  }

  /**
   * Get tag
   * @return tag
   */
  
  @Schema(name = "tag", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tag")
  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomTypeV2Dto roomTypeV2Dto = (RoomTypeV2Dto) o;
    return Objects.equals(this.adults, roomTypeV2Dto.adults) &&
        Objects.equals(this.children, roomTypeV2Dto.children) &&
        Objects.equals(this.numberOfRooms, roomTypeV2Dto.numberOfRooms) &&
        Objects.equals(this.roomRates, roomTypeV2Dto.roomRates) &&
        Objects.equals(this.roomType, roomTypeV2Dto.roomType) &&
        Objects.equals(this.specialRequests, roomTypeV2Dto.specialRequests) &&
        Objects.equals(this.tag, roomTypeV2Dto.tag);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adults, children, numberOfRooms, roomRates, roomType, specialRequests, tag);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomTypeV2Dto {\n");
    sb.append("    adults: ").append(toIndentedString(adults)).append("\n");
    sb.append("    children: ").append(toIndentedString(children)).append("\n");
    sb.append("    numberOfRooms: ").append(toIndentedString(numberOfRooms)).append("\n");
    sb.append("    roomRates: ").append(toIndentedString(roomRates)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
    sb.append("    specialRequests: ").append(toIndentedString(specialRequests)).append("\n");
    sb.append("    tag: ").append(toIndentedString(tag)).append("\n");
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


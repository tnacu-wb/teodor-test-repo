package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.content.GalleryImageDto;
import uk.co.whitbread.basket.generated.models.content.HotelFacilityDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * TabItemDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:06.327224+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class TabItemDto {

  @Valid
  private List<@Valid HotelFacilityDto> facilities = new ArrayList<>();

  @Valid
  private List<@Valid GalleryImageDto> images = new ArrayList<>();

  private @Nullable String roomDescription;

  private @Nullable String roomName;

  private @Nullable String roomType;

  public TabItemDto facilities(List<@Valid HotelFacilityDto> facilities) {
    this.facilities = facilities;
    return this;
  }

  public TabItemDto addFacilitiesItem(HotelFacilityDto facilitiesItem) {
    if (this.facilities == null) {
      this.facilities = new ArrayList<>();
    }
    this.facilities.add(facilitiesItem);
    return this;
  }

  /**
   * Get facilities
   * @return facilities
   */
  @Valid 
  @Schema(name = "facilities", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("facilities")
  public List<@Valid HotelFacilityDto> getFacilities() {
    return facilities;
  }

  public void setFacilities(List<@Valid HotelFacilityDto> facilities) {
    this.facilities = facilities;
  }

  public TabItemDto images(List<@Valid GalleryImageDto> images) {
    this.images = images;
    return this;
  }

  public TabItemDto addImagesItem(GalleryImageDto imagesItem) {
    if (this.images == null) {
      this.images = new ArrayList<>();
    }
    this.images.add(imagesItem);
    return this;
  }

  /**
   * Get images
   * @return images
   */
  @Valid 
  @Schema(name = "images", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("images")
  public List<@Valid GalleryImageDto> getImages() {
    return images;
  }

  public void setImages(List<@Valid GalleryImageDto> images) {
    this.images = images;
  }

  public TabItemDto roomDescription(String roomDescription) {
    this.roomDescription = roomDescription;
    return this;
  }

  /**
   * Get roomDescription
   * @return roomDescription
   */
  
  @Schema(name = "roomDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomDescription")
  public String getRoomDescription() {
    return roomDescription;
  }

  public void setRoomDescription(String roomDescription) {
    this.roomDescription = roomDescription;
  }

  public TabItemDto roomName(String roomName) {
    this.roomName = roomName;
    return this;
  }

  /**
   * Get roomName
   * @return roomName
   */
  
  @Schema(name = "roomName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomName")
  public String getRoomName() {
    return roomName;
  }

  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }

  public TabItemDto roomType(String roomType) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TabItemDto tabItemDto = (TabItemDto) o;
    return Objects.equals(this.facilities, tabItemDto.facilities) &&
        Objects.equals(this.images, tabItemDto.images) &&
        Objects.equals(this.roomDescription, tabItemDto.roomDescription) &&
        Objects.equals(this.roomName, tabItemDto.roomName) &&
        Objects.equals(this.roomType, tabItemDto.roomType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(facilities, images, roomDescription, roomName, roomType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TabItemDto {\n");
    sb.append("    facilities: ").append(toIndentedString(facilities)).append("\n");
    sb.append("    images: ").append(toIndentedString(images)).append("\n");
    sb.append("    roomDescription: ").append(toIndentedString(roomDescription)).append("\n");
    sb.append("    roomName: ").append(toIndentedString(roomName)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
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


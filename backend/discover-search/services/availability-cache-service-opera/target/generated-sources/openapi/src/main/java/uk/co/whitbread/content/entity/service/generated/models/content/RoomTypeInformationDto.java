package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomTypeInformationDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomTypeInformationDto {

  @Valid
  private List<String> facilities = new ArrayList<>();

  private @Nullable String gridImage;

  private @Nullable String groupId;

  private @Nullable String roomCategory;

  private @Nullable String roomDescription;

  private @Nullable String roomImage;

  private @Nullable String roomInfo;

  private @Nullable String roomInfoLabel;

  private @Nullable String roomLabel;

  @Valid
  private List<String> roomTypeCode = new ArrayList<>();

  public RoomTypeInformationDto facilities(List<String> facilities) {
    this.facilities = facilities;
    return this;
  }

  public RoomTypeInformationDto addFacilitiesItem(String facilitiesItem) {
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
  
  @Schema(name = "facilities", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("facilities")
  public List<String> getFacilities() {
    return facilities;
  }

  public void setFacilities(List<String> facilities) {
    this.facilities = facilities;
  }

  public RoomTypeInformationDto gridImage(String gridImage) {
    this.gridImage = gridImage;
    return this;
  }

  /**
   * Get gridImage
   * @return gridImage
   */
  
  @Schema(name = "gridImage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("gridImage")
  public String getGridImage() {
    return gridImage;
  }

  public void setGridImage(String gridImage) {
    this.gridImage = gridImage;
  }

  public RoomTypeInformationDto groupId(String groupId) {
    this.groupId = groupId;
    return this;
  }

  /**
   * Get groupId
   * @return groupId
   */
  
  @Schema(name = "groupId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("groupId")
  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public RoomTypeInformationDto roomCategory(String roomCategory) {
    this.roomCategory = roomCategory;
    return this;
  }

  /**
   * Get roomCategory
   * @return roomCategory
   */
  
  @Schema(name = "roomCategory", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomCategory")
  public String getRoomCategory() {
    return roomCategory;
  }

  public void setRoomCategory(String roomCategory) {
    this.roomCategory = roomCategory;
  }

  public RoomTypeInformationDto roomDescription(String roomDescription) {
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

  public RoomTypeInformationDto roomImage(String roomImage) {
    this.roomImage = roomImage;
    return this;
  }

  /**
   * Get roomImage
   * @return roomImage
   */
  
  @Schema(name = "roomImage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomImage")
  public String getRoomImage() {
    return roomImage;
  }

  public void setRoomImage(String roomImage) {
    this.roomImage = roomImage;
  }

  public RoomTypeInformationDto roomInfo(String roomInfo) {
    this.roomInfo = roomInfo;
    return this;
  }

  /**
   * Get roomInfo
   * @return roomInfo
   */
  
  @Schema(name = "roomInfo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomInfo")
  public String getRoomInfo() {
    return roomInfo;
  }

  public void setRoomInfo(String roomInfo) {
    this.roomInfo = roomInfo;
  }

  public RoomTypeInformationDto roomInfoLabel(String roomInfoLabel) {
    this.roomInfoLabel = roomInfoLabel;
    return this;
  }

  /**
   * Get roomInfoLabel
   * @return roomInfoLabel
   */
  
  @Schema(name = "roomInfoLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomInfoLabel")
  public String getRoomInfoLabel() {
    return roomInfoLabel;
  }

  public void setRoomInfoLabel(String roomInfoLabel) {
    this.roomInfoLabel = roomInfoLabel;
  }

  public RoomTypeInformationDto roomLabel(String roomLabel) {
    this.roomLabel = roomLabel;
    return this;
  }

  /**
   * Get roomLabel
   * @return roomLabel
   */
  
  @Schema(name = "roomLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomLabel")
  public String getRoomLabel() {
    return roomLabel;
  }

  public void setRoomLabel(String roomLabel) {
    this.roomLabel = roomLabel;
  }

  public RoomTypeInformationDto roomTypeCode(List<String> roomTypeCode) {
    this.roomTypeCode = roomTypeCode;
    return this;
  }

  public RoomTypeInformationDto addRoomTypeCodeItem(String roomTypeCodeItem) {
    if (this.roomTypeCode == null) {
      this.roomTypeCode = new ArrayList<>();
    }
    this.roomTypeCode.add(roomTypeCodeItem);
    return this;
  }

  /**
   * Get roomTypeCode
   * @return roomTypeCode
   */
  
  @Schema(name = "roomTypeCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomTypeCode")
  public List<String> getRoomTypeCode() {
    return roomTypeCode;
  }

  public void setRoomTypeCode(List<String> roomTypeCode) {
    this.roomTypeCode = roomTypeCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomTypeInformationDto roomTypeInformationDto = (RoomTypeInformationDto) o;
    return Objects.equals(this.facilities, roomTypeInformationDto.facilities) &&
        Objects.equals(this.gridImage, roomTypeInformationDto.gridImage) &&
        Objects.equals(this.groupId, roomTypeInformationDto.groupId) &&
        Objects.equals(this.roomCategory, roomTypeInformationDto.roomCategory) &&
        Objects.equals(this.roomDescription, roomTypeInformationDto.roomDescription) &&
        Objects.equals(this.roomImage, roomTypeInformationDto.roomImage) &&
        Objects.equals(this.roomInfo, roomTypeInformationDto.roomInfo) &&
        Objects.equals(this.roomInfoLabel, roomTypeInformationDto.roomInfoLabel) &&
        Objects.equals(this.roomLabel, roomTypeInformationDto.roomLabel) &&
        Objects.equals(this.roomTypeCode, roomTypeInformationDto.roomTypeCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(facilities, gridImage, groupId, roomCategory, roomDescription, roomImage, roomInfo, roomInfoLabel, roomLabel, roomTypeCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomTypeInformationDto {\n");
    sb.append("    facilities: ").append(toIndentedString(facilities)).append("\n");
    sb.append("    gridImage: ").append(toIndentedString(gridImage)).append("\n");
    sb.append("    groupId: ").append(toIndentedString(groupId)).append("\n");
    sb.append("    roomCategory: ").append(toIndentedString(roomCategory)).append("\n");
    sb.append("    roomDescription: ").append(toIndentedString(roomDescription)).append("\n");
    sb.append("    roomImage: ").append(toIndentedString(roomImage)).append("\n");
    sb.append("    roomInfo: ").append(toIndentedString(roomInfo)).append("\n");
    sb.append("    roomInfoLabel: ").append(toIndentedString(roomInfoLabel)).append("\n");
    sb.append("    roomLabel: ").append(toIndentedString(roomLabel)).append("\n");
    sb.append("    roomTypeCode: ").append(toIndentedString(roomTypeCode)).append("\n");
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


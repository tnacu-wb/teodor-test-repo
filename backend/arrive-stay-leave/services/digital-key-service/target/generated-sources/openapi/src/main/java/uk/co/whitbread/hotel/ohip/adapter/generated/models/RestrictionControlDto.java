package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RestrictionControlDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RestrictionControlDto {

  private @Nullable Boolean house;

  private @Nullable String ratePlanCategory;

  private @Nullable String ratePlanCode;

  private @Nullable String roomClass;

  private @Nullable String roomType;

  public RestrictionControlDto house(Boolean house) {
    this.house = house;
    return this;
  }

  /**
   * Get house
   * @return house
   */
  
  @Schema(name = "house", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("house")
  public Boolean getHouse() {
    return house;
  }

  public void setHouse(Boolean house) {
    this.house = house;
  }

  public RestrictionControlDto ratePlanCategory(String ratePlanCategory) {
    this.ratePlanCategory = ratePlanCategory;
    return this;
  }

  /**
   * Get ratePlanCategory
   * @return ratePlanCategory
   */
  
  @Schema(name = "ratePlanCategory", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanCategory")
  public String getRatePlanCategory() {
    return ratePlanCategory;
  }

  public void setRatePlanCategory(String ratePlanCategory) {
    this.ratePlanCategory = ratePlanCategory;
  }

  public RestrictionControlDto ratePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
    return this;
  }

  /**
   * Get ratePlanCode
   * @return ratePlanCode
   */
  
  @Schema(name = "ratePlanCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanCode")
  public String getRatePlanCode() {
    return ratePlanCode;
  }

  public void setRatePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
  }

  public RestrictionControlDto roomClass(String roomClass) {
    this.roomClass = roomClass;
    return this;
  }

  /**
   * Get roomClass
   * @return roomClass
   */
  
  @Schema(name = "roomClass", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomClass")
  public String getRoomClass() {
    return roomClass;
  }

  public void setRoomClass(String roomClass) {
    this.roomClass = roomClass;
  }

  public RestrictionControlDto roomType(String roomType) {
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
    RestrictionControlDto restrictionControlDto = (RestrictionControlDto) o;
    return Objects.equals(this.house, restrictionControlDto.house) &&
        Objects.equals(this.ratePlanCategory, restrictionControlDto.ratePlanCategory) &&
        Objects.equals(this.ratePlanCode, restrictionControlDto.ratePlanCode) &&
        Objects.equals(this.roomClass, restrictionControlDto.roomClass) &&
        Objects.equals(this.roomType, restrictionControlDto.roomType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(house, ratePlanCategory, ratePlanCode, roomClass, roomType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RestrictionControlDto {\n");
    sb.append("    house: ").append(toIndentedString(house)).append("\n");
    sb.append("    ratePlanCategory: ").append(toIndentedString(ratePlanCategory)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    roomClass: ").append(toIndentedString(roomClass)).append("\n");
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


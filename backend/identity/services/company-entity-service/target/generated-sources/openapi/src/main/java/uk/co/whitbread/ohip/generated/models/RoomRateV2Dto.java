package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.RoomRateInfoV2Dto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomRateV2Dto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomRateV2Dto {

  private @Nullable String currencyCode;

  private @Nullable String displaySet;

  private @Nullable String ratePlanCode;

  private @Nullable RoomRateInfoV2Dto roomRateInfo;

  public RoomRateV2Dto currencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
    return this;
  }

  /**
   * Get currencyCode
   * @return currencyCode
   */
  
  @Schema(name = "currencyCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currencyCode")
  public String getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
  }

  public RoomRateV2Dto displaySet(String displaySet) {
    this.displaySet = displaySet;
    return this;
  }

  /**
   * Get displaySet
   * @return displaySet
   */
  
  @Schema(name = "displaySet", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("displaySet")
  public String getDisplaySet() {
    return displaySet;
  }

  public void setDisplaySet(String displaySet) {
    this.displaySet = displaySet;
  }

  public RoomRateV2Dto ratePlanCode(String ratePlanCode) {
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

  public RoomRateV2Dto roomRateInfo(RoomRateInfoV2Dto roomRateInfo) {
    this.roomRateInfo = roomRateInfo;
    return this;
  }

  /**
   * Get roomRateInfo
   * @return roomRateInfo
   */
  @Valid 
  @Schema(name = "roomRateInfo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomRateInfo")
  public RoomRateInfoV2Dto getRoomRateInfo() {
    return roomRateInfo;
  }

  public void setRoomRateInfo(RoomRateInfoV2Dto roomRateInfo) {
    this.roomRateInfo = roomRateInfo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomRateV2Dto roomRateV2Dto = (RoomRateV2Dto) o;
    return Objects.equals(this.currencyCode, roomRateV2Dto.currencyCode) &&
        Objects.equals(this.displaySet, roomRateV2Dto.displaySet) &&
        Objects.equals(this.ratePlanCode, roomRateV2Dto.ratePlanCode) &&
        Objects.equals(this.roomRateInfo, roomRateV2Dto.roomRateInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currencyCode, displaySet, ratePlanCode, roomRateInfo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomRateV2Dto {\n");
    sb.append("    currencyCode: ").append(toIndentedString(currencyCode)).append("\n");
    sb.append("    displaySet: ").append(toIndentedString(displaySet)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    roomRateInfo: ").append(toIndentedString(roomRateInfo)).append("\n");
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


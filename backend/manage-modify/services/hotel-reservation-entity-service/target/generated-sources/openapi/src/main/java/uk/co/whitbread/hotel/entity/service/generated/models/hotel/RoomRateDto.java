package uk.co.whitbread.hotel.entity.service.generated.models.hotel;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.entity.service.generated.models.hotel.RoomTypeDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * RoomRateDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:33.749132+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomRateDto {

  private @Nullable String cellCode;

  private @Nullable String globalCompanyId;

  private @Nullable String promotionCode;

  private String rateDisplaySet;

  private String ratePlanCode;

  @Valid
  private List<@Valid RoomTypeDto> roomTypes = new ArrayList<>();

  private @Nullable Boolean twinRoomTypeAvailability;

  public RoomRateDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RoomRateDto(String rateDisplaySet, String ratePlanCode) {
    this.rateDisplaySet = rateDisplaySet;
    this.ratePlanCode = ratePlanCode;
  }

  public RoomRateDto cellCode(String cellCode) {
    this.cellCode = cellCode;
    return this;
  }

  /**
   * Get cellCode
   * @return cellCode
   */
  
  @Schema(name = "cellCode", example = "EMP01", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cellCode")
  public String getCellCode() {
    return cellCode;
  }

  public void setCellCode(String cellCode) {
    this.cellCode = cellCode;
  }

  public RoomRateDto globalCompanyId(String globalCompanyId) {
    this.globalCompanyId = globalCompanyId;
    return this;
  }

  /**
   * Get globalCompanyId
   * @return globalCompanyId
   */
  
  @Schema(name = "globalCompanyId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("globalCompanyId")
  public String getGlobalCompanyId() {
    return globalCompanyId;
  }

  public void setGlobalCompanyId(String globalCompanyId) {
    this.globalCompanyId = globalCompanyId;
  }

  public RoomRateDto promotionCode(String promotionCode) {
    this.promotionCode = promotionCode;
    return this;
  }

  /**
   * Get promotionCode
   * @return promotionCode
   */
  
  @Schema(name = "promotionCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promotionCode")
  public String getPromotionCode() {
    return promotionCode;
  }

  public void setPromotionCode(String promotionCode) {
    this.promotionCode = promotionCode;
  }

  public RoomRateDto rateDisplaySet(String rateDisplaySet) {
    this.rateDisplaySet = rateDisplaySet;
    return this;
  }

  /**
   * Get rateDisplaySet
   * @return rateDisplaySet
   */
  @NotNull 
  @Schema(name = "rateDisplaySet", example = "PBF", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("rateDisplaySet")
  public String getRateDisplaySet() {
    return rateDisplaySet;
  }

  public void setRateDisplaySet(String rateDisplaySet) {
    this.rateDisplaySet = rateDisplaySet;
  }

  public RoomRateDto ratePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
    return this;
  }

  /**
   * Get ratePlanCode
   * @return ratePlanCode
   */
  @NotNull 
  @Schema(name = "ratePlanCode", example = "FLEXRATE", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ratePlanCode")
  public String getRatePlanCode() {
    return ratePlanCode;
  }

  public void setRatePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
  }

  public RoomRateDto roomTypes(List<@Valid RoomTypeDto> roomTypes) {
    this.roomTypes = roomTypes;
    return this;
  }

  public RoomRateDto addRoomTypesItem(RoomTypeDto roomTypesItem) {
    if (this.roomTypes == null) {
      this.roomTypes = new ArrayList<>();
    }
    this.roomTypes.add(roomTypesItem);
    return this;
  }

  /**
   * Get roomTypes
   * @return roomTypes
   */
  @Valid 
  @Schema(name = "roomTypes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomTypes")
  public List<@Valid RoomTypeDto> getRoomTypes() {
    return roomTypes;
  }

  public void setRoomTypes(List<@Valid RoomTypeDto> roomTypes) {
    this.roomTypes = roomTypes;
  }

  public RoomRateDto twinRoomTypeAvailability(Boolean twinRoomTypeAvailability) {
    this.twinRoomTypeAvailability = twinRoomTypeAvailability;
    return this;
  }

  /**
   * Get twinRoomTypeAvailability
   * @return twinRoomTypeAvailability
   */
  
  @Schema(name = "twinRoomTypeAvailability", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("twinRoomTypeAvailability")
  public Boolean getTwinRoomTypeAvailability() {
    return twinRoomTypeAvailability;
  }

  public void setTwinRoomTypeAvailability(Boolean twinRoomTypeAvailability) {
    this.twinRoomTypeAvailability = twinRoomTypeAvailability;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomRateDto roomRateDto = (RoomRateDto) o;
    return Objects.equals(this.cellCode, roomRateDto.cellCode) &&
        Objects.equals(this.globalCompanyId, roomRateDto.globalCompanyId) &&
        Objects.equals(this.promotionCode, roomRateDto.promotionCode) &&
        Objects.equals(this.rateDisplaySet, roomRateDto.rateDisplaySet) &&
        Objects.equals(this.ratePlanCode, roomRateDto.ratePlanCode) &&
        Objects.equals(this.roomTypes, roomRateDto.roomTypes) &&
        Objects.equals(this.twinRoomTypeAvailability, roomRateDto.twinRoomTypeAvailability);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cellCode, globalCompanyId, promotionCode, rateDisplaySet, ratePlanCode, roomTypes, twinRoomTypeAvailability);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomRateDto {\n");
    sb.append("    cellCode: ").append(toIndentedString(cellCode)).append("\n");
    sb.append("    globalCompanyId: ").append(toIndentedString(globalCompanyId)).append("\n");
    sb.append("    promotionCode: ").append(toIndentedString(promotionCode)).append("\n");
    sb.append("    rateDisplaySet: ").append(toIndentedString(rateDisplaySet)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    roomTypes: ").append(toIndentedString(roomTypes)).append("\n");
    sb.append("    twinRoomTypeAvailability: ").append(toIndentedString(twinRoomTypeAvailability)).append("\n");
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


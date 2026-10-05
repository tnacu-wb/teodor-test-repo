package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePriceDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomRateDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomRateDto {

  private @Nullable String cellCode;

  private String end;

  private @Nullable String promotionCode;

  private String ratePlanCode;

  @Valid
  private @Nullable List<@Valid RatePriceDto> ratePrices;

  private String roomType;

  @Valid
  private @Nullable List<String> specialRequests;

  private String start;

  public RoomRateDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RoomRateDto(String end, String ratePlanCode, String roomType, String start) {
    this.end = end;
    this.ratePlanCode = ratePlanCode;
    this.roomType = roomType;
    this.start = start;
  }

  public RoomRateDto cellCode(String cellCode) {
    this.cellCode = cellCode;
    return this;
  }

  /**
   * Get cellCode
   * @return cellCode
   */
  
  @Schema(name = "cellCode", example = "ABC", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cellCode")
  public String getCellCode() {
    return cellCode;
  }

  public void setCellCode(String cellCode) {
    this.cellCode = cellCode;
  }

  public RoomRateDto end(String end) {
    this.end = end;
    return this;
  }

  /**
   * Get end
   * @return end
   */
  @NotNull 
  @Schema(name = "end", example = "2015-10-21", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("end")
  public String getEnd() {
    return end;
  }

  public void setEnd(String end) {
    this.end = end;
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

  public RoomRateDto ratePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
    return this;
  }

  /**
   * Get ratePlanCode
   * @return ratePlanCode
   */
  @NotNull 
  @Schema(name = "ratePlanCode", example = "DAILY", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ratePlanCode")
  public String getRatePlanCode() {
    return ratePlanCode;
  }

  public void setRatePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
  }

  public RoomRateDto ratePrices(List<@Valid RatePriceDto> ratePrices) {
    this.ratePrices = ratePrices;
    return this;
  }

  public RoomRateDto addRatePricesItem(RatePriceDto ratePricesItem) {
    if (this.ratePrices == null) {
      this.ratePrices = new ArrayList<>();
    }
    this.ratePrices.add(ratePricesItem);
    return this;
  }

  /**
   * Get ratePrices
   * @return ratePrices
   */
  @Valid 
  @Schema(name = "ratePrices", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePrices")
  public List<@Valid RatePriceDto> getRatePrices() {
    return ratePrices;
  }

  public void setRatePrices(List<@Valid RatePriceDto> ratePrices) {
    this.ratePrices = ratePrices;
  }

  public RoomRateDto roomType(String roomType) {
    this.roomType = roomType;
    return this;
  }

  /**
   * Get roomType
   * @return roomType
   */
  @NotNull 
  @Schema(name = "roomType", example = "SDB", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("roomType")
  public String getRoomType() {
    return roomType;
  }

  public void setRoomType(String roomType) {
    this.roomType = roomType;
  }

  public RoomRateDto specialRequests(List<String> specialRequests) {
    this.specialRequests = specialRequests;
    return this;
  }

  public RoomRateDto addSpecialRequestsItem(String specialRequestsItem) {
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
  
  @Schema(name = "specialRequests", example = "SNGL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("specialRequests")
  public List<String> getSpecialRequests() {
    return specialRequests;
  }

  public void setSpecialRequests(List<String> specialRequests) {
    this.specialRequests = specialRequests;
  }

  public RoomRateDto start(String start) {
    this.start = start;
    return this;
  }

  /**
   * Get start
   * @return start
   */
  @NotNull 
  @Schema(name = "start", example = "2015-10-20", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("start")
  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
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
        Objects.equals(this.end, roomRateDto.end) &&
        Objects.equals(this.promotionCode, roomRateDto.promotionCode) &&
        Objects.equals(this.ratePlanCode, roomRateDto.ratePlanCode) &&
        Objects.equals(this.ratePrices, roomRateDto.ratePrices) &&
        Objects.equals(this.roomType, roomRateDto.roomType) &&
        Objects.equals(this.specialRequests, roomRateDto.specialRequests) &&
        Objects.equals(this.start, roomRateDto.start);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cellCode, end, promotionCode, ratePlanCode, ratePrices, roomType, specialRequests, start);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomRateDto {\n");
    sb.append("    cellCode: ").append(toIndentedString(cellCode)).append("\n");
    sb.append("    end: ").append(toIndentedString(end)).append("\n");
    sb.append("    promotionCode: ").append(toIndentedString(promotionCode)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    ratePrices: ").append(toIndentedString(ratePrices)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
    sb.append("    specialRequests: ").append(toIndentedString(specialRequests)).append("\n");
    sb.append("    start: ").append(toIndentedString(start)).append("\n");
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


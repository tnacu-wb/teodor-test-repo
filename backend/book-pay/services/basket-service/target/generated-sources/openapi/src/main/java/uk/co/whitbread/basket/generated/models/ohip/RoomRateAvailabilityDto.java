package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.RoomTypeDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomRateAvailabilityDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomRateAvailabilityDto {

  private @Nullable String promotionCode;

  private String ratePlanCode;

  @Valid
  private List<@Valid RoomTypeDto> roomTypes = new ArrayList<>();

  public RoomRateAvailabilityDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RoomRateAvailabilityDto(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
  }

  public RoomRateAvailabilityDto promotionCode(String promotionCode) {
    this.promotionCode = promotionCode;
    return this;
  }

  /**
   * Get promotionCode
   * @return promotionCode
   */
  
  @Schema(name = "promotionCode", example = "PROMO", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promotionCode")
  public String getPromotionCode() {
    return promotionCode;
  }

  public void setPromotionCode(String promotionCode) {
    this.promotionCode = promotionCode;
  }

  public RoomRateAvailabilityDto ratePlanCode(String ratePlanCode) {
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

  public RoomRateAvailabilityDto roomTypes(List<@Valid RoomTypeDto> roomTypes) {
    this.roomTypes = roomTypes;
    return this;
  }

  public RoomRateAvailabilityDto addRoomTypesItem(RoomTypeDto roomTypesItem) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomRateAvailabilityDto roomRateAvailabilityDto = (RoomRateAvailabilityDto) o;
    return Objects.equals(this.promotionCode, roomRateAvailabilityDto.promotionCode) &&
        Objects.equals(this.ratePlanCode, roomRateAvailabilityDto.ratePlanCode) &&
        Objects.equals(this.roomTypes, roomRateAvailabilityDto.roomTypes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(promotionCode, ratePlanCode, roomTypes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomRateAvailabilityDto {\n");
    sb.append("    promotionCode: ").append(toIndentedString(promotionCode)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    roomTypes: ").append(toIndentedString(roomTypes)).append("\n");
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


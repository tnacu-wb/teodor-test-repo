package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.MealsIncludedDto;
import uk.co.whitbread.basket.generated.models.ohip.RoomPriceBreakdownDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomDto {

  private @Nullable Boolean cotAvailable;

  private @Nullable MealsIncludedDto mealsIncluded;

  private @Nullable Integer numberOfRoomsAvailable;

  private @Nullable String pmsRoomType;

  private @Nullable String roomClass;

  private @Nullable RoomPriceBreakdownDto roomPriceBreakdown;

  private @Nullable Boolean silentSubstitution;

  @Valid
  private List<String> specialRequests = new ArrayList<>();

  public RoomDto cotAvailable(Boolean cotAvailable) {
    this.cotAvailable = cotAvailable;
    return this;
  }

  /**
   * Get cotAvailable
   * @return cotAvailable
   */
  
  @Schema(name = "cotAvailable", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cotAvailable")
  public Boolean getCotAvailable() {
    return cotAvailable;
  }

  public void setCotAvailable(Boolean cotAvailable) {
    this.cotAvailable = cotAvailable;
  }

  public RoomDto mealsIncluded(MealsIncludedDto mealsIncluded) {
    this.mealsIncluded = mealsIncluded;
    return this;
  }

  /**
   * Get mealsIncluded
   * @return mealsIncluded
   */
  @Valid 
  @Schema(name = "mealsIncluded", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mealsIncluded")
  public MealsIncludedDto getMealsIncluded() {
    return mealsIncluded;
  }

  public void setMealsIncluded(MealsIncludedDto mealsIncluded) {
    this.mealsIncluded = mealsIncluded;
  }

  public RoomDto numberOfRoomsAvailable(Integer numberOfRoomsAvailable) {
    this.numberOfRoomsAvailable = numberOfRoomsAvailable;
    return this;
  }

  /**
   * Get numberOfRoomsAvailable
   * @return numberOfRoomsAvailable
   */
  
  @Schema(name = "numberOfRoomsAvailable", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("numberOfRoomsAvailable")
  public Integer getNumberOfRoomsAvailable() {
    return numberOfRoomsAvailable;
  }

  public void setNumberOfRoomsAvailable(Integer numberOfRoomsAvailable) {
    this.numberOfRoomsAvailable = numberOfRoomsAvailable;
  }

  public RoomDto pmsRoomType(String pmsRoomType) {
    this.pmsRoomType = pmsRoomType;
    return this;
  }

  /**
   * Get pmsRoomType
   * @return pmsRoomType
   */
  
  @Schema(name = "pmsRoomType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pmsRoomType")
  public String getPmsRoomType() {
    return pmsRoomType;
  }

  public void setPmsRoomType(String pmsRoomType) {
    this.pmsRoomType = pmsRoomType;
  }

  public RoomDto roomClass(String roomClass) {
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

  public RoomDto roomPriceBreakdown(RoomPriceBreakdownDto roomPriceBreakdown) {
    this.roomPriceBreakdown = roomPriceBreakdown;
    return this;
  }

  /**
   * Get roomPriceBreakdown
   * @return roomPriceBreakdown
   */
  @Valid 
  @Schema(name = "roomPriceBreakdown", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomPriceBreakdown")
  public RoomPriceBreakdownDto getRoomPriceBreakdown() {
    return roomPriceBreakdown;
  }

  public void setRoomPriceBreakdown(RoomPriceBreakdownDto roomPriceBreakdown) {
    this.roomPriceBreakdown = roomPriceBreakdown;
  }

  public RoomDto silentSubstitution(Boolean silentSubstitution) {
    this.silentSubstitution = silentSubstitution;
    return this;
  }

  /**
   * Get silentSubstitution
   * @return silentSubstitution
   */
  
  @Schema(name = "silentSubstitution", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("silentSubstitution")
  public Boolean getSilentSubstitution() {
    return silentSubstitution;
  }

  public void setSilentSubstitution(Boolean silentSubstitution) {
    this.silentSubstitution = silentSubstitution;
  }

  public RoomDto specialRequests(List<String> specialRequests) {
    this.specialRequests = specialRequests;
    return this;
  }

  public RoomDto addSpecialRequestsItem(String specialRequestsItem) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomDto roomDto = (RoomDto) o;
    return Objects.equals(this.cotAvailable, roomDto.cotAvailable) &&
        Objects.equals(this.mealsIncluded, roomDto.mealsIncluded) &&
        Objects.equals(this.numberOfRoomsAvailable, roomDto.numberOfRoomsAvailable) &&
        Objects.equals(this.pmsRoomType, roomDto.pmsRoomType) &&
        Objects.equals(this.roomClass, roomDto.roomClass) &&
        Objects.equals(this.roomPriceBreakdown, roomDto.roomPriceBreakdown) &&
        Objects.equals(this.silentSubstitution, roomDto.silentSubstitution) &&
        Objects.equals(this.specialRequests, roomDto.specialRequests);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cotAvailable, mealsIncluded, numberOfRoomsAvailable, pmsRoomType, roomClass, roomPriceBreakdown, silentSubstitution, specialRequests);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomDto {\n");
    sb.append("    cotAvailable: ").append(toIndentedString(cotAvailable)).append("\n");
    sb.append("    mealsIncluded: ").append(toIndentedString(mealsIncluded)).append("\n");
    sb.append("    numberOfRoomsAvailable: ").append(toIndentedString(numberOfRoomsAvailable)).append("\n");
    sb.append("    pmsRoomType: ").append(toIndentedString(pmsRoomType)).append("\n");
    sb.append("    roomClass: ").append(toIndentedString(roomClass)).append("\n");
    sb.append("    roomPriceBreakdown: ").append(toIndentedString(roomPriceBreakdown)).append("\n");
    sb.append("    silentSubstitution: ").append(toIndentedString(silentSubstitution)).append("\n");
    sb.append("    specialRequests: ").append(toIndentedString(specialRequests)).append("\n");
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


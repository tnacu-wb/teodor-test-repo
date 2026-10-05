package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.RoomUpgradesDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomUpgradeOptionsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomUpgradeOptionsDto {

  private @Nullable String priceText;

  private @Nullable String primaryButtonText;

  @Valid
  private List<@Valid RoomUpgradesDto> roomUpgrades = new ArrayList<>();

  private @Nullable String secondaryButtonText;

  public RoomUpgradeOptionsDto priceText(String priceText) {
    this.priceText = priceText;
    return this;
  }

  /**
   * Get priceText
   * @return priceText
   */
  
  @Schema(name = "priceText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("priceText")
  public String getPriceText() {
    return priceText;
  }

  public void setPriceText(String priceText) {
    this.priceText = priceText;
  }

  public RoomUpgradeOptionsDto primaryButtonText(String primaryButtonText) {
    this.primaryButtonText = primaryButtonText;
    return this;
  }

  /**
   * Get primaryButtonText
   * @return primaryButtonText
   */
  
  @Schema(name = "primaryButtonText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("primaryButtonText")
  public String getPrimaryButtonText() {
    return primaryButtonText;
  }

  public void setPrimaryButtonText(String primaryButtonText) {
    this.primaryButtonText = primaryButtonText;
  }

  public RoomUpgradeOptionsDto roomUpgrades(List<@Valid RoomUpgradesDto> roomUpgrades) {
    this.roomUpgrades = roomUpgrades;
    return this;
  }

  public RoomUpgradeOptionsDto addRoomUpgradesItem(RoomUpgradesDto roomUpgradesItem) {
    if (this.roomUpgrades == null) {
      this.roomUpgrades = new ArrayList<>();
    }
    this.roomUpgrades.add(roomUpgradesItem);
    return this;
  }

  /**
   * Get roomUpgrades
   * @return roomUpgrades
   */
  @Valid 
  @Schema(name = "roomUpgrades", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomUpgrades")
  public List<@Valid RoomUpgradesDto> getRoomUpgrades() {
    return roomUpgrades;
  }

  public void setRoomUpgrades(List<@Valid RoomUpgradesDto> roomUpgrades) {
    this.roomUpgrades = roomUpgrades;
  }

  public RoomUpgradeOptionsDto secondaryButtonText(String secondaryButtonText) {
    this.secondaryButtonText = secondaryButtonText;
    return this;
  }

  /**
   * Get secondaryButtonText
   * @return secondaryButtonText
   */
  
  @Schema(name = "secondaryButtonText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("secondaryButtonText")
  public String getSecondaryButtonText() {
    return secondaryButtonText;
  }

  public void setSecondaryButtonText(String secondaryButtonText) {
    this.secondaryButtonText = secondaryButtonText;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomUpgradeOptionsDto roomUpgradeOptionsDto = (RoomUpgradeOptionsDto) o;
    return Objects.equals(this.priceText, roomUpgradeOptionsDto.priceText) &&
        Objects.equals(this.primaryButtonText, roomUpgradeOptionsDto.primaryButtonText) &&
        Objects.equals(this.roomUpgrades, roomUpgradeOptionsDto.roomUpgrades) &&
        Objects.equals(this.secondaryButtonText, roomUpgradeOptionsDto.secondaryButtonText);
  }

  @Override
  public int hashCode() {
    return Objects.hash(priceText, primaryButtonText, roomUpgrades, secondaryButtonText);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomUpgradeOptionsDto {\n");
    sb.append("    priceText: ").append(toIndentedString(priceText)).append("\n");
    sb.append("    primaryButtonText: ").append(toIndentedString(primaryButtonText)).append("\n");
    sb.append("    roomUpgrades: ").append(toIndentedString(roomUpgrades)).append("\n");
    sb.append("    secondaryButtonText: ").append(toIndentedString(secondaryButtonText)).append("\n");
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


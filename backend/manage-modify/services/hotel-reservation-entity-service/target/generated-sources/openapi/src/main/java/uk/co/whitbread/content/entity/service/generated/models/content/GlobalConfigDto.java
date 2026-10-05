package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.PriceFinderConfigDto;
import uk.co.whitbread.content.entity.service.generated.models.content.PromotionsConfigResponseDto;
import uk.co.whitbread.content.entity.service.generated.models.content.RoomClassOrderDto;
import uk.co.whitbread.content.entity.service.generated.models.content.RoomUpgradeOptionsDto;
import uk.co.whitbread.content.entity.service.generated.models.content.SearchRulesDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * GlobalConfigDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class GlobalConfigDto {

  @Valid
  private List<String> hotelsWithCityTax = new ArrayList<>();

  private @Nullable SearchRulesDto maxRoomsLim;

  private @Nullable PriceFinderConfigDto priceFinderConfig;

  private @Nullable PromotionsConfigResponseDto promotionsConfig;

  @Valid
  private List<@Valid RoomClassOrderDto> roomClassConfig = new ArrayList<>();

  private @Nullable RoomUpgradeOptionsDto roomUpgradeOptions;

  public GlobalConfigDto hotelsWithCityTax(List<String> hotelsWithCityTax) {
    this.hotelsWithCityTax = hotelsWithCityTax;
    return this;
  }

  public GlobalConfigDto addHotelsWithCityTaxItem(String hotelsWithCityTaxItem) {
    if (this.hotelsWithCityTax == null) {
      this.hotelsWithCityTax = new ArrayList<>();
    }
    this.hotelsWithCityTax.add(hotelsWithCityTaxItem);
    return this;
  }

  /**
   * Get hotelsWithCityTax
   * @return hotelsWithCityTax
   */
  
  @Schema(name = "hotelsWithCityTax", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelsWithCityTax")
  public List<String> getHotelsWithCityTax() {
    return hotelsWithCityTax;
  }

  public void setHotelsWithCityTax(List<String> hotelsWithCityTax) {
    this.hotelsWithCityTax = hotelsWithCityTax;
  }

  public GlobalConfigDto maxRoomsLim(SearchRulesDto maxRoomsLim) {
    this.maxRoomsLim = maxRoomsLim;
    return this;
  }

  /**
   * Get maxRoomsLim
   * @return maxRoomsLim
   */
  @Valid 
  @Schema(name = "maxRoomsLim", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxRoomsLim")
  public SearchRulesDto getMaxRoomsLim() {
    return maxRoomsLim;
  }

  public void setMaxRoomsLim(SearchRulesDto maxRoomsLim) {
    this.maxRoomsLim = maxRoomsLim;
  }

  public GlobalConfigDto priceFinderConfig(PriceFinderConfigDto priceFinderConfig) {
    this.priceFinderConfig = priceFinderConfig;
    return this;
  }

  /**
   * Get priceFinderConfig
   * @return priceFinderConfig
   */
  @Valid 
  @Schema(name = "priceFinderConfig", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("priceFinderConfig")
  public PriceFinderConfigDto getPriceFinderConfig() {
    return priceFinderConfig;
  }

  public void setPriceFinderConfig(PriceFinderConfigDto priceFinderConfig) {
    this.priceFinderConfig = priceFinderConfig;
  }

  public GlobalConfigDto promotionsConfig(PromotionsConfigResponseDto promotionsConfig) {
    this.promotionsConfig = promotionsConfig;
    return this;
  }

  /**
   * Get promotionsConfig
   * @return promotionsConfig
   */
  @Valid 
  @Schema(name = "promotionsConfig", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promotionsConfig")
  public PromotionsConfigResponseDto getPromotionsConfig() {
    return promotionsConfig;
  }

  public void setPromotionsConfig(PromotionsConfigResponseDto promotionsConfig) {
    this.promotionsConfig = promotionsConfig;
  }

  public GlobalConfigDto roomClassConfig(List<@Valid RoomClassOrderDto> roomClassConfig) {
    this.roomClassConfig = roomClassConfig;
    return this;
  }

  public GlobalConfigDto addRoomClassConfigItem(RoomClassOrderDto roomClassConfigItem) {
    if (this.roomClassConfig == null) {
      this.roomClassConfig = new ArrayList<>();
    }
    this.roomClassConfig.add(roomClassConfigItem);
    return this;
  }

  /**
   * Get roomClassConfig
   * @return roomClassConfig
   */
  @Valid 
  @Schema(name = "roomClassConfig", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomClassConfig")
  public List<@Valid RoomClassOrderDto> getRoomClassConfig() {
    return roomClassConfig;
  }

  public void setRoomClassConfig(List<@Valid RoomClassOrderDto> roomClassConfig) {
    this.roomClassConfig = roomClassConfig;
  }

  public GlobalConfigDto roomUpgradeOptions(RoomUpgradeOptionsDto roomUpgradeOptions) {
    this.roomUpgradeOptions = roomUpgradeOptions;
    return this;
  }

  /**
   * Get roomUpgradeOptions
   * @return roomUpgradeOptions
   */
  @Valid 
  @Schema(name = "roomUpgradeOptions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomUpgradeOptions")
  public RoomUpgradeOptionsDto getRoomUpgradeOptions() {
    return roomUpgradeOptions;
  }

  public void setRoomUpgradeOptions(RoomUpgradeOptionsDto roomUpgradeOptions) {
    this.roomUpgradeOptions = roomUpgradeOptions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GlobalConfigDto globalConfigDto = (GlobalConfigDto) o;
    return Objects.equals(this.hotelsWithCityTax, globalConfigDto.hotelsWithCityTax) &&
        Objects.equals(this.maxRoomsLim, globalConfigDto.maxRoomsLim) &&
        Objects.equals(this.priceFinderConfig, globalConfigDto.priceFinderConfig) &&
        Objects.equals(this.promotionsConfig, globalConfigDto.promotionsConfig) &&
        Objects.equals(this.roomClassConfig, globalConfigDto.roomClassConfig) &&
        Objects.equals(this.roomUpgradeOptions, globalConfigDto.roomUpgradeOptions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelsWithCityTax, maxRoomsLim, priceFinderConfig, promotionsConfig, roomClassConfig, roomUpgradeOptions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GlobalConfigDto {\n");
    sb.append("    hotelsWithCityTax: ").append(toIndentedString(hotelsWithCityTax)).append("\n");
    sb.append("    maxRoomsLim: ").append(toIndentedString(maxRoomsLim)).append("\n");
    sb.append("    priceFinderConfig: ").append(toIndentedString(priceFinderConfig)).append("\n");
    sb.append("    promotionsConfig: ").append(toIndentedString(promotionsConfig)).append("\n");
    sb.append("    roomClassConfig: ").append(toIndentedString(roomClassConfig)).append("\n");
    sb.append("    roomUpgradeOptions: ").append(toIndentedString(roomUpgradeOptions)).append("\n");
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


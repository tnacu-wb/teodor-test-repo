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
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * RoomClassOrderDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomClassOrderDto {

  @Valid
  private List<String> availableUpgrades = new ArrayList<>();

  private @Nullable String code;

  private @Nullable Integer order;

  public RoomClassOrderDto availableUpgrades(List<String> availableUpgrades) {
    this.availableUpgrades = availableUpgrades;
    return this;
  }

  public RoomClassOrderDto addAvailableUpgradesItem(String availableUpgradesItem) {
    if (this.availableUpgrades == null) {
      this.availableUpgrades = new ArrayList<>();
    }
    this.availableUpgrades.add(availableUpgradesItem);
    return this;
  }

  /**
   * Get availableUpgrades
   * @return availableUpgrades
   */
  
  @Schema(name = "availableUpgrades", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("availableUpgrades")
  public List<String> getAvailableUpgrades() {
    return availableUpgrades;
  }

  public void setAvailableUpgrades(List<String> availableUpgrades) {
    this.availableUpgrades = availableUpgrades;
  }

  public RoomClassOrderDto code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
   */
  
  @Schema(name = "code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public RoomClassOrderDto order(Integer order) {
    this.order = order;
    return this;
  }

  /**
   * Get order
   * @return order
   */
  
  @Schema(name = "order", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("order")
  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomClassOrderDto roomClassOrderDto = (RoomClassOrderDto) o;
    return Objects.equals(this.availableUpgrades, roomClassOrderDto.availableUpgrades) &&
        Objects.equals(this.code, roomClassOrderDto.code) &&
        Objects.equals(this.order, roomClassOrderDto.order);
  }

  @Override
  public int hashCode() {
    return Objects.hash(availableUpgrades, code, order);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomClassOrderDto {\n");
    sb.append("    availableUpgrades: ").append(toIndentedString(availableUpgrades)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    order: ").append(toIndentedString(order)).append("\n");
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


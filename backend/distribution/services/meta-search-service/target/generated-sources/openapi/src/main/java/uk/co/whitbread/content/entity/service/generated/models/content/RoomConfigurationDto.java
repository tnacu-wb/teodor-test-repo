package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.TabGroupDto;
import uk.co.whitbread.content.entity.service.generated.models.content.TabItemDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomConfigurationDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:27.565654+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomConfigurationDto {

  @Valid
  private List<@Valid TabGroupDto> tabGroups = new ArrayList<>();

  @Valid
  private List<@Valid TabItemDto> tabItems = new ArrayList<>();

  public RoomConfigurationDto tabGroups(List<@Valid TabGroupDto> tabGroups) {
    this.tabGroups = tabGroups;
    return this;
  }

  public RoomConfigurationDto addTabGroupsItem(TabGroupDto tabGroupsItem) {
    if (this.tabGroups == null) {
      this.tabGroups = new ArrayList<>();
    }
    this.tabGroups.add(tabGroupsItem);
    return this;
  }

  /**
   * Get tabGroups
   * @return tabGroups
   */
  @Valid 
  @Schema(name = "tabGroups", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tabGroups")
  public List<@Valid TabGroupDto> getTabGroups() {
    return tabGroups;
  }

  public void setTabGroups(List<@Valid TabGroupDto> tabGroups) {
    this.tabGroups = tabGroups;
  }

  public RoomConfigurationDto tabItems(List<@Valid TabItemDto> tabItems) {
    this.tabItems = tabItems;
    return this;
  }

  public RoomConfigurationDto addTabItemsItem(TabItemDto tabItemsItem) {
    if (this.tabItems == null) {
      this.tabItems = new ArrayList<>();
    }
    this.tabItems.add(tabItemsItem);
    return this;
  }

  /**
   * Get tabItems
   * @return tabItems
   */
  @Valid 
  @Schema(name = "tabItems", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tabItems")
  public List<@Valid TabItemDto> getTabItems() {
    return tabItems;
  }

  public void setTabItems(List<@Valid TabItemDto> tabItems) {
    this.tabItems = tabItems;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomConfigurationDto roomConfigurationDto = (RoomConfigurationDto) o;
    return Objects.equals(this.tabGroups, roomConfigurationDto.tabGroups) &&
        Objects.equals(this.tabItems, roomConfigurationDto.tabItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tabGroups, tabItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomConfigurationDto {\n");
    sb.append("    tabGroups: ").append(toIndentedString(tabGroups)).append("\n");
    sb.append("    tabItems: ").append(toIndentedString(tabItems)).append("\n");
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


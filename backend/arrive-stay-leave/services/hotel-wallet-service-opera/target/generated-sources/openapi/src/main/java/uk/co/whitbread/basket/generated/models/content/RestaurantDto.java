package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.content.MenuDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * RestaurantDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:24.587145+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RestaurantDto {

  private @Nullable String description;

  private @Nullable String logoSrc;

  @Valid
  private List<@Valid MenuDto> menus = new ArrayList<>();

  private @Nullable String name;

  public RestaurantDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public RestaurantDto logoSrc(String logoSrc) {
    this.logoSrc = logoSrc;
    return this;
  }

  /**
   * Get logoSrc
   * @return logoSrc
   */
  
  @Schema(name = "logoSrc", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("logoSrc")
  public String getLogoSrc() {
    return logoSrc;
  }

  public void setLogoSrc(String logoSrc) {
    this.logoSrc = logoSrc;
  }

  public RestaurantDto menus(List<@Valid MenuDto> menus) {
    this.menus = menus;
    return this;
  }

  public RestaurantDto addMenusItem(MenuDto menusItem) {
    if (this.menus == null) {
      this.menus = new ArrayList<>();
    }
    this.menus.add(menusItem);
    return this;
  }

  /**
   * Get menus
   * @return menus
   */
  @Valid 
  @Schema(name = "menus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("menus")
  public List<@Valid MenuDto> getMenus() {
    return menus;
  }

  public void setMenus(List<@Valid MenuDto> menus) {
    this.menus = menus;
  }

  public RestaurantDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  
  @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RestaurantDto restaurantDto = (RestaurantDto) o;
    return Objects.equals(this.description, restaurantDto.description) &&
        Objects.equals(this.logoSrc, restaurantDto.logoSrc) &&
        Objects.equals(this.menus, restaurantDto.menus) &&
        Objects.equals(this.name, restaurantDto.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, logoSrc, menus, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RestaurantDto {\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    logoSrc: ").append(toIndentedString(logoSrc)).append("\n");
    sb.append("    menus: ").append(toIndentedString(menus)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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


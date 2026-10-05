package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.AuthenticationDto;
import uk.co.whitbread.content.entity.service.generated.models.content.CountryDto;
import uk.co.whitbread.content.entity.service.generated.models.content.GlobalDto;
import uk.co.whitbread.content.entity.service.generated.models.content.HeaderDto;
import uk.co.whitbread.content.entity.service.generated.models.content.MenuDto;
import uk.co.whitbread.content.entity.service.generated.models.content.SubNavDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ContentDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:27.565654+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ContentDto {

  private @Nullable AuthenticationDto authentication;

  @Valid
  private List<@Valid CountryDto> countries = new ArrayList<>();

  private @Nullable GlobalDto global;

  private @Nullable HeaderDto header;

  private @Nullable MenuDto menu;

  @Valid
  private List<@Valid SubNavDto> subNav = new ArrayList<>();

  public ContentDto authentication(AuthenticationDto authentication) {
    this.authentication = authentication;
    return this;
  }

  /**
   * Get authentication
   * @return authentication
   */
  @Valid 
  @Schema(name = "authentication", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("authentication")
  public AuthenticationDto getAuthentication() {
    return authentication;
  }

  public void setAuthentication(AuthenticationDto authentication) {
    this.authentication = authentication;
  }

  public ContentDto countries(List<@Valid CountryDto> countries) {
    this.countries = countries;
    return this;
  }

  public ContentDto addCountriesItem(CountryDto countriesItem) {
    if (this.countries == null) {
      this.countries = new ArrayList<>();
    }
    this.countries.add(countriesItem);
    return this;
  }

  /**
   * Get countries
   * @return countries
   */
  @Valid 
  @Schema(name = "countries", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("countries")
  public List<@Valid CountryDto> getCountries() {
    return countries;
  }

  public void setCountries(List<@Valid CountryDto> countries) {
    this.countries = countries;
  }

  public ContentDto global(GlobalDto global) {
    this.global = global;
    return this;
  }

  /**
   * Get global
   * @return global
   */
  @Valid 
  @Schema(name = "global", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("global")
  public GlobalDto getGlobal() {
    return global;
  }

  public void setGlobal(GlobalDto global) {
    this.global = global;
  }

  public ContentDto header(HeaderDto header) {
    this.header = header;
    return this;
  }

  /**
   * Get header
   * @return header
   */
  @Valid 
  @Schema(name = "header", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("header")
  public HeaderDto getHeader() {
    return header;
  }

  public void setHeader(HeaderDto header) {
    this.header = header;
  }

  public ContentDto menu(MenuDto menu) {
    this.menu = menu;
    return this;
  }

  /**
   * Get menu
   * @return menu
   */
  @Valid 
  @Schema(name = "menu", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("menu")
  public MenuDto getMenu() {
    return menu;
  }

  public void setMenu(MenuDto menu) {
    this.menu = menu;
  }

  public ContentDto subNav(List<@Valid SubNavDto> subNav) {
    this.subNav = subNav;
    return this;
  }

  public ContentDto addSubNavItem(SubNavDto subNavItem) {
    if (this.subNav == null) {
      this.subNav = new ArrayList<>();
    }
    this.subNav.add(subNavItem);
    return this;
  }

  /**
   * Get subNav
   * @return subNav
   */
  @Valid 
  @Schema(name = "subNav", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("subNav")
  public List<@Valid SubNavDto> getSubNav() {
    return subNav;
  }

  public void setSubNav(List<@Valid SubNavDto> subNav) {
    this.subNav = subNav;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContentDto contentDto = (ContentDto) o;
    return Objects.equals(this.authentication, contentDto.authentication) &&
        Objects.equals(this.countries, contentDto.countries) &&
        Objects.equals(this.global, contentDto.global) &&
        Objects.equals(this.header, contentDto.header) &&
        Objects.equals(this.menu, contentDto.menu) &&
        Objects.equals(this.subNav, contentDto.subNav);
  }

  @Override
  public int hashCode() {
    return Objects.hash(authentication, countries, global, header, menu, subNav);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContentDto {\n");
    sb.append("    authentication: ").append(toIndentedString(authentication)).append("\n");
    sb.append("    countries: ").append(toIndentedString(countries)).append("\n");
    sb.append("    global: ").append(toIndentedString(global)).append("\n");
    sb.append("    header: ").append(toIndentedString(header)).append("\n");
    sb.append("    menu: ").append(toIndentedString(menu)).append("\n");
    sb.append("    subNav: ").append(toIndentedString(subNav)).append("\n");
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


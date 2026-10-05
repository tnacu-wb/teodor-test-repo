package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.HelpDto;
import uk.co.whitbread.content.entity.service.generated.models.content.ManageAccountDto;
import uk.co.whitbread.content.entity.service.generated.models.content.MenuDto;
import uk.co.whitbread.content.entity.service.generated.models.content.SidebarDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * LayoutDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class LayoutDto {

  private @Nullable HelpDto help;

  private @Nullable ManageAccountDto manageAccount;

  private @Nullable MenuDto menu;

  private @Nullable SidebarDto sidebar;

  public LayoutDto help(HelpDto help) {
    this.help = help;
    return this;
  }

  /**
   * Get help
   * @return help
   */
  @Valid 
  @Schema(name = "help", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("help")
  public HelpDto getHelp() {
    return help;
  }

  public void setHelp(HelpDto help) {
    this.help = help;
  }

  public LayoutDto manageAccount(ManageAccountDto manageAccount) {
    this.manageAccount = manageAccount;
    return this;
  }

  /**
   * Get manageAccount
   * @return manageAccount
   */
  @Valid 
  @Schema(name = "manageAccount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("manageAccount")
  public ManageAccountDto getManageAccount() {
    return manageAccount;
  }

  public void setManageAccount(ManageAccountDto manageAccount) {
    this.manageAccount = manageAccount;
  }

  public LayoutDto menu(MenuDto menu) {
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

  public LayoutDto sidebar(SidebarDto sidebar) {
    this.sidebar = sidebar;
    return this;
  }

  /**
   * Get sidebar
   * @return sidebar
   */
  @Valid 
  @Schema(name = "sidebar", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sidebar")
  public SidebarDto getSidebar() {
    return sidebar;
  }

  public void setSidebar(SidebarDto sidebar) {
    this.sidebar = sidebar;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LayoutDto layoutDto = (LayoutDto) o;
    return Objects.equals(this.help, layoutDto.help) &&
        Objects.equals(this.manageAccount, layoutDto.manageAccount) &&
        Objects.equals(this.menu, layoutDto.menu) &&
        Objects.equals(this.sidebar, layoutDto.sidebar);
  }

  @Override
  public int hashCode() {
    return Objects.hash(help, manageAccount, menu, sidebar);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LayoutDto {\n");
    sb.append("    help: ").append(toIndentedString(help)).append("\n");
    sb.append("    manageAccount: ").append(toIndentedString(manageAccount)).append("\n");
    sb.append("    menu: ").append(toIndentedString(menu)).append("\n");
    sb.append("    sidebar: ").append(toIndentedString(sidebar)).append("\n");
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


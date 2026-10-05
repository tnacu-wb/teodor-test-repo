package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.CookieGroupDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ManageViewDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:27.565654+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ManageViewDto {

  private @Nullable String alwaysActiveText;

  @Valid
  private List<@Valid CookieGroupDto> cookieGroup = new ArrayList<>();

  private @Nullable String description;

  private @Nullable String saveSettingsButtonText;

  private @Nullable String title;

  public ManageViewDto alwaysActiveText(String alwaysActiveText) {
    this.alwaysActiveText = alwaysActiveText;
    return this;
  }

  /**
   * Get alwaysActiveText
   * @return alwaysActiveText
   */
  
  @Schema(name = "alwaysActiveText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("alwaysActiveText")
  public String getAlwaysActiveText() {
    return alwaysActiveText;
  }

  public void setAlwaysActiveText(String alwaysActiveText) {
    this.alwaysActiveText = alwaysActiveText;
  }

  public ManageViewDto cookieGroup(List<@Valid CookieGroupDto> cookieGroup) {
    this.cookieGroup = cookieGroup;
    return this;
  }

  public ManageViewDto addCookieGroupItem(CookieGroupDto cookieGroupItem) {
    if (this.cookieGroup == null) {
      this.cookieGroup = new ArrayList<>();
    }
    this.cookieGroup.add(cookieGroupItem);
    return this;
  }

  /**
   * Get cookieGroup
   * @return cookieGroup
   */
  @Valid 
  @Schema(name = "cookieGroup", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cookieGroup")
  public List<@Valid CookieGroupDto> getCookieGroup() {
    return cookieGroup;
  }

  public void setCookieGroup(List<@Valid CookieGroupDto> cookieGroup) {
    this.cookieGroup = cookieGroup;
  }

  public ManageViewDto description(String description) {
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

  public ManageViewDto saveSettingsButtonText(String saveSettingsButtonText) {
    this.saveSettingsButtonText = saveSettingsButtonText;
    return this;
  }

  /**
   * Get saveSettingsButtonText
   * @return saveSettingsButtonText
   */
  
  @Schema(name = "saveSettingsButtonText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("saveSettingsButtonText")
  public String getSaveSettingsButtonText() {
    return saveSettingsButtonText;
  }

  public void setSaveSettingsButtonText(String saveSettingsButtonText) {
    this.saveSettingsButtonText = saveSettingsButtonText;
  }

  public ManageViewDto title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
   */
  
  @Schema(name = "title", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ManageViewDto manageViewDto = (ManageViewDto) o;
    return Objects.equals(this.alwaysActiveText, manageViewDto.alwaysActiveText) &&
        Objects.equals(this.cookieGroup, manageViewDto.cookieGroup) &&
        Objects.equals(this.description, manageViewDto.description) &&
        Objects.equals(this.saveSettingsButtonText, manageViewDto.saveSettingsButtonText) &&
        Objects.equals(this.title, manageViewDto.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(alwaysActiveText, cookieGroup, description, saveSettingsButtonText, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ManageViewDto {\n");
    sb.append("    alwaysActiveText: ").append(toIndentedString(alwaysActiveText)).append("\n");
    sb.append("    cookieGroup: ").append(toIndentedString(cookieGroup)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    saveSettingsButtonText: ").append(toIndentedString(saveSettingsButtonText)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
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


package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.NavOptionDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SubNavDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:27.565654+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class SubNavDto {

  @Valid
  private List<@Valid NavOptionDto> navOptions = new ArrayList<>();

  private @Nullable String title;

  public SubNavDto navOptions(List<@Valid NavOptionDto> navOptions) {
    this.navOptions = navOptions;
    return this;
  }

  public SubNavDto addNavOptionsItem(NavOptionDto navOptionsItem) {
    if (this.navOptions == null) {
      this.navOptions = new ArrayList<>();
    }
    this.navOptions.add(navOptionsItem);
    return this;
  }

  /**
   * Get navOptions
   * @return navOptions
   */
  @Valid 
  @Schema(name = "navOptions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("navOptions")
  public List<@Valid NavOptionDto> getNavOptions() {
    return navOptions;
  }

  public void setNavOptions(List<@Valid NavOptionDto> navOptions) {
    this.navOptions = navOptions;
  }

  public SubNavDto title(String title) {
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
    SubNavDto subNavDto = (SubNavDto) o;
    return Objects.equals(this.navOptions, subNavDto.navOptions) &&
        Objects.equals(this.title, subNavDto.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(navOptions, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SubNavDto {\n");
    sb.append("    navOptions: ").append(toIndentedString(navOptions)).append("\n");
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


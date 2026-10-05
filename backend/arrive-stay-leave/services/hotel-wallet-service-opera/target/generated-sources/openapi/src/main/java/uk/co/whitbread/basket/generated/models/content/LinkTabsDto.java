package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.content.IntroDto;
import uk.co.whitbread.basket.generated.models.content.LinkColumnsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * LinkTabsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:24.587145+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class LinkTabsDto {

  @Valid
  private List<@Valid LinkColumnsDto> columns = new ArrayList<>();

  private @Nullable IntroDto intro;

  private @Nullable String name;

  public LinkTabsDto columns(List<@Valid LinkColumnsDto> columns) {
    this.columns = columns;
    return this;
  }

  public LinkTabsDto addColumnsItem(LinkColumnsDto columnsItem) {
    if (this.columns == null) {
      this.columns = new ArrayList<>();
    }
    this.columns.add(columnsItem);
    return this;
  }

  /**
   * Get columns
   * @return columns
   */
  @Valid 
  @Schema(name = "columns", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("columns")
  public List<@Valid LinkColumnsDto> getColumns() {
    return columns;
  }

  public void setColumns(List<@Valid LinkColumnsDto> columns) {
    this.columns = columns;
  }

  public LinkTabsDto intro(IntroDto intro) {
    this.intro = intro;
    return this;
  }

  /**
   * Get intro
   * @return intro
   */
  @Valid 
  @Schema(name = "intro", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("intro")
  public IntroDto getIntro() {
    return intro;
  }

  public void setIntro(IntroDto intro) {
    this.intro = intro;
  }

  public LinkTabsDto name(String name) {
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
    LinkTabsDto linkTabsDto = (LinkTabsDto) o;
    return Objects.equals(this.columns, linkTabsDto.columns) &&
        Objects.equals(this.intro, linkTabsDto.intro) &&
        Objects.equals(this.name, linkTabsDto.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(columns, intro, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LinkTabsDto {\n");
    sb.append("    columns: ").append(toIndentedString(columns)).append("\n");
    sb.append("    intro: ").append(toIndentedString(intro)).append("\n");
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


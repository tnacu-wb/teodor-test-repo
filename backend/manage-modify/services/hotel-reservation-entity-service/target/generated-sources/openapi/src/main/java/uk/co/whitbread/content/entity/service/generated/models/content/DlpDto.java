package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.DlpItemDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * DlpDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class DlpDto {

  @Valid
  private List<@Valid DlpItemDto> dlpItems = new ArrayList<>();

  private @Nullable String title;

  public DlpDto dlpItems(List<@Valid DlpItemDto> dlpItems) {
    this.dlpItems = dlpItems;
    return this;
  }

  public DlpDto addDlpItemsItem(DlpItemDto dlpItemsItem) {
    if (this.dlpItems == null) {
      this.dlpItems = new ArrayList<>();
    }
    this.dlpItems.add(dlpItemsItem);
    return this;
  }

  /**
   * Get dlpItems
   * @return dlpItems
   */
  @Valid 
  @Schema(name = "dlpItems", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dlpItems")
  public List<@Valid DlpItemDto> getDlpItems() {
    return dlpItems;
  }

  public void setDlpItems(List<@Valid DlpItemDto> dlpItems) {
    this.dlpItems = dlpItems;
  }

  public DlpDto title(String title) {
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
    DlpDto dlpDto = (DlpDto) o;
    return Objects.equals(this.dlpItems, dlpDto.dlpItems) &&
        Objects.equals(this.title, dlpDto.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dlpItems, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DlpDto {\n");
    sb.append("    dlpItems: ").append(toIndentedString(dlpItems)).append("\n");
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


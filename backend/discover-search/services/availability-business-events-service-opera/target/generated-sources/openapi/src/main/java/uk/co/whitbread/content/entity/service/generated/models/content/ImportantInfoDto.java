package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.InfoItemDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ImportantInfoDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:46.057591+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ImportantInfoDto {

  @Valid
  private List<@Valid InfoItemDto> infoItems = new ArrayList<>();

  private @Nullable String title;

  public ImportantInfoDto infoItems(List<@Valid InfoItemDto> infoItems) {
    this.infoItems = infoItems;
    return this;
  }

  public ImportantInfoDto addInfoItemsItem(InfoItemDto infoItemsItem) {
    if (this.infoItems == null) {
      this.infoItems = new ArrayList<>();
    }
    this.infoItems.add(infoItemsItem);
    return this;
  }

  /**
   * Get infoItems
   * @return infoItems
   */
  @Valid 
  @Schema(name = "infoItems", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("infoItems")
  public List<@Valid InfoItemDto> getInfoItems() {
    return infoItems;
  }

  public void setInfoItems(List<@Valid InfoItemDto> infoItems) {
    this.infoItems = infoItems;
  }

  public ImportantInfoDto title(String title) {
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
    ImportantInfoDto importantInfoDto = (ImportantInfoDto) o;
    return Objects.equals(this.infoItems, importantInfoDto.infoItems) &&
        Objects.equals(this.title, importantInfoDto.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(infoItems, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ImportantInfoDto {\n");
    sb.append("    infoItems: ").append(toIndentedString(infoItems)).append("\n");
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


package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CardManagementDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CardManagementDto {

  private @Nullable String info;

  private @Nullable String title;

  public CardManagementDto info(String info) {
    this.info = info;
    return this;
  }

  /**
   * Get info
   * @return info
   */
  
  @Schema(name = "info", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("info")
  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public CardManagementDto title(String title) {
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
    CardManagementDto cardManagementDto = (CardManagementDto) o;
    return Objects.equals(this.info, cardManagementDto.info) &&
        Objects.equals(this.title, cardManagementDto.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(info, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CardManagementDto {\n");
    sb.append("    info: ").append(toIndentedString(info)).append("\n");
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


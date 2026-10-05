package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.ContentDto;
import uk.co.whitbread.content.entity.service.generated.models.content.LayoutDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * HeaderResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HeaderResponseDto {

  private @Nullable ContentDto content;

  private @Nullable LayoutDto layout;

  public HeaderResponseDto content(ContentDto content) {
    this.content = content;
    return this;
  }

  /**
   * Get content
   * @return content
   */
  @Valid 
  @Schema(name = "content", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("content")
  public ContentDto getContent() {
    return content;
  }

  public void setContent(ContentDto content) {
    this.content = content;
  }

  public HeaderResponseDto layout(LayoutDto layout) {
    this.layout = layout;
    return this;
  }

  /**
   * Get layout
   * @return layout
   */
  @Valid 
  @Schema(name = "layout", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("layout")
  public LayoutDto getLayout() {
    return layout;
  }

  public void setLayout(LayoutDto layout) {
    this.layout = layout;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HeaderResponseDto headerResponseDto = (HeaderResponseDto) o;
    return Objects.equals(this.content, headerResponseDto.content) &&
        Objects.equals(this.layout, headerResponseDto.layout);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content, layout);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HeaderResponseDto {\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    layout: ").append(toIndentedString(layout)).append("\n");
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


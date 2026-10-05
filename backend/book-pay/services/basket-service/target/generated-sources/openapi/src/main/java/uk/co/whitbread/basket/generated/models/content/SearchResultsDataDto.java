package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.content.ConfigDto;
import uk.co.whitbread.basket.generated.models.content.ContentDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SearchResultsDataDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:06.327224+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class SearchResultsDataDto {

  private @Nullable ConfigDto config;

  private @Nullable ContentDto content;

  public SearchResultsDataDto config(ConfigDto config) {
    this.config = config;
    return this;
  }

  /**
   * Get config
   * @return config
   */
  @Valid 
  @Schema(name = "config", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("config")
  public ConfigDto getConfig() {
    return config;
  }

  public void setConfig(ConfigDto config) {
    this.config = config;
  }

  public SearchResultsDataDto content(ContentDto content) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SearchResultsDataDto searchResultsDataDto = (SearchResultsDataDto) o;
    return Objects.equals(this.config, searchResultsDataDto.config) &&
        Objects.equals(this.content, searchResultsDataDto.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(config, content);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SearchResultsDataDto {\n");
    sb.append("    config: ").append(toIndentedString(config)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
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


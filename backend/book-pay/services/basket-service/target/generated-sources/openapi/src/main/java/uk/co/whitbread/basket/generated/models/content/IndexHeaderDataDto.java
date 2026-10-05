package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.content.AnnouncementDto;
import uk.co.whitbread.basket.generated.models.content.ConfigDto;
import uk.co.whitbread.basket.generated.models.content.ContentDto;
import uk.co.whitbread.basket.generated.models.content.DatePickerDto;
import uk.co.whitbread.basket.generated.models.content.FormDto;
import uk.co.whitbread.basket.generated.models.content.ResultsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * IndexHeaderDataDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:06.327224+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class IndexHeaderDataDto {

  private @Nullable AnnouncementDto announcement;

  private @Nullable ConfigDto config;

  private @Nullable ContentDto content;

  private @Nullable DatePickerDto datePicker;

  private @Nullable FormDto form;

  private @Nullable ResultsDto results;

  public IndexHeaderDataDto announcement(AnnouncementDto announcement) {
    this.announcement = announcement;
    return this;
  }

  /**
   * Get announcement
   * @return announcement
   */
  @Valid 
  @Schema(name = "announcement", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("announcement")
  public AnnouncementDto getAnnouncement() {
    return announcement;
  }

  public void setAnnouncement(AnnouncementDto announcement) {
    this.announcement = announcement;
  }

  public IndexHeaderDataDto config(ConfigDto config) {
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

  public IndexHeaderDataDto content(ContentDto content) {
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

  public IndexHeaderDataDto datePicker(DatePickerDto datePicker) {
    this.datePicker = datePicker;
    return this;
  }

  /**
   * Get datePicker
   * @return datePicker
   */
  @Valid 
  @Schema(name = "datePicker", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("datePicker")
  public DatePickerDto getDatePicker() {
    return datePicker;
  }

  public void setDatePicker(DatePickerDto datePicker) {
    this.datePicker = datePicker;
  }

  public IndexHeaderDataDto form(FormDto form) {
    this.form = form;
    return this;
  }

  /**
   * Get form
   * @return form
   */
  @Valid 
  @Schema(name = "form", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("form")
  public FormDto getForm() {
    return form;
  }

  public void setForm(FormDto form) {
    this.form = form;
  }

  public IndexHeaderDataDto results(ResultsDto results) {
    this.results = results;
    return this;
  }

  /**
   * Get results
   * @return results
   */
  @Valid 
  @Schema(name = "results", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("results")
  public ResultsDto getResults() {
    return results;
  }

  public void setResults(ResultsDto results) {
    this.results = results;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IndexHeaderDataDto indexHeaderDataDto = (IndexHeaderDataDto) o;
    return Objects.equals(this.announcement, indexHeaderDataDto.announcement) &&
        Objects.equals(this.config, indexHeaderDataDto.config) &&
        Objects.equals(this.content, indexHeaderDataDto.content) &&
        Objects.equals(this.datePicker, indexHeaderDataDto.datePicker) &&
        Objects.equals(this.form, indexHeaderDataDto.form) &&
        Objects.equals(this.results, indexHeaderDataDto.results);
  }

  @Override
  public int hashCode() {
    return Objects.hash(announcement, config, content, datePicker, form, results);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IndexHeaderDataDto {\n");
    sb.append("    announcement: ").append(toIndentedString(announcement)).append("\n");
    sb.append("    config: ").append(toIndentedString(config)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    datePicker: ").append(toIndentedString(datePicker)).append("\n");
    sb.append("    form: ").append(toIndentedString(form)).append("\n");
    sb.append("    results: ").append(toIndentedString(results)).append("\n");
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


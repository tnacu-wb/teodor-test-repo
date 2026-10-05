package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * IntroViewDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:06.327224+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class IntroViewDto {

  private @Nullable String acceptAllButtonText;

  private @Nullable String description;

  private @Nullable String manageButtonText;

  private @Nullable String title;

  public IntroViewDto acceptAllButtonText(String acceptAllButtonText) {
    this.acceptAllButtonText = acceptAllButtonText;
    return this;
  }

  /**
   * Get acceptAllButtonText
   * @return acceptAllButtonText
   */
  
  @Schema(name = "acceptAllButtonText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("acceptAllButtonText")
  public String getAcceptAllButtonText() {
    return acceptAllButtonText;
  }

  public void setAcceptAllButtonText(String acceptAllButtonText) {
    this.acceptAllButtonText = acceptAllButtonText;
  }

  public IntroViewDto description(String description) {
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

  public IntroViewDto manageButtonText(String manageButtonText) {
    this.manageButtonText = manageButtonText;
    return this;
  }

  /**
   * Get manageButtonText
   * @return manageButtonText
   */
  
  @Schema(name = "manageButtonText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("manageButtonText")
  public String getManageButtonText() {
    return manageButtonText;
  }

  public void setManageButtonText(String manageButtonText) {
    this.manageButtonText = manageButtonText;
  }

  public IntroViewDto title(String title) {
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
    IntroViewDto introViewDto = (IntroViewDto) o;
    return Objects.equals(this.acceptAllButtonText, introViewDto.acceptAllButtonText) &&
        Objects.equals(this.description, introViewDto.description) &&
        Objects.equals(this.manageButtonText, introViewDto.manageButtonText) &&
        Objects.equals(this.title, introViewDto.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(acceptAllButtonText, description, manageButtonText, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IntroViewDto {\n");
    sb.append("    acceptAllButtonText: ").append(toIndentedString(acceptAllButtonText)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    manageButtonText: ").append(toIndentedString(manageButtonText)).append("\n");
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


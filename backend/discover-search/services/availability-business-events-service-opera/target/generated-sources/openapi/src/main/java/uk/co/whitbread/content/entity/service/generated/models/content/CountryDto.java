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
 * CountryDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:46.057591+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CountryDto {

  private @Nullable String flagUrl;

  private @Nullable String language;

  public CountryDto flagUrl(String flagUrl) {
    this.flagUrl = flagUrl;
    return this;
  }

  /**
   * Get flagUrl
   * @return flagUrl
   */
  
  @Schema(name = "flagUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("flagUrl")
  public String getFlagUrl() {
    return flagUrl;
  }

  public void setFlagUrl(String flagUrl) {
    this.flagUrl = flagUrl;
  }

  public CountryDto language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
   */
  
  @Schema(name = "language", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("language")
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CountryDto countryDto = (CountryDto) o;
    return Objects.equals(this.flagUrl, countryDto.flagUrl) &&
        Objects.equals(this.language, countryDto.language);
  }

  @Override
  public int hashCode() {
    return Objects.hash(flagUrl, language);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CountryDto {\n");
    sb.append("    flagUrl: ").append(toIndentedString(flagUrl)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
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


package uk.co.whitbread.content.entity.service.generated.models.content;

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
 * CookieDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:27.565654+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CookieDto {

  private @Nullable String domain;

  private @Nullable String minutesTillExpiry;

  private @Nullable String name;

  public CookieDto domain(String domain) {
    this.domain = domain;
    return this;
  }

  /**
   * Get domain
   * @return domain
   */
  
  @Schema(name = "domain", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("domain")
  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public CookieDto minutesTillExpiry(String minutesTillExpiry) {
    this.minutesTillExpiry = minutesTillExpiry;
    return this;
  }

  /**
   * Get minutesTillExpiry
   * @return minutesTillExpiry
   */
  
  @Schema(name = "minutesTillExpiry", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("minutesTillExpiry")
  public String getMinutesTillExpiry() {
    return minutesTillExpiry;
  }

  public void setMinutesTillExpiry(String minutesTillExpiry) {
    this.minutesTillExpiry = minutesTillExpiry;
  }

  public CookieDto name(String name) {
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
    CookieDto cookieDto = (CookieDto) o;
    return Objects.equals(this.domain, cookieDto.domain) &&
        Objects.equals(this.minutesTillExpiry, cookieDto.minutesTillExpiry) &&
        Objects.equals(this.name, cookieDto.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(domain, minutesTillExpiry, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CookieDto {\n");
    sb.append("    domain: ").append(toIndentedString(domain)).append("\n");
    sb.append("    minutesTillExpiry: ").append(toIndentedString(minutesTillExpiry)).append("\n");
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


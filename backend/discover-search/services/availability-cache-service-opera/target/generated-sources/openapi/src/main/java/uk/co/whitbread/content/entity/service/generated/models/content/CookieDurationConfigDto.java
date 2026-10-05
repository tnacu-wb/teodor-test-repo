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
 * CookieDurationConfigDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CookieDurationConfigDto {

  private @Nullable String cookieOptInExpiryDays;

  private @Nullable String cookieOptOutExpiryDays;

  public CookieDurationConfigDto cookieOptInExpiryDays(String cookieOptInExpiryDays) {
    this.cookieOptInExpiryDays = cookieOptInExpiryDays;
    return this;
  }

  /**
   * Get cookieOptInExpiryDays
   * @return cookieOptInExpiryDays
   */
  
  @Schema(name = "cookieOptInExpiryDays", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cookieOptInExpiryDays")
  public String getCookieOptInExpiryDays() {
    return cookieOptInExpiryDays;
  }

  public void setCookieOptInExpiryDays(String cookieOptInExpiryDays) {
    this.cookieOptInExpiryDays = cookieOptInExpiryDays;
  }

  public CookieDurationConfigDto cookieOptOutExpiryDays(String cookieOptOutExpiryDays) {
    this.cookieOptOutExpiryDays = cookieOptOutExpiryDays;
    return this;
  }

  /**
   * Get cookieOptOutExpiryDays
   * @return cookieOptOutExpiryDays
   */
  
  @Schema(name = "cookieOptOutExpiryDays", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cookieOptOutExpiryDays")
  public String getCookieOptOutExpiryDays() {
    return cookieOptOutExpiryDays;
  }

  public void setCookieOptOutExpiryDays(String cookieOptOutExpiryDays) {
    this.cookieOptOutExpiryDays = cookieOptOutExpiryDays;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CookieDurationConfigDto cookieDurationConfigDto = (CookieDurationConfigDto) o;
    return Objects.equals(this.cookieOptInExpiryDays, cookieDurationConfigDto.cookieOptInExpiryDays) &&
        Objects.equals(this.cookieOptOutExpiryDays, cookieDurationConfigDto.cookieOptOutExpiryDays);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cookieOptInExpiryDays, cookieOptOutExpiryDays);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CookieDurationConfigDto {\n");
    sb.append("    cookieOptInExpiryDays: ").append(toIndentedString(cookieOptInExpiryDays)).append("\n");
    sb.append("    cookieOptOutExpiryDays: ").append(toIndentedString(cookieOptOutExpiryDays)).append("\n");
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


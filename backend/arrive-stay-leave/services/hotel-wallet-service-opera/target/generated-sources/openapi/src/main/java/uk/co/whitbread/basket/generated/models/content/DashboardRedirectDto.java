package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.content.CookieDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * DashboardRedirectDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:24.587145+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class DashboardRedirectDto {

  private @Nullable String bookingReference;

  private @Nullable CookieDto cookie;

  private @Nullable String operaUrl;

  private @Nullable String url;

  public DashboardRedirectDto bookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
    return this;
  }

  /**
   * Get bookingReference
   * @return bookingReference
   */
  
  @Schema(name = "bookingReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingReference")
  public String getBookingReference() {
    return bookingReference;
  }

  public void setBookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
  }

  public DashboardRedirectDto cookie(CookieDto cookie) {
    this.cookie = cookie;
    return this;
  }

  /**
   * Get cookie
   * @return cookie
   */
  @Valid 
  @Schema(name = "cookie", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cookie")
  public CookieDto getCookie() {
    return cookie;
  }

  public void setCookie(CookieDto cookie) {
    this.cookie = cookie;
  }

  public DashboardRedirectDto operaUrl(String operaUrl) {
    this.operaUrl = operaUrl;
    return this;
  }

  /**
   * Get operaUrl
   * @return operaUrl
   */
  
  @Schema(name = "operaUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("operaUrl")
  public String getOperaUrl() {
    return operaUrl;
  }

  public void setOperaUrl(String operaUrl) {
    this.operaUrl = operaUrl;
  }

  public DashboardRedirectDto url(String url) {
    this.url = url;
    return this;
  }

  /**
   * Get url
   * @return url
   */
  
  @Schema(name = "url", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("url")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DashboardRedirectDto dashboardRedirectDto = (DashboardRedirectDto) o;
    return Objects.equals(this.bookingReference, dashboardRedirectDto.bookingReference) &&
        Objects.equals(this.cookie, dashboardRedirectDto.cookie) &&
        Objects.equals(this.operaUrl, dashboardRedirectDto.operaUrl) &&
        Objects.equals(this.url, dashboardRedirectDto.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingReference, cookie, operaUrl, url);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DashboardRedirectDto {\n");
    sb.append("    bookingReference: ").append(toIndentedString(bookingReference)).append("\n");
    sb.append("    cookie: ").append(toIndentedString(cookie)).append("\n");
    sb.append("    operaUrl: ").append(toIndentedString(operaUrl)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
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


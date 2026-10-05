package uk.co.whitbread.basket.generated.models.content;

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
 * BusinessNoteDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:24.587145+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BusinessNoteDto {

  private @Nullable String allow;

  private @Nullable String deny;

  private @Nullable String id;

  private @Nullable String lang;

  public BusinessNoteDto allow(String allow) {
    this.allow = allow;
    return this;
  }

  /**
   * Get allow
   * @return allow
   */
  
  @Schema(name = "allow", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("allow")
  public String getAllow() {
    return allow;
  }

  public void setAllow(String allow) {
    this.allow = allow;
  }

  public BusinessNoteDto deny(String deny) {
    this.deny = deny;
    return this;
  }

  /**
   * Get deny
   * @return deny
   */
  
  @Schema(name = "deny", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("deny")
  public String getDeny() {
    return deny;
  }

  public void setDeny(String deny) {
    this.deny = deny;
  }

  public BusinessNoteDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public BusinessNoteDto lang(String lang) {
    this.lang = lang;
    return this;
  }

  /**
   * Get lang
   * @return lang
   */
  
  @Schema(name = "lang", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lang")
  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BusinessNoteDto businessNoteDto = (BusinessNoteDto) o;
    return Objects.equals(this.allow, businessNoteDto.allow) &&
        Objects.equals(this.deny, businessNoteDto.deny) &&
        Objects.equals(this.id, businessNoteDto.id) &&
        Objects.equals(this.lang, businessNoteDto.lang);
  }

  @Override
  public int hashCode() {
    return Objects.hash(allow, deny, id, lang);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BusinessNoteDto {\n");
    sb.append("    allow: ").append(toIndentedString(allow)).append("\n");
    sb.append("    deny: ").append(toIndentedString(deny)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    lang: ").append(toIndentedString(lang)).append("\n");
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


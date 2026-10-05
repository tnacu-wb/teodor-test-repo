package uk.co.whitbread.basket.generated.models.marketing;

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
 * ContentPermissionData
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:09.701357+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ContentPermissionData {

  private @Nullable Boolean _2ndParty;

  private @Nullable Boolean _3rdParty;

  public ContentPermissionData _2ndParty(Boolean _2ndParty) {
    this._2ndParty = _2ndParty;
    return this;
  }

  /**
   * Get _2ndParty
   * @return _2ndParty
   */
  
  @Schema(name = "2ndParty", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("2ndParty")
  public Boolean get2ndParty() {
    return _2ndParty;
  }

  public void set2ndParty(Boolean _2ndParty) {
    this._2ndParty = _2ndParty;
  }

  public ContentPermissionData _3rdParty(Boolean _3rdParty) {
    this._3rdParty = _3rdParty;
    return this;
  }

  /**
   * Get _3rdParty
   * @return _3rdParty
   */
  
  @Schema(name = "3rdParty", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("3rdParty")
  public Boolean get3rdParty() {
    return _3rdParty;
  }

  public void set3rdParty(Boolean _3rdParty) {
    this._3rdParty = _3rdParty;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContentPermissionData contentPermissionData = (ContentPermissionData) o;
    return Objects.equals(this._2ndParty, contentPermissionData._2ndParty) &&
        Objects.equals(this._3rdParty, contentPermissionData._3rdParty);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_2ndParty, _3rdParty);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContentPermissionData {\n");
    sb.append("    _2ndParty: ").append(toIndentedString(_2ndParty)).append("\n");
    sb.append("    _3rdParty: ").append(toIndentedString(_3rdParty)).append("\n");
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


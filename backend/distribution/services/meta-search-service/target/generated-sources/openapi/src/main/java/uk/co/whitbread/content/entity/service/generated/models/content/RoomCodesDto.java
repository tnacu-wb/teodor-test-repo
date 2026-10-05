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
 * RoomCodesDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:27.565654+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomCodesDto {

  private @Nullable String accessible;

  private @Nullable String _double;

  private @Nullable String family;

  private @Nullable String single;

  private @Nullable String twin;

  public RoomCodesDto accessible(String accessible) {
    this.accessible = accessible;
    return this;
  }

  /**
   * Get accessible
   * @return accessible
   */
  
  @Schema(name = "accessible", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accessible")
  public String getAccessible() {
    return accessible;
  }

  public void setAccessible(String accessible) {
    this.accessible = accessible;
  }

  public RoomCodesDto _double(String _double) {
    this._double = _double;
    return this;
  }

  /**
   * Get _double
   * @return _double
   */
  
  @Schema(name = "double", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("double")
  public String getDouble() {
    return _double;
  }

  public void setDouble(String _double) {
    this._double = _double;
  }

  public RoomCodesDto family(String family) {
    this.family = family;
    return this;
  }

  /**
   * Get family
   * @return family
   */
  
  @Schema(name = "family", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("family")
  public String getFamily() {
    return family;
  }

  public void setFamily(String family) {
    this.family = family;
  }

  public RoomCodesDto single(String single) {
    this.single = single;
    return this;
  }

  /**
   * Get single
   * @return single
   */
  
  @Schema(name = "single", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("single")
  public String getSingle() {
    return single;
  }

  public void setSingle(String single) {
    this.single = single;
  }

  public RoomCodesDto twin(String twin) {
    this.twin = twin;
    return this;
  }

  /**
   * Get twin
   * @return twin
   */
  
  @Schema(name = "twin", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("twin")
  public String getTwin() {
    return twin;
  }

  public void setTwin(String twin) {
    this.twin = twin;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomCodesDto roomCodesDto = (RoomCodesDto) o;
    return Objects.equals(this.accessible, roomCodesDto.accessible) &&
        Objects.equals(this._double, roomCodesDto._double) &&
        Objects.equals(this.family, roomCodesDto.family) &&
        Objects.equals(this.single, roomCodesDto.single) &&
        Objects.equals(this.twin, roomCodesDto.twin);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessible, _double, family, single, twin);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomCodesDto {\n");
    sb.append("    accessible: ").append(toIndentedString(accessible)).append("\n");
    sb.append("    _double: ").append(toIndentedString(_double)).append("\n");
    sb.append("    family: ").append(toIndentedString(family)).append("\n");
    sb.append("    single: ").append(toIndentedString(single)).append("\n");
    sb.append("    twin: ").append(toIndentedString(twin)).append("\n");
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


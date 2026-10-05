package uk.co.whitbread.hotel.info.generated.hotelinfo.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jspecify.annotations.Nullable;
import uk.co.whitbread.hotel.info.generated.hotelinfo.model.Field;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Section
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:34.398121+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Section {

  @Valid
  private List<@Valid Field> fields = new ArrayList<>();

  private @Nullable String labelKey;

  private @Nullable String name;

  public Section fields(List<@Valid Field> fields) {
    this.fields = fields;
    return this;
  }

  public Section addFieldsItem(Field fieldsItem) {
    if (this.fields == null) {
      this.fields = new ArrayList<>();
    }
    this.fields.add(fieldsItem);
    return this;
  }

  /**
   * Get fields
   * @return fields
   */
  @Valid 
  @Schema(name = "fields", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fields")
  public List<@Valid Field> getFields() {
    return fields;
  }

  public void setFields(List<@Valid Field> fields) {
    this.fields = fields;
  }

  public Section labelKey(String labelKey) {
    this.labelKey = labelKey;
    return this;
  }

  /**
   * Get labelKey
   * @return labelKey
   */
  
  @Schema(name = "labelKey", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("labelKey")
  public String getLabelKey() {
    return labelKey;
  }

  public void setLabelKey(String labelKey) {
    this.labelKey = labelKey;
  }

  public Section name(String name) {
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
    Section section = (Section) o;
    return Objects.equals(this.fields, section.fields) &&
        Objects.equals(this.labelKey, section.labelKey) &&
        Objects.equals(this.name, section.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fields, labelKey, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Section {\n");
    sb.append("    fields: ").append(toIndentedString(fields)).append("\n");
    sb.append("    labelKey: ").append(toIndentedString(labelKey)).append("\n");
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


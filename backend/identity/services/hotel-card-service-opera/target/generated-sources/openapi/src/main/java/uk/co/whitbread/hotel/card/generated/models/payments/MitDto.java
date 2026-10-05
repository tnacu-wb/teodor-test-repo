package uk.co.whitbread.hotel.card.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Information about the mit payment
 */

@Schema(name = "Mit", description = "Information about the mit payment")
@JsonTypeName("Mit")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:52.919417+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MitDto {

  private @Nullable String scaReference;

  /**
   * type
   */
  public enum TypeEnum {
    N("N"),
    
    L("L"),
    
    S("S"),
    
    R("R");

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static TypeEnum fromValue(String value) {
      for (TypeEnum b : TypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable TypeEnum type;

  public MitDto scaReference(String scaReference) {
    this.scaReference = scaReference;
    return this;
  }

  /**
   * scaReference
   * @return scaReference
   */
  
  @Schema(name = "scaReference", example = "43686363637", description = "scaReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("scaReference")
  public String getScaReference() {
    return scaReference;
  }

  public void setScaReference(String scaReference) {
    this.scaReference = scaReference;
  }

  public MitDto type(TypeEnum type) {
    this.type = type;
    return this;
  }

  /**
   * type
   * @return type
   */
  
  @Schema(name = "type", example = "N", description = "type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("type")
  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MitDto mit = (MitDto) o;
    return Objects.equals(this.scaReference, mit.scaReference) &&
        Objects.equals(this.type, mit.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(scaReference, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MitDto {\n");
    sb.append("    scaReference: ").append(toIndentedString(scaReference)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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


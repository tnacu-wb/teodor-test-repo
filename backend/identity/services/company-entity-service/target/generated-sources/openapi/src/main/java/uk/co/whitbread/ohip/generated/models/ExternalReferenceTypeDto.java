package uk.co.whitbread.ohip.generated.models;

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
 * ExternalReferenceTypeDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ExternalReferenceTypeDto {

  private @Nullable String id;

  private @Nullable String idContext;

  private @Nullable Integer idExtension;

  public ExternalReferenceTypeDto id(String id) {
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

  public ExternalReferenceTypeDto idContext(String idContext) {
    this.idContext = idContext;
    return this;
  }

  /**
   * Get idContext
   * @return idContext
   */
  
  @Schema(name = "idContext", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("idContext")
  public String getIdContext() {
    return idContext;
  }

  public void setIdContext(String idContext) {
    this.idContext = idContext;
  }

  public ExternalReferenceTypeDto idExtension(Integer idExtension) {
    this.idExtension = idExtension;
    return this;
  }

  /**
   * Get idExtension
   * @return idExtension
   */
  
  @Schema(name = "idExtension", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("idExtension")
  public Integer getIdExtension() {
    return idExtension;
  }

  public void setIdExtension(Integer idExtension) {
    this.idExtension = idExtension;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExternalReferenceTypeDto externalReferenceTypeDto = (ExternalReferenceTypeDto) o;
    return Objects.equals(this.id, externalReferenceTypeDto.id) &&
        Objects.equals(this.idContext, externalReferenceTypeDto.idContext) &&
        Objects.equals(this.idExtension, externalReferenceTypeDto.idExtension);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, idContext, idExtension);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExternalReferenceTypeDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    idContext: ").append(toIndentedString(idContext)).append("\n");
    sb.append("    idExtension: ").append(toIndentedString(idExtension)).append("\n");
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


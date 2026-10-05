package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceExternalRefDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * InvoiceExternalReferencesDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:45.778333+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class InvoiceExternalReferencesDto {

  @Valid
  private List<@Valid InvoiceExternalRefDto> externalRef = new ArrayList<>();

  public InvoiceExternalReferencesDto externalRef(List<@Valid InvoiceExternalRefDto> externalRef) {
    this.externalRef = externalRef;
    return this;
  }

  public InvoiceExternalReferencesDto addExternalRefItem(InvoiceExternalRefDto externalRefItem) {
    if (this.externalRef == null) {
      this.externalRef = new ArrayList<>();
    }
    this.externalRef.add(externalRefItem);
    return this;
  }

  /**
   * Get externalRef
   * @return externalRef
   */
  @Valid 
  @Schema(name = "externalRef", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("externalRef")
  public List<@Valid InvoiceExternalRefDto> getExternalRef() {
    return externalRef;
  }

  public void setExternalRef(List<@Valid InvoiceExternalRefDto> externalRef) {
    this.externalRef = externalRef;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvoiceExternalReferencesDto invoiceExternalReferencesDto = (InvoiceExternalReferencesDto) o;
    return Objects.equals(this.externalRef, invoiceExternalReferencesDto.externalRef);
  }

  @Override
  public int hashCode() {
    return Objects.hash(externalRef);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvoiceExternalReferencesDto {\n");
    sb.append("    externalRef: ").append(toIndentedString(externalRef)).append("\n");
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


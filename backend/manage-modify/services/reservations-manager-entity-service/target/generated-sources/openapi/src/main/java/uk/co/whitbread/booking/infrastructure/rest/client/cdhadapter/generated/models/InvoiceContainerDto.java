package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceDetailsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * InvoiceContainerDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:45.778333+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class InvoiceContainerDto {

  private @Nullable InvoiceDetailsDto invoiceDetails;

  public InvoiceContainerDto invoiceDetails(InvoiceDetailsDto invoiceDetails) {
    this.invoiceDetails = invoiceDetails;
    return this;
  }

  /**
   * Get invoiceDetails
   * @return invoiceDetails
   */
  @Valid 
  @Schema(name = "invoiceDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("invoiceDetails")
  public InvoiceDetailsDto getInvoiceDetails() {
    return invoiceDetails;
  }

  public void setInvoiceDetails(InvoiceDetailsDto invoiceDetails) {
    this.invoiceDetails = invoiceDetails;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvoiceContainerDto invoiceContainerDto = (InvoiceContainerDto) o;
    return Objects.equals(this.invoiceDetails, invoiceContainerDto.invoiceDetails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(invoiceDetails);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvoiceContainerDto {\n");
    sb.append("    invoiceDetails: ").append(toIndentedString(invoiceDetails)).append("\n");
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


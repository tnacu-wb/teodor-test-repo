package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceVatDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * InvoiceVatBreakdownDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:45.778333+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class InvoiceVatBreakdownDto {

  @Valid
  private List<@Valid InvoiceVatDto> vat = new ArrayList<>();

  public InvoiceVatBreakdownDto vat(List<@Valid InvoiceVatDto> vat) {
    this.vat = vat;
    return this;
  }

  public InvoiceVatBreakdownDto addVatItem(InvoiceVatDto vatItem) {
    if (this.vat == null) {
      this.vat = new ArrayList<>();
    }
    this.vat.add(vatItem);
    return this;
  }

  /**
   * Get vat
   * @return vat
   */
  @Valid 
  @Schema(name = "vat", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("vat")
  public List<@Valid InvoiceVatDto> getVat() {
    return vat;
  }

  public void setVat(List<@Valid InvoiceVatDto> vat) {
    this.vat = vat;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvoiceVatBreakdownDto invoiceVatBreakdownDto = (InvoiceVatBreakdownDto) o;
    return Objects.equals(this.vat, invoiceVatBreakdownDto.vat);
  }

  @Override
  public int hashCode() {
    return Objects.hash(vat);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvoiceVatBreakdownDto {\n");
    sb.append("    vat: ").append(toIndentedString(vat)).append("\n");
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


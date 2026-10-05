package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceHotelDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * InvoiceDetailsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:45.778333+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class InvoiceDetailsDto {

  private @Nullable InvoiceHotelDto hotel;

  private @Nullable InvoiceDto invoice;

  public InvoiceDetailsDto hotel(InvoiceHotelDto hotel) {
    this.hotel = hotel;
    return this;
  }

  /**
   * Get hotel
   * @return hotel
   */
  @Valid 
  @Schema(name = "hotel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotel")
  public InvoiceHotelDto getHotel() {
    return hotel;
  }

  public void setHotel(InvoiceHotelDto hotel) {
    this.hotel = hotel;
  }

  public InvoiceDetailsDto invoice(InvoiceDto invoice) {
    this.invoice = invoice;
    return this;
  }

  /**
   * Get invoice
   * @return invoice
   */
  @Valid 
  @Schema(name = "invoice", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("invoice")
  public InvoiceDto getInvoice() {
    return invoice;
  }

  public void setInvoice(InvoiceDto invoice) {
    this.invoice = invoice;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvoiceDetailsDto invoiceDetailsDto = (InvoiceDetailsDto) o;
    return Objects.equals(this.hotel, invoiceDetailsDto.hotel) &&
        Objects.equals(this.invoice, invoiceDetailsDto.invoice);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotel, invoice);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvoiceDetailsDto {\n");
    sb.append("    hotel: ").append(toIndentedString(hotel)).append("\n");
    sb.append("    invoice: ").append(toIndentedString(invoice)).append("\n");
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


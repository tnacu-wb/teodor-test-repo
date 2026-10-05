package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceContainerDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ReservationInvoicesResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:45.778333+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationInvoicesResponseDto {

  @Valid
  private List<@Valid InvoiceContainerDto> invoices = new ArrayList<>();

  private @Nullable Object notFound;

  private @Nullable Integer numberOfResults;

  public ReservationInvoicesResponseDto invoices(List<@Valid InvoiceContainerDto> invoices) {
    this.invoices = invoices;
    return this;
  }

  public ReservationInvoicesResponseDto addInvoicesItem(InvoiceContainerDto invoicesItem) {
    if (this.invoices == null) {
      this.invoices = new ArrayList<>();
    }
    this.invoices.add(invoicesItem);
    return this;
  }

  /**
   * Get invoices
   * @return invoices
   */
  @Valid 
  @Schema(name = "invoices", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("invoices")
  public List<@Valid InvoiceContainerDto> getInvoices() {
    return invoices;
  }

  public void setInvoices(List<@Valid InvoiceContainerDto> invoices) {
    this.invoices = invoices;
  }

  public ReservationInvoicesResponseDto notFound(Object notFound) {
    this.notFound = notFound;
    return this;
  }

  /**
   * Get notFound
   * @return notFound
   */
  
  @Schema(name = "notFound", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("notFound")
  public Object getNotFound() {
    return notFound;
  }

  public void setNotFound(Object notFound) {
    this.notFound = notFound;
  }

  public ReservationInvoicesResponseDto numberOfResults(Integer numberOfResults) {
    this.numberOfResults = numberOfResults;
    return this;
  }

  /**
   * Get numberOfResults
   * @return numberOfResults
   */
  
  @Schema(name = "numberOfResults", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("numberOfResults")
  public Integer getNumberOfResults() {
    return numberOfResults;
  }

  public void setNumberOfResults(Integer numberOfResults) {
    this.numberOfResults = numberOfResults;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationInvoicesResponseDto reservationInvoicesResponseDto = (ReservationInvoicesResponseDto) o;
    return Objects.equals(this.invoices, reservationInvoicesResponseDto.invoices) &&
        Objects.equals(this.notFound, reservationInvoicesResponseDto.notFound) &&
        Objects.equals(this.numberOfResults, reservationInvoicesResponseDto.numberOfResults);
  }

  @Override
  public int hashCode() {
    return Objects.hash(invoices, notFound, numberOfResults);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationInvoicesResponseDto {\n");
    sb.append("    invoices: ").append(toIndentedString(invoices)).append("\n");
    sb.append("    notFound: ").append(toIndentedString(notFound)).append("\n");
    sb.append("    numberOfResults: ").append(toIndentedString(numberOfResults)).append("\n");
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


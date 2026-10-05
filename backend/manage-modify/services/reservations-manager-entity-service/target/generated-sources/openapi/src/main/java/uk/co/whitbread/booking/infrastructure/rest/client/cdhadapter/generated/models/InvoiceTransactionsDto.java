package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceTransactionDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceTseDataDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * InvoiceTransactionsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:45.778333+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class InvoiceTransactionsDto {

  @Valid
  private List<@Valid InvoiceTransactionDto> transaction = new ArrayList<>();

  private @Nullable InvoiceTseDataDto tseData;

  public InvoiceTransactionsDto transaction(List<@Valid InvoiceTransactionDto> transaction) {
    this.transaction = transaction;
    return this;
  }

  public InvoiceTransactionsDto addTransactionItem(InvoiceTransactionDto transactionItem) {
    if (this.transaction == null) {
      this.transaction = new ArrayList<>();
    }
    this.transaction.add(transactionItem);
    return this;
  }

  /**
   * Get transaction
   * @return transaction
   */
  @Valid 
  @Schema(name = "transaction", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("transaction")
  public List<@Valid InvoiceTransactionDto> getTransaction() {
    return transaction;
  }

  public void setTransaction(List<@Valid InvoiceTransactionDto> transaction) {
    this.transaction = transaction;
  }

  public InvoiceTransactionsDto tseData(InvoiceTseDataDto tseData) {
    this.tseData = tseData;
    return this;
  }

  /**
   * Get tseData
   * @return tseData
   */
  @Valid 
  @Schema(name = "tseData", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tseData")
  public InvoiceTseDataDto getTseData() {
    return tseData;
  }

  public void setTseData(InvoiceTseDataDto tseData) {
    this.tseData = tseData;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvoiceTransactionsDto invoiceTransactionsDto = (InvoiceTransactionsDto) o;
    return Objects.equals(this.transaction, invoiceTransactionsDto.transaction) &&
        Objects.equals(this.tseData, invoiceTransactionsDto.tseData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(transaction, tseData);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvoiceTransactionsDto {\n");
    sb.append("    transaction: ").append(toIndentedString(transaction)).append("\n");
    sb.append("    tseData: ").append(toIndentedString(tseData)).append("\n");
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


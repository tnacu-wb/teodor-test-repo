package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * InvoiceTotalsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:45.778333+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class InvoiceTotalsDto {

  private @Nullable String balanceAmount;

  private @Nullable String creditAmount;

  private @Nullable String debitAmount;

  public InvoiceTotalsDto balanceAmount(String balanceAmount) {
    this.balanceAmount = balanceAmount;
    return this;
  }

  /**
   * Get balanceAmount
   * @return balanceAmount
   */
  
  @Schema(name = "balanceAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("balanceAmount")
  public String getBalanceAmount() {
    return balanceAmount;
  }

  public void setBalanceAmount(String balanceAmount) {
    this.balanceAmount = balanceAmount;
  }

  public InvoiceTotalsDto creditAmount(String creditAmount) {
    this.creditAmount = creditAmount;
    return this;
  }

  /**
   * Get creditAmount
   * @return creditAmount
   */
  
  @Schema(name = "creditAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("creditAmount")
  public String getCreditAmount() {
    return creditAmount;
  }

  public void setCreditAmount(String creditAmount) {
    this.creditAmount = creditAmount;
  }

  public InvoiceTotalsDto debitAmount(String debitAmount) {
    this.debitAmount = debitAmount;
    return this;
  }

  /**
   * Get debitAmount
   * @return debitAmount
   */
  
  @Schema(name = "debitAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("debitAmount")
  public String getDebitAmount() {
    return debitAmount;
  }

  public void setDebitAmount(String debitAmount) {
    this.debitAmount = debitAmount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvoiceTotalsDto invoiceTotalsDto = (InvoiceTotalsDto) o;
    return Objects.equals(this.balanceAmount, invoiceTotalsDto.balanceAmount) &&
        Objects.equals(this.creditAmount, invoiceTotalsDto.creditAmount) &&
        Objects.equals(this.debitAmount, invoiceTotalsDto.debitAmount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(balanceAmount, creditAmount, debitAmount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvoiceTotalsDto {\n");
    sb.append("    balanceAmount: ").append(toIndentedString(balanceAmount)).append("\n");
    sb.append("    creditAmount: ").append(toIndentedString(creditAmount)).append("\n");
    sb.append("    debitAmount: ").append(toIndentedString(debitAmount)).append("\n");
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


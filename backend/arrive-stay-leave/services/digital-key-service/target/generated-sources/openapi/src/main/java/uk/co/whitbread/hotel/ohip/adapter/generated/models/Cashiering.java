package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BillingPrivileges;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CheckInTaxType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CompAccounting;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RevenuesAndBalances;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Cashiering
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Cashiering {

  private @Nullable BillingPrivileges billingPrivileges;

  private @Nullable CompAccounting compAccounting;

  private @Nullable RevenuesAndBalances revenuesAndBalances;

  private @Nullable Boolean reverseAdvanceCheckInAllowed;

  private @Nullable Boolean reverseCheckInAllowed;

  private @Nullable CheckInTaxType taxType;

  private @Nullable Boolean transactionsPosted;

  public Cashiering billingPrivileges(BillingPrivileges billingPrivileges) {
    this.billingPrivileges = billingPrivileges;
    return this;
  }

  /**
   * Get billingPrivileges
   * @return billingPrivileges
   */
  @Valid 
  @Schema(name = "billingPrivileges", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("billingPrivileges")
  public BillingPrivileges getBillingPrivileges() {
    return billingPrivileges;
  }

  public void setBillingPrivileges(BillingPrivileges billingPrivileges) {
    this.billingPrivileges = billingPrivileges;
  }

  public Cashiering compAccounting(CompAccounting compAccounting) {
    this.compAccounting = compAccounting;
    return this;
  }

  /**
   * Get compAccounting
   * @return compAccounting
   */
  @Valid 
  @Schema(name = "compAccounting", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("compAccounting")
  public CompAccounting getCompAccounting() {
    return compAccounting;
  }

  public void setCompAccounting(CompAccounting compAccounting) {
    this.compAccounting = compAccounting;
  }

  public Cashiering revenuesAndBalances(RevenuesAndBalances revenuesAndBalances) {
    this.revenuesAndBalances = revenuesAndBalances;
    return this;
  }

  /**
   * Get revenuesAndBalances
   * @return revenuesAndBalances
   */
  @Valid 
  @Schema(name = "revenuesAndBalances", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("revenuesAndBalances")
  public RevenuesAndBalances getRevenuesAndBalances() {
    return revenuesAndBalances;
  }

  public void setRevenuesAndBalances(RevenuesAndBalances revenuesAndBalances) {
    this.revenuesAndBalances = revenuesAndBalances;
  }

  public Cashiering reverseAdvanceCheckInAllowed(Boolean reverseAdvanceCheckInAllowed) {
    this.reverseAdvanceCheckInAllowed = reverseAdvanceCheckInAllowed;
    return this;
  }

  /**
   * Get reverseAdvanceCheckInAllowed
   * @return reverseAdvanceCheckInAllowed
   */
  
  @Schema(name = "reverseAdvanceCheckInAllowed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reverseAdvanceCheckInAllowed")
  public Boolean getReverseAdvanceCheckInAllowed() {
    return reverseAdvanceCheckInAllowed;
  }

  public void setReverseAdvanceCheckInAllowed(Boolean reverseAdvanceCheckInAllowed) {
    this.reverseAdvanceCheckInAllowed = reverseAdvanceCheckInAllowed;
  }

  public Cashiering reverseCheckInAllowed(Boolean reverseCheckInAllowed) {
    this.reverseCheckInAllowed = reverseCheckInAllowed;
    return this;
  }

  /**
   * Get reverseCheckInAllowed
   * @return reverseCheckInAllowed
   */
  
  @Schema(name = "reverseCheckInAllowed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reverseCheckInAllowed")
  public Boolean getReverseCheckInAllowed() {
    return reverseCheckInAllowed;
  }

  public void setReverseCheckInAllowed(Boolean reverseCheckInAllowed) {
    this.reverseCheckInAllowed = reverseCheckInAllowed;
  }

  public Cashiering taxType(CheckInTaxType taxType) {
    this.taxType = taxType;
    return this;
  }

  /**
   * Get taxType
   * @return taxType
   */
  @Valid 
  @Schema(name = "taxType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("taxType")
  public CheckInTaxType getTaxType() {
    return taxType;
  }

  public void setTaxType(CheckInTaxType taxType) {
    this.taxType = taxType;
  }

  public Cashiering transactionsPosted(Boolean transactionsPosted) {
    this.transactionsPosted = transactionsPosted;
    return this;
  }

  /**
   * Get transactionsPosted
   * @return transactionsPosted
   */
  
  @Schema(name = "transactionsPosted", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("transactionsPosted")
  public Boolean getTransactionsPosted() {
    return transactionsPosted;
  }

  public void setTransactionsPosted(Boolean transactionsPosted) {
    this.transactionsPosted = transactionsPosted;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Cashiering cashiering = (Cashiering) o;
    return Objects.equals(this.billingPrivileges, cashiering.billingPrivileges) &&
        Objects.equals(this.compAccounting, cashiering.compAccounting) &&
        Objects.equals(this.revenuesAndBalances, cashiering.revenuesAndBalances) &&
        Objects.equals(this.reverseAdvanceCheckInAllowed, cashiering.reverseAdvanceCheckInAllowed) &&
        Objects.equals(this.reverseCheckInAllowed, cashiering.reverseCheckInAllowed) &&
        Objects.equals(this.taxType, cashiering.taxType) &&
        Objects.equals(this.transactionsPosted, cashiering.transactionsPosted);
  }

  @Override
  public int hashCode() {
    return Objects.hash(billingPrivileges, compAccounting, revenuesAndBalances, reverseAdvanceCheckInAllowed, reverseCheckInAllowed, taxType, transactionsPosted);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Cashiering {\n");
    sb.append("    billingPrivileges: ").append(toIndentedString(billingPrivileges)).append("\n");
    sb.append("    compAccounting: ").append(toIndentedString(compAccounting)).append("\n");
    sb.append("    revenuesAndBalances: ").append(toIndentedString(revenuesAndBalances)).append("\n");
    sb.append("    reverseAdvanceCheckInAllowed: ").append(toIndentedString(reverseAdvanceCheckInAllowed)).append("\n");
    sb.append("    reverseCheckInAllowed: ").append(toIndentedString(reverseCheckInAllowed)).append("\n");
    sb.append("    taxType: ").append(toIndentedString(taxType)).append("\n");
    sb.append("    transactionsPosted: ").append(toIndentedString(transactionsPosted)).append("\n");
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


package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.BillingPrivilegesDto;
import uk.co.whitbread.basket.generated.models.ohip.CheckInTaxTypeDto;
import uk.co.whitbread.basket.generated.models.ohip.CompAccountingDto;
import uk.co.whitbread.basket.generated.models.ohip.RevenuesAndBalancesDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CashieringDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CashieringDto {

  private @Nullable BillingPrivilegesDto billingPrivileges;

  private @Nullable CompAccountingDto compAccounting;

  private @Nullable RevenuesAndBalancesDto revenuesAndBalances;

  private @Nullable Boolean reverseAdvanceCheckInAllowed;

  private @Nullable Boolean reverseCheckInAllowed;

  private @Nullable CheckInTaxTypeDto taxType;

  private @Nullable Boolean transactionsPosted;

  public CashieringDto billingPrivileges(BillingPrivilegesDto billingPrivileges) {
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
  public BillingPrivilegesDto getBillingPrivileges() {
    return billingPrivileges;
  }

  public void setBillingPrivileges(BillingPrivilegesDto billingPrivileges) {
    this.billingPrivileges = billingPrivileges;
  }

  public CashieringDto compAccounting(CompAccountingDto compAccounting) {
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
  public CompAccountingDto getCompAccounting() {
    return compAccounting;
  }

  public void setCompAccounting(CompAccountingDto compAccounting) {
    this.compAccounting = compAccounting;
  }

  public CashieringDto revenuesAndBalances(RevenuesAndBalancesDto revenuesAndBalances) {
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
  public RevenuesAndBalancesDto getRevenuesAndBalances() {
    return revenuesAndBalances;
  }

  public void setRevenuesAndBalances(RevenuesAndBalancesDto revenuesAndBalances) {
    this.revenuesAndBalances = revenuesAndBalances;
  }

  public CashieringDto reverseAdvanceCheckInAllowed(Boolean reverseAdvanceCheckInAllowed) {
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

  public CashieringDto reverseCheckInAllowed(Boolean reverseCheckInAllowed) {
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

  public CashieringDto taxType(CheckInTaxTypeDto taxType) {
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
  public CheckInTaxTypeDto getTaxType() {
    return taxType;
  }

  public void setTaxType(CheckInTaxTypeDto taxType) {
    this.taxType = taxType;
  }

  public CashieringDto transactionsPosted(Boolean transactionsPosted) {
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
    CashieringDto cashieringDto = (CashieringDto) o;
    return Objects.equals(this.billingPrivileges, cashieringDto.billingPrivileges) &&
        Objects.equals(this.compAccounting, cashieringDto.compAccounting) &&
        Objects.equals(this.revenuesAndBalances, cashieringDto.revenuesAndBalances) &&
        Objects.equals(this.reverseAdvanceCheckInAllowed, cashieringDto.reverseAdvanceCheckInAllowed) &&
        Objects.equals(this.reverseCheckInAllowed, cashieringDto.reverseCheckInAllowed) &&
        Objects.equals(this.taxType, cashieringDto.taxType) &&
        Objects.equals(this.transactionsPosted, cashieringDto.transactionsPosted);
  }

  @Override
  public int hashCode() {
    return Objects.hash(billingPrivileges, compAccounting, revenuesAndBalances, reverseAdvanceCheckInAllowed, reverseCheckInAllowed, taxType, transactionsPosted);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CashieringDto {\n");
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


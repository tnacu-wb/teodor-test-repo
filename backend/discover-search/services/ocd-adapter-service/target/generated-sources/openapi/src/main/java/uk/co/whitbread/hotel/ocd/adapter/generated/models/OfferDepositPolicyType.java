package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferDepositPolicyDeadlineType;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.PolicyBasisType;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * The deposit rule associated to the deposit policy.
 */

@Schema(name = "OfferDepositPolicyType", description = "The deposit rule associated to the deposit policy.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferDepositPolicyType {

  private @Nullable String policyCode;

  private @Nullable OfferDepositPolicyDeadlineType deadline;

  private @Nullable PolicyBasisType basisType;

  private @Nullable BigDecimal amount;

  private @Nullable Boolean taxInclusive;

  private @Nullable Boolean nonRefundable;

  public OfferDepositPolicyType policyCode(String policyCode) {
    this.policyCode = policyCode;
    return this;
  }

  /**
   * The code for the deposit rule.
   * @return policyCode
   */
  @Size(max = 20) 
  @Schema(name = "policyCode", example = "DEP_POLICY001", description = "The code for the deposit rule.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("policyCode")
  public String getPolicyCode() {
    return policyCode;
  }

  public void setPolicyCode(String policyCode) {
    this.policyCode = policyCode;
  }

  public OfferDepositPolicyType deadline(OfferDepositPolicyDeadlineType deadline) {
    this.deadline = deadline;
    return this;
  }

  /**
   * Get deadline
   * @return deadline
   */
  @Valid 
  @Schema(name = "deadline", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("deadline")
  public OfferDepositPolicyDeadlineType getDeadline() {
    return deadline;
  }

  public void setDeadline(OfferDepositPolicyDeadlineType deadline) {
    this.deadline = deadline;
  }

  public OfferDepositPolicyType basisType(PolicyBasisType basisType) {
    this.basisType = basisType;
    return this;
  }

  /**
   * Get basisType
   * @return basisType
   */
  @Valid 
  @Schema(name = "basisType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("basisType")
  public PolicyBasisType getBasisType() {
    return basisType;
  }

  public void setBasisType(PolicyBasisType basisType) {
    this.basisType = basisType;
  }

  public OfferDepositPolicyType amount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  /**
   * The total amount of deposit due.
   * @return amount
   */
  @Valid 
  @Schema(name = "amount", example = "55.21", description = "The total amount of deposit due.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amount")
  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public OfferDepositPolicyType taxInclusive(Boolean taxInclusive) {
    this.taxInclusive = taxInclusive;
    return this;
  }

  /**
   * When true the deposit amount due includes taxes.
   * @return taxInclusive
   */
  
  @Schema(name = "taxInclusive", example = "true", description = "When true the deposit amount due includes taxes.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("taxInclusive")
  public Boolean getTaxInclusive() {
    return taxInclusive;
  }

  public void setTaxInclusive(Boolean taxInclusive) {
    this.taxInclusive = taxInclusive;
  }

  public OfferDepositPolicyType nonRefundable(Boolean nonRefundable) {
    this.nonRefundable = nonRefundable;
    return this;
  }

  /**
   * When true the deposit amount is non-refundable.
   * @return nonRefundable
   */
  
  @Schema(name = "nonRefundable", example = "true", description = "When true the deposit amount is non-refundable.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nonRefundable")
  public Boolean getNonRefundable() {
    return nonRefundable;
  }

  public void setNonRefundable(Boolean nonRefundable) {
    this.nonRefundable = nonRefundable;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferDepositPolicyType offerDepositPolicyType = (OfferDepositPolicyType) o;
    return Objects.equals(this.policyCode, offerDepositPolicyType.policyCode) &&
        Objects.equals(this.deadline, offerDepositPolicyType.deadline) &&
        Objects.equals(this.basisType, offerDepositPolicyType.basisType) &&
        Objects.equals(this.amount, offerDepositPolicyType.amount) &&
        Objects.equals(this.taxInclusive, offerDepositPolicyType.taxInclusive) &&
        Objects.equals(this.nonRefundable, offerDepositPolicyType.nonRefundable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(policyCode, deadline, basisType, amount, taxInclusive, nonRefundable);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferDepositPolicyType {\n");
    sb.append("    policyCode: ").append(toIndentedString(policyCode)).append("\n");
    sb.append("    deadline: ").append(toIndentedString(deadline)).append("\n");
    sb.append("    basisType: ").append(toIndentedString(basisType)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    taxInclusive: ").append(toIndentedString(taxInclusive)).append("\n");
    sb.append("    nonRefundable: ").append(toIndentedString(nonRefundable)).append("\n");
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


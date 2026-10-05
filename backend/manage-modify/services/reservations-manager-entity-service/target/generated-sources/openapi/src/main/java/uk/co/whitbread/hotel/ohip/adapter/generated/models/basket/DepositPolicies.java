package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CurrencyAmountType;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * DepositPolicies
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:44.119190+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class DepositPolicies {

  private @Nullable CurrencyAmountType amountDue;

  private @Nullable CurrencyAmountType amountPaid;

  private @Nullable String policyCode;

  public DepositPolicies amountDue(CurrencyAmountType amountDue) {
    this.amountDue = amountDue;
    return this;
  }

  /**
   * Get amountDue
   * @return amountDue
   */
  @Valid 
  @Schema(name = "amountDue", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amountDue")
  public CurrencyAmountType getAmountDue() {
    return amountDue;
  }

  public void setAmountDue(CurrencyAmountType amountDue) {
    this.amountDue = amountDue;
  }

  public DepositPolicies amountPaid(CurrencyAmountType amountPaid) {
    this.amountPaid = amountPaid;
    return this;
  }

  /**
   * Get amountPaid
   * @return amountPaid
   */
  @Valid 
  @Schema(name = "amountPaid", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amountPaid")
  public CurrencyAmountType getAmountPaid() {
    return amountPaid;
  }

  public void setAmountPaid(CurrencyAmountType amountPaid) {
    this.amountPaid = amountPaid;
  }

  public DepositPolicies policyCode(String policyCode) {
    this.policyCode = policyCode;
    return this;
  }

  /**
   * Get policyCode
   * @return policyCode
   */
  
  @Schema(name = "policyCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("policyCode")
  public String getPolicyCode() {
    return policyCode;
  }

  public void setPolicyCode(String policyCode) {
    this.policyCode = policyCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DepositPolicies depositPolicies = (DepositPolicies) o;
    return Objects.equals(this.amountDue, depositPolicies.amountDue) &&
        Objects.equals(this.amountPaid, depositPolicies.amountPaid) &&
        Objects.equals(this.policyCode, depositPolicies.policyCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amountDue, amountPaid, policyCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DepositPolicies {\n");
    sb.append("    amountDue: ").append(toIndentedString(amountDue)).append("\n");
    sb.append("    amountPaid: ").append(toIndentedString(amountPaid)).append("\n");
    sb.append("    policyCode: ").append(toIndentedString(policyCode)).append("\n");
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


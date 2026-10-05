package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.CurrencyAmountTypeSingleCall;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * DepositPoliciesResponseSingleCall
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class DepositPoliciesResponseSingleCall {

  private @Nullable CurrencyAmountTypeSingleCall amountDue;

  private @Nullable CurrencyAmountTypeSingleCall amountPaid;

  private @Nullable String policyCode;

  public DepositPoliciesResponseSingleCall amountDue(CurrencyAmountTypeSingleCall amountDue) {
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
  public CurrencyAmountTypeSingleCall getAmountDue() {
    return amountDue;
  }

  public void setAmountDue(CurrencyAmountTypeSingleCall amountDue) {
    this.amountDue = amountDue;
  }

  public DepositPoliciesResponseSingleCall amountPaid(CurrencyAmountTypeSingleCall amountPaid) {
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
  public CurrencyAmountTypeSingleCall getAmountPaid() {
    return amountPaid;
  }

  public void setAmountPaid(CurrencyAmountTypeSingleCall amountPaid) {
    this.amountPaid = amountPaid;
  }

  public DepositPoliciesResponseSingleCall policyCode(String policyCode) {
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
    DepositPoliciesResponseSingleCall depositPoliciesResponseSingleCall = (DepositPoliciesResponseSingleCall) o;
    return Objects.equals(this.amountDue, depositPoliciesResponseSingleCall.amountDue) &&
        Objects.equals(this.amountPaid, depositPoliciesResponseSingleCall.amountPaid) &&
        Objects.equals(this.policyCode, depositPoliciesResponseSingleCall.policyCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amountDue, amountPaid, policyCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DepositPoliciesResponseSingleCall {\n");
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


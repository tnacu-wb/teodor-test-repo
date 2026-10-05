package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * TransactionDetails
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class TransactionDetails {

  private @Nullable Boolean allowance;

  private @Nullable String calculationRule;

  private @Nullable String currency;

  private @Nullable String postingType;

  public TransactionDetails allowance(Boolean allowance) {
    this.allowance = allowance;
    return this;
  }

  /**
   * Get allowance
   * @return allowance
   */
  
  @Schema(name = "allowance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("allowance")
  public Boolean getAllowance() {
    return allowance;
  }

  public void setAllowance(Boolean allowance) {
    this.allowance = allowance;
  }

  public TransactionDetails calculationRule(String calculationRule) {
    this.calculationRule = calculationRule;
    return this;
  }

  /**
   * Get calculationRule
   * @return calculationRule
   */
  
  @Schema(name = "calculationRule", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("calculationRule")
  public String getCalculationRule() {
    return calculationRule;
  }

  public void setCalculationRule(String calculationRule) {
    this.calculationRule = calculationRule;
  }

  public TransactionDetails currency(String currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Get currency
   * @return currency
   */
  
  @Schema(name = "currency", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currency")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public TransactionDetails postingType(String postingType) {
    this.postingType = postingType;
    return this;
  }

  /**
   * Get postingType
   * @return postingType
   */
  
  @Schema(name = "postingType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("postingType")
  public String getPostingType() {
    return postingType;
  }

  public void setPostingType(String postingType) {
    this.postingType = postingType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TransactionDetails transactionDetails = (TransactionDetails) o;
    return Objects.equals(this.allowance, transactionDetails.allowance) &&
        Objects.equals(this.calculationRule, transactionDetails.calculationRule) &&
        Objects.equals(this.currency, transactionDetails.currency) &&
        Objects.equals(this.postingType, transactionDetails.postingType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(allowance, calculationRule, currency, postingType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransactionDetails {\n");
    sb.append("    allowance: ").append(toIndentedString(allowance)).append("\n");
    sb.append("    calculationRule: ").append(toIndentedString(calculationRule)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    postingType: ").append(toIndentedString(postingType)).append("\n");
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


package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferDepositPolicyPaymentCodeDescriptionType;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferDepositPolicyType;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferPolicyRevenueType;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Deposit Policy Details
 */

@Schema(name = "OfferDepositPolicy", description = "Deposit Policy Details")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferDepositPolicy {

  private @Nullable OfferPolicyRevenueType revenueType;

  private @Nullable OfferDepositPolicyType policy;

  private @Nullable String description;

  private @Nullable String depositPolicyMethod;

  @Valid
  private List<@Valid OfferDepositPolicyPaymentCodeDescriptionType> paymentTypes = new ArrayList<>();

  public OfferDepositPolicy revenueType(OfferPolicyRevenueType revenueType) {
    this.revenueType = revenueType;
    return this;
  }

  /**
   * Get revenueType
   * @return revenueType
   */
  @Valid 
  @Schema(name = "revenueType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("revenueType")
  public OfferPolicyRevenueType getRevenueType() {
    return revenueType;
  }

  public void setRevenueType(OfferPolicyRevenueType revenueType) {
    this.revenueType = revenueType;
  }

  public OfferDepositPolicy policy(OfferDepositPolicyType policy) {
    this.policy = policy;
    return this;
  }

  /**
   * Get policy
   * @return policy
   */
  @Valid 
  @Schema(name = "policy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("policy")
  public OfferDepositPolicyType getPolicy() {
    return policy;
  }

  public void setPolicy(OfferDepositPolicyType policy) {
    this.policy = policy;
  }

  public OfferDepositPolicy description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Description of the deposit rule.
   * @return description
   */
  
  @Schema(name = "description", example = "Deposit policy description in English", description = "Description of the deposit rule.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public OfferDepositPolicy depositPolicyMethod(String depositPolicyMethod) {
    this.depositPolicyMethod = depositPolicyMethod;
    return this;
  }

  /**
   * The methods of payment that can be used to pay the deposit.
   * @return depositPolicyMethod
   */
  
  @Schema(name = "depositPolicyMethod", example = "Deposit can be made by AX, MC, VI", description = "The methods of payment that can be used to pay the deposit.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("depositPolicyMethod")
  public String getDepositPolicyMethod() {
    return depositPolicyMethod;
  }

  public void setDepositPolicyMethod(String depositPolicyMethod) {
    this.depositPolicyMethod = depositPolicyMethod;
  }

  public OfferDepositPolicy paymentTypes(List<@Valid OfferDepositPolicyPaymentCodeDescriptionType> paymentTypes) {
    this.paymentTypes = paymentTypes;
    return this;
  }

  public OfferDepositPolicy addPaymentTypesItem(OfferDepositPolicyPaymentCodeDescriptionType paymentTypesItem) {
    if (this.paymentTypes == null) {
      this.paymentTypes = new ArrayList<>();
    }
    this.paymentTypes.add(paymentTypesItem);
    return this;
  }

  /**
   * List of payment types that can be used to pay the deposit.
   * @return paymentTypes
   */
  @Valid 
  @Schema(name = "paymentTypes", description = "List of payment types that can be used to pay the deposit.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentTypes")
  public List<@Valid OfferDepositPolicyPaymentCodeDescriptionType> getPaymentTypes() {
    return paymentTypes;
  }

  public void setPaymentTypes(List<@Valid OfferDepositPolicyPaymentCodeDescriptionType> paymentTypes) {
    this.paymentTypes = paymentTypes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferDepositPolicy offerDepositPolicy = (OfferDepositPolicy) o;
    return Objects.equals(this.revenueType, offerDepositPolicy.revenueType) &&
        Objects.equals(this.policy, offerDepositPolicy.policy) &&
        Objects.equals(this.description, offerDepositPolicy.description) &&
        Objects.equals(this.depositPolicyMethod, offerDepositPolicy.depositPolicyMethod) &&
        Objects.equals(this.paymentTypes, offerDepositPolicy.paymentTypes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(revenueType, policy, description, depositPolicyMethod, paymentTypes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferDepositPolicy {\n");
    sb.append("    revenueType: ").append(toIndentedString(revenueType)).append("\n");
    sb.append("    policy: ").append(toIndentedString(policy)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    depositPolicyMethod: ").append(toIndentedString(depositPolicyMethod)).append("\n");
    sb.append("    paymentTypes: ").append(toIndentedString(paymentTypes)).append("\n");
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


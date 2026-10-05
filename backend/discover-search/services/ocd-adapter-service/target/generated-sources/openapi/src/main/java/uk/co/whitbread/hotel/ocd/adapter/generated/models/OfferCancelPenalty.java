package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferCancellationPolicyType;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferPolicyRevenueType;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Cancellation Policy Details
 */

@Schema(name = "OfferCancelPenalty", description = "Cancellation Policy Details")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferCancelPenalty {

  private @Nullable OfferPolicyRevenueType revenueType;

  private @Nullable OfferCancellationPolicyType policy;

  public OfferCancelPenalty revenueType(OfferPolicyRevenueType revenueType) {
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

  public OfferCancelPenalty policy(OfferCancellationPolicyType policy) {
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
  public OfferCancellationPolicyType getPolicy() {
    return policy;
  }

  public void setPolicy(OfferCancellationPolicyType policy) {
    this.policy = policy;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferCancelPenalty offerCancelPenalty = (OfferCancelPenalty) o;
    return Objects.equals(this.revenueType, offerCancelPenalty.revenueType) &&
        Objects.equals(this.policy, offerCancelPenalty.policy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(revenueType, policy);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferCancelPenalty {\n");
    sb.append("    revenueType: ").append(toIndentedString(revenueType)).append("\n");
    sb.append("    policy: ").append(toIndentedString(policy)).append("\n");
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


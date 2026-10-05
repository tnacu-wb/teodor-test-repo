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
import uk.co.whitbread.hotel.ocd.adapter.generated.models.GuaranteeRequirementType;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferCancelPenalty;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferDepositPolicy;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferGuaranteePolicy;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferOverallRateInformation;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferRateRange;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Details on the rate plan of the offer.
 */

@Schema(name = "OfferRateInformation", description = "Details on the rate plan of the offer.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferRateInformation {

  private @Nullable OfferOverallRateInformation rate;

  @Valid
  private List<@Valid OfferCancelPenalty> cancellationPolicies = new ArrayList<>();

  private @Nullable GuaranteeRequirementType guaranteeRequirement;

  @Valid
  private List<@Valid OfferDepositPolicy> depositPolicies = new ArrayList<>();

  @Valid
  private List<@Valid OfferGuaranteePolicy> paymentPolicies = new ArrayList<>();

  @Valid
  private List<OfferRateRange> base = new ArrayList<>();

  public OfferRateInformation rate(OfferOverallRateInformation rate) {
    this.rate = rate;
    return this;
  }

  /**
   * Get rate
   * @return rate
   */
  @Valid 
  @Schema(name = "rate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rate")
  public OfferOverallRateInformation getRate() {
    return rate;
  }

  public void setRate(OfferOverallRateInformation rate) {
    this.rate = rate;
  }

  public OfferRateInformation cancellationPolicies(List<@Valid OfferCancelPenalty> cancellationPolicies) {
    this.cancellationPolicies = cancellationPolicies;
    return this;
  }

  public OfferRateInformation addCancellationPoliciesItem(OfferCancelPenalty cancellationPoliciesItem) {
    if (this.cancellationPolicies == null) {
      this.cancellationPolicies = new ArrayList<>();
    }
    this.cancellationPolicies.add(cancellationPoliciesItem);
    return this;
  }

  /**
   * List of cancellation policies associateded to the rate plan.
   * @return cancellationPolicies
   */
  @Valid 
  @Schema(name = "cancellationPolicies", description = "List of cancellation policies associateded to the rate plan.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cancellationPolicies")
  public List<@Valid OfferCancelPenalty> getCancellationPolicies() {
    return cancellationPolicies;
  }

  public void setCancellationPolicies(List<@Valid OfferCancelPenalty> cancellationPolicies) {
    this.cancellationPolicies = cancellationPolicies;
  }

  public OfferRateInformation guaranteeRequirement(GuaranteeRequirementType guaranteeRequirement) {
    this.guaranteeRequirement = guaranteeRequirement;
    return this;
  }

  /**
   * Get guaranteeRequirement
   * @return guaranteeRequirement
   */
  @Valid 
  @Schema(name = "guaranteeRequirement", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("guaranteeRequirement")
  public GuaranteeRequirementType getGuaranteeRequirement() {
    return guaranteeRequirement;
  }

  public void setGuaranteeRequirement(GuaranteeRequirementType guaranteeRequirement) {
    this.guaranteeRequirement = guaranteeRequirement;
  }

  public OfferRateInformation depositPolicies(List<@Valid OfferDepositPolicy> depositPolicies) {
    this.depositPolicies = depositPolicies;
    return this;
  }

  public OfferRateInformation addDepositPoliciesItem(OfferDepositPolicy depositPoliciesItem) {
    if (this.depositPolicies == null) {
      this.depositPolicies = new ArrayList<>();
    }
    this.depositPolicies.add(depositPoliciesItem);
    return this;
  }

  /**
   * List of deposit policies associateded to the rate plan.
   * @return depositPolicies
   */
  @Valid 
  @Schema(name = "depositPolicies", description = "List of deposit policies associateded to the rate plan.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("depositPolicies")
  public List<@Valid OfferDepositPolicy> getDepositPolicies() {
    return depositPolicies;
  }

  public void setDepositPolicies(List<@Valid OfferDepositPolicy> depositPolicies) {
    this.depositPolicies = depositPolicies;
  }

  public OfferRateInformation paymentPolicies(List<@Valid OfferGuaranteePolicy> paymentPolicies) {
    this.paymentPolicies = paymentPolicies;
    return this;
  }

  public OfferRateInformation addPaymentPoliciesItem(OfferGuaranteePolicy paymentPoliciesItem) {
    if (this.paymentPolicies == null) {
      this.paymentPolicies = new ArrayList<>();
    }
    this.paymentPolicies.add(paymentPoliciesItem);
    return this;
  }

  /**
   * List of payment policies for guaranteeing the rate plan.
   * @return paymentPolicies
   */
  @Valid @Size(max = 10) 
  @Schema(name = "paymentPolicies", description = "List of payment policies for guaranteeing the rate plan.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentPolicies")
  public List<@Valid OfferGuaranteePolicy> getPaymentPolicies() {
    return paymentPolicies;
  }

  public void setPaymentPolicies(List<@Valid OfferGuaranteePolicy> paymentPolicies) {
    this.paymentPolicies = paymentPolicies;
  }

  public OfferRateInformation base(List<OfferRateRange> base) {
    this.base = base;
    return this;
  }

  public OfferRateInformation addBaseItem(OfferRateRange baseItem) {
    if (this.base == null) {
      this.base = new ArrayList<>();
    }
    this.base.add(baseItem);
    return this;
  }

  /**
   * List of date ranges within the stay and corresponding charges.
   * @return base
   */
  @Valid 
  @Schema(name = "base", description = "List of date ranges within the stay and corresponding charges.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("base")
  public List<OfferRateRange> getBase() {
    return base;
  }

  public void setBase(List<OfferRateRange> base) {
    this.base = base;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferRateInformation offerRateInformation = (OfferRateInformation) o;
    return Objects.equals(this.rate, offerRateInformation.rate) &&
        Objects.equals(this.cancellationPolicies, offerRateInformation.cancellationPolicies) &&
        Objects.equals(this.guaranteeRequirement, offerRateInformation.guaranteeRequirement) &&
        Objects.equals(this.depositPolicies, offerRateInformation.depositPolicies) &&
        Objects.equals(this.paymentPolicies, offerRateInformation.paymentPolicies) &&
        Objects.equals(this.base, offerRateInformation.base);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rate, cancellationPolicies, guaranteeRequirement, depositPolicies, paymentPolicies, base);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferRateInformation {\n");
    sb.append("    rate: ").append(toIndentedString(rate)).append("\n");
    sb.append("    cancellationPolicies: ").append(toIndentedString(cancellationPolicies)).append("\n");
    sb.append("    guaranteeRequirement: ").append(toIndentedString(guaranteeRequirement)).append("\n");
    sb.append("    depositPolicies: ").append(toIndentedString(depositPolicies)).append("\n");
    sb.append("    paymentPolicies: ").append(toIndentedString(paymentPolicies)).append("\n");
    sb.append("    base: ").append(toIndentedString(base)).append("\n");
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


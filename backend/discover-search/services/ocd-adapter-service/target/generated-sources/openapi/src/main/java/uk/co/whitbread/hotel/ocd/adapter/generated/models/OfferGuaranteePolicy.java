package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.GuaranteePolicyRequirements;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferPaymentCodeDescriptionType;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Policy for guaranteeing reservation.
 */

@Schema(name = "OfferGuaranteePolicy", description = "Policy for guaranteeing reservation.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferGuaranteePolicy {

  private @Nullable String guaranteeCode;

  private @Nullable String guaranteeType;

  private @Nullable String description;

  @Valid
  private List<@Valid OfferPaymentCodeDescriptionType> paymentTypes = new ArrayList<>();

  private @Nullable GuaranteePolicyRequirements policyRequirements;

  private @Nullable Boolean onHold;

  private @Nullable String releaseTime;

  public OfferGuaranteePolicy guaranteeCode(String guaranteeCode) {
    this.guaranteeCode = guaranteeCode;
    return this;
  }

  /**
   * The code of the guarantee type.
   * @return guaranteeCode
   */
  
  @Schema(name = "guaranteeCode", example = "GX", description = "The code of the guarantee type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("guaranteeCode")
  public String getGuaranteeCode() {
    return guaranteeCode;
  }

  public void setGuaranteeCode(String guaranteeCode) {
    this.guaranteeCode = guaranteeCode;
  }

  public OfferGuaranteePolicy guaranteeType(String guaranteeType) {
    this.guaranteeType = guaranteeType;
    return this;
  }

  /**
   * The guarantee type associated with the guarantee code. PMT Valid values: 5 = Credit Card, 8 = Deposit, 19 = Travel agency IATA number, 22 = Frequent guest, 38 = None
   * @return guaranteeType
   */
  
  @Schema(name = "guaranteeType", example = "5", description = "The guarantee type associated with the guarantee code. PMT Valid values: 5 = Credit Card, 8 = Deposit, 19 = Travel agency IATA number, 22 = Frequent guest, 38 = None", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("guaranteeType")
  public String getGuaranteeType() {
    return guaranteeType;
  }

  public void setGuaranteeType(String guaranteeType) {
    this.guaranteeType = guaranteeType;
  }

  public OfferGuaranteePolicy description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Description of the guarantee type.
   * @return description
   */
  
  @Schema(name = "description", example = "Credit Card Guaranteed", description = "Description of the guarantee type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public OfferGuaranteePolicy paymentTypes(List<@Valid OfferPaymentCodeDescriptionType> paymentTypes) {
    this.paymentTypes = paymentTypes;
    return this;
  }

  public OfferGuaranteePolicy addPaymentTypesItem(OfferPaymentCodeDescriptionType paymentTypesItem) {
    if (this.paymentTypes == null) {
      this.paymentTypes = new ArrayList<>();
    }
    this.paymentTypes.add(paymentTypesItem);
    return this;
  }

  /**
   * List of payment types that can be used to guarantee the rate plan.
   * @return paymentTypes
   */
  @Valid 
  @Schema(name = "paymentTypes", description = "List of payment types that can be used to guarantee the rate plan.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentTypes")
  public List<@Valid OfferPaymentCodeDescriptionType> getPaymentTypes() {
    return paymentTypes;
  }

  public void setPaymentTypes(List<@Valid OfferPaymentCodeDescriptionType> paymentTypes) {
    this.paymentTypes = paymentTypes;
  }

  public OfferGuaranteePolicy policyRequirements(GuaranteePolicyRequirements policyRequirements) {
    this.policyRequirements = policyRequirements;
    return this;
  }

  /**
   * Get policyRequirements
   * @return policyRequirements
   */
  @Valid 
  @Schema(name = "policyRequirements", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("policyRequirements")
  public GuaranteePolicyRequirements getPolicyRequirements() {
    return policyRequirements;
  }

  public void setPolicyRequirements(GuaranteePolicyRequirements policyRequirements) {
    this.policyRequirements = policyRequirements;
  }

  public OfferGuaranteePolicy onHold(Boolean onHold) {
    this.onHold = onHold;
    return this;
  }

  /**
   * When true the guarantee type is used to only hold and not guarantee the rate plan, works with releaseTime.
   * @return onHold
   */
  
  @Schema(name = "onHold", example = "false", description = "When true the guarantee type is used to only hold and not guarantee the rate plan, works with releaseTime.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("onHold")
  public Boolean getOnHold() {
    return onHold;
  }

  public void setOnHold(Boolean onHold) {
    this.onHold = onHold;
  }

  public OfferGuaranteePolicy releaseTime(String releaseTime) {
    this.releaseTime = releaseTime;
    return this;
  }

  /**
   * Time till which the non-guaranteed or on hold  rate plan will be held.
   * @return releaseTime
   */
  @Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$") 
  @Schema(name = "releaseTime", example = "22:00", description = "Time till which the non-guaranteed or on hold  rate plan will be held.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("releaseTime")
  public String getReleaseTime() {
    return releaseTime;
  }

  public void setReleaseTime(String releaseTime) {
    this.releaseTime = releaseTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferGuaranteePolicy offerGuaranteePolicy = (OfferGuaranteePolicy) o;
    return Objects.equals(this.guaranteeCode, offerGuaranteePolicy.guaranteeCode) &&
        Objects.equals(this.guaranteeType, offerGuaranteePolicy.guaranteeType) &&
        Objects.equals(this.description, offerGuaranteePolicy.description) &&
        Objects.equals(this.paymentTypes, offerGuaranteePolicy.paymentTypes) &&
        Objects.equals(this.policyRequirements, offerGuaranteePolicy.policyRequirements) &&
        Objects.equals(this.onHold, offerGuaranteePolicy.onHold) &&
        Objects.equals(this.releaseTime, offerGuaranteePolicy.releaseTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(guaranteeCode, guaranteeType, description, paymentTypes, policyRequirements, onHold, releaseTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferGuaranteePolicy {\n");
    sb.append("    guaranteeCode: ").append(toIndentedString(guaranteeCode)).append("\n");
    sb.append("    guaranteeType: ").append(toIndentedString(guaranteeType)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    paymentTypes: ").append(toIndentedString(paymentTypes)).append("\n");
    sb.append("    policyRequirements: ").append(toIndentedString(policyRequirements)).append("\n");
    sb.append("    onHold: ").append(toIndentedString(onHold)).append("\n");
    sb.append("    releaseTime: ").append(toIndentedString(releaseTime)).append("\n");
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


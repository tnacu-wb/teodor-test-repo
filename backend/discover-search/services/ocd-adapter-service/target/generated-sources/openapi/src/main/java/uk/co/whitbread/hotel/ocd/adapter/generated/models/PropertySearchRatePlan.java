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
import uk.co.whitbread.hotel.ocd.adapter.generated.models.AdditionalDetails;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferRatePlanAvailabilityStatus;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.PromotionCodeItem;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.Restriction;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PropertySearchRatePlan
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PropertySearchRatePlan {

  private @Nullable String ratePlanCode;

  private @Nullable String accessCode;

  private @Nullable String ratePlanType;

  private @Nullable Boolean identificationRequired;

  private @Nullable String accountId;

  private @Nullable OfferRatePlanAvailabilityStatus availabilityStatus;

  private @Nullable AdditionalDetails additionalDetails;

  private @Nullable Boolean taxInclusive;

  @Valid
  private List<@Valid PromotionCodeItem> promotionCodes = new ArrayList<>();

  @Valid
  private List<@Valid Restriction> restrictions = new ArrayList<>();

  public PropertySearchRatePlan ratePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
    return this;
  }

  /**
   * The code for the rate plan.
   * @return ratePlanCode
   */
  
  @Schema(name = "ratePlanCode", example = "XDAILY", description = "The code for the rate plan.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanCode")
  public String getRatePlanCode() {
    return ratePlanCode;
  }

  public void setRatePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
  }

  public PropertySearchRatePlan accessCode(String accessCode) {
    this.accessCode = accessCode;
    return this;
  }

  /**
   * The access code for a negotiated rate plan.
   * @return accessCode
   */
  
  @Schema(name = "accessCode", description = "The access code for a negotiated rate plan.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accessCode")
  public String getAccessCode() {
    return accessCode;
  }

  public void setAccessCode(String accessCode) {
    this.accessCode = accessCode;
  }

  public PropertySearchRatePlan ratePlanType(String ratePlanType) {
    this.ratePlanType = ratePlanType;
    return this;
  }

  /**
   * The rate plan type associated with the rate plan.
   * @return ratePlanType
   */
  
  @Schema(name = "ratePlanType", example = "Corporate", description = "The rate plan type associated with the rate plan.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanType")
  public String getRatePlanType() {
    return ratePlanType;
  }

  public void setRatePlanType(String ratePlanType) {
    this.ratePlanType = ratePlanType;
  }

  public PropertySearchRatePlan identificationRequired(Boolean identificationRequired) {
    this.identificationRequired = identificationRequired;
    return this;
  }

  /**
   * When true indicates identification is required at checkin.
   * @return identificationRequired
   */
  
  @Schema(name = "identificationRequired", description = "When true indicates identification is required at checkin.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("identificationRequired")
  public Boolean getIdentificationRequired() {
    return identificationRequired;
  }

  public void setIdentificationRequired(Boolean identificationRequired) {
    this.identificationRequired = identificationRequired;
  }

  public PropertySearchRatePlan accountId(String accountId) {
    this.accountId = accountId;
    return this;
  }

  /**
   * The account id assigned to to specific rate plan.
   * @return accountId
   */
  
  @Schema(name = "accountId", description = "The account id assigned to to specific rate plan.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accountId")
  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public PropertySearchRatePlan availabilityStatus(OfferRatePlanAvailabilityStatus availabilityStatus) {
    this.availabilityStatus = availabilityStatus;
    return this;
  }

  /**
   * Get availabilityStatus
   * @return availabilityStatus
   */
  @Valid 
  @Schema(name = "availabilityStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("availabilityStatus")
  public OfferRatePlanAvailabilityStatus getAvailabilityStatus() {
    return availabilityStatus;
  }

  public void setAvailabilityStatus(OfferRatePlanAvailabilityStatus availabilityStatus) {
    this.availabilityStatus = availabilityStatus;
  }

  public PropertySearchRatePlan additionalDetails(AdditionalDetails additionalDetails) {
    this.additionalDetails = additionalDetails;
    return this;
  }

  /**
   * Get additionalDetails
   * @return additionalDetails
   */
  @Valid 
  @Schema(name = "additionalDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("additionalDetails")
  public AdditionalDetails getAdditionalDetails() {
    return additionalDetails;
  }

  public void setAdditionalDetails(AdditionalDetails additionalDetails) {
    this.additionalDetails = additionalDetails;
  }

  public PropertySearchRatePlan taxInclusive(Boolean taxInclusive) {
    this.taxInclusive = taxInclusive;
    return this;
  }

  /**
   * When true indicates the rate plan includes applicable taxes.
   * @return taxInclusive
   */
  
  @Schema(name = "taxInclusive", example = "true", description = "When true indicates the rate plan includes applicable taxes.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("taxInclusive")
  public Boolean getTaxInclusive() {
    return taxInclusive;
  }

  public void setTaxInclusive(Boolean taxInclusive) {
    this.taxInclusive = taxInclusive;
  }

  public PropertySearchRatePlan promotionCodes(List<@Valid PromotionCodeItem> promotionCodes) {
    this.promotionCodes = promotionCodes;
    return this;
  }

  public PropertySearchRatePlan addPromotionCodesItem(PromotionCodeItem promotionCodesItem) {
    if (this.promotionCodes == null) {
      this.promotionCodes = new ArrayList<>();
    }
    this.promotionCodes.add(promotionCodesItem);
    return this;
  }

  /**
   * List of promotion codes associated with rate plan.
   * @return promotionCodes
   */
  @Valid 
  @Schema(name = "promotionCodes", description = "List of promotion codes associated with rate plan.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promotionCodes")
  public List<@Valid PromotionCodeItem> getPromotionCodes() {
    return promotionCodes;
  }

  public void setPromotionCodes(List<@Valid PromotionCodeItem> promotionCodes) {
    this.promotionCodes = promotionCodes;
  }

  public PropertySearchRatePlan restrictions(List<@Valid Restriction> restrictions) {
    this.restrictions = restrictions;
    return this;
  }

  public PropertySearchRatePlan addRestrictionsItem(Restriction restrictionsItem) {
    if (this.restrictions == null) {
      this.restrictions = new ArrayList<>();
    }
    this.restrictions.add(restrictionsItem);
    return this;
  }

  /**
   * List of restrictions for rate plan, only populated when rate plan codes are given in request
   * @return restrictions
   */
  @Valid 
  @Schema(name = "restrictions", description = "List of restrictions for rate plan, only populated when rate plan codes are given in request", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("restrictions")
  public List<@Valid Restriction> getRestrictions() {
    return restrictions;
  }

  public void setRestrictions(List<@Valid Restriction> restrictions) {
    this.restrictions = restrictions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PropertySearchRatePlan propertySearchRatePlan = (PropertySearchRatePlan) o;
    return Objects.equals(this.ratePlanCode, propertySearchRatePlan.ratePlanCode) &&
        Objects.equals(this.accessCode, propertySearchRatePlan.accessCode) &&
        Objects.equals(this.ratePlanType, propertySearchRatePlan.ratePlanType) &&
        Objects.equals(this.identificationRequired, propertySearchRatePlan.identificationRequired) &&
        Objects.equals(this.accountId, propertySearchRatePlan.accountId) &&
        Objects.equals(this.availabilityStatus, propertySearchRatePlan.availabilityStatus) &&
        Objects.equals(this.additionalDetails, propertySearchRatePlan.additionalDetails) &&
        Objects.equals(this.taxInclusive, propertySearchRatePlan.taxInclusive) &&
        Objects.equals(this.promotionCodes, propertySearchRatePlan.promotionCodes) &&
        Objects.equals(this.restrictions, propertySearchRatePlan.restrictions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ratePlanCode, accessCode, ratePlanType, identificationRequired, accountId, availabilityStatus, additionalDetails, taxInclusive, promotionCodes, restrictions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PropertySearchRatePlan {\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    accessCode: ").append(toIndentedString(accessCode)).append("\n");
    sb.append("    ratePlanType: ").append(toIndentedString(ratePlanType)).append("\n");
    sb.append("    identificationRequired: ").append(toIndentedString(identificationRequired)).append("\n");
    sb.append("    accountId: ").append(toIndentedString(accountId)).append("\n");
    sb.append("    availabilityStatus: ").append(toIndentedString(availabilityStatus)).append("\n");
    sb.append("    additionalDetails: ").append(toIndentedString(additionalDetails)).append("\n");
    sb.append("    taxInclusive: ").append(toIndentedString(taxInclusive)).append("\n");
    sb.append("    promotionCodes: ").append(toIndentedString(promotionCodes)).append("\n");
    sb.append("    restrictions: ").append(toIndentedString(restrictions)).append("\n");
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


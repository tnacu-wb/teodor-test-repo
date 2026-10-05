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
import uk.co.whitbread.hotel.ocd.adapter.generated.models.Description;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferMealPlan;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferRatePlanAvailabilityStatus;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferRatePlanCommission;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.PromotionCodeItem;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.RatePackage;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.Restriction;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Details of the rate plan selected during the request including rate plan information, availability status of the rate plan, and commission.
 */

@Schema(name = "OfferDetailsRatePlan", description = "Details of the rate plan selected during the request including rate plan information, availability status of the rate plan, and commission.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferDetailsRatePlan {

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

  private @Nullable String ratePlanName;

  private @Nullable String ratePlanLevel;

  private @Nullable String ratePlanCategory;

  private @Nullable Description gdsDescription;

  private Boolean commissionable = false;

  private @Nullable String commissionDescription;

  private @Nullable OfferRatePlanCommission commission;

  @Valid
  private List<@Valid RatePackage> packages = new ArrayList<>();

  private @Nullable OfferMealPlan mealPlan;

  public OfferDetailsRatePlan ratePlanCode(String ratePlanCode) {
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

  public OfferDetailsRatePlan accessCode(String accessCode) {
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

  public OfferDetailsRatePlan ratePlanType(String ratePlanType) {
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

  public OfferDetailsRatePlan identificationRequired(Boolean identificationRequired) {
    this.identificationRequired = identificationRequired;
    return this;
  }

  /**
   * Indicates if an ID is required during the Check-In for this rate booking
   * @return identificationRequired
   */
  
  @Schema(name = "identificationRequired", description = "Indicates if an ID is required during the Check-In for this rate booking", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("identificationRequired")
  public Boolean getIdentificationRequired() {
    return identificationRequired;
  }

  public void setIdentificationRequired(Boolean identificationRequired) {
    this.identificationRequired = identificationRequired;
  }

  public OfferDetailsRatePlan accountId(String accountId) {
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

  public OfferDetailsRatePlan availabilityStatus(OfferRatePlanAvailabilityStatus availabilityStatus) {
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

  public OfferDetailsRatePlan additionalDetails(AdditionalDetails additionalDetails) {
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

  public OfferDetailsRatePlan taxInclusive(Boolean taxInclusive) {
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

  public OfferDetailsRatePlan promotionCodes(List<@Valid PromotionCodeItem> promotionCodes) {
    this.promotionCodes = promotionCodes;
    return this;
  }

  public OfferDetailsRatePlan addPromotionCodesItem(PromotionCodeItem promotionCodesItem) {
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

  public OfferDetailsRatePlan restrictions(List<@Valid Restriction> restrictions) {
    this.restrictions = restrictions;
    return this;
  }

  public OfferDetailsRatePlan addRestrictionsItem(Restriction restrictionsItem) {
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

  public OfferDetailsRatePlan ratePlanName(String ratePlanName) {
    this.ratePlanName = ratePlanName;
    return this;
  }

  /**
   * The name of the rate plan.
   * @return ratePlanName
   */
  
  @Schema(name = "ratePlanName", description = "The name of the rate plan.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanName")
  public String getRatePlanName() {
    return ratePlanName;
  }

  public void setRatePlanName(String ratePlanName) {
    this.ratePlanName = ratePlanName;
  }

  public OfferDetailsRatePlan ratePlanLevel(String ratePlanLevel) {
    this.ratePlanLevel = ratePlanLevel;
    return this;
  }

  /**
   * The rate level the rate plan is associated to.
   * @return ratePlanLevel
   */
  
  @Schema(name = "ratePlanLevel", description = "The rate level the rate plan is associated to.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanLevel")
  public String getRatePlanLevel() {
    return ratePlanLevel;
  }

  public void setRatePlanLevel(String ratePlanLevel) {
    this.ratePlanLevel = ratePlanLevel;
  }

  public OfferDetailsRatePlan ratePlanCategory(String ratePlanCategory) {
    this.ratePlanCategory = ratePlanCategory;
    return this;
  }

  /**
   * The rate category the rate plan is associated to.
   * @return ratePlanCategory
   */
  
  @Schema(name = "ratePlanCategory", description = "The rate category the rate plan is associated to.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanCategory")
  public String getRatePlanCategory() {
    return ratePlanCategory;
  }

  public void setRatePlanCategory(String ratePlanCategory) {
    this.ratePlanCategory = ratePlanCategory;
  }

  public OfferDetailsRatePlan gdsDescription(Description gdsDescription) {
    this.gdsDescription = gdsDescription;
    return this;
  }

  /**
   * Get gdsDescription
   * @return gdsDescription
   */
  @Valid 
  @Schema(name = "gdsDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("gdsDescription")
  public Description getGdsDescription() {
    return gdsDescription;
  }

  public void setGdsDescription(Description gdsDescription) {
    this.gdsDescription = gdsDescription;
  }

  public OfferDetailsRatePlan commissionable(Boolean commissionable) {
    this.commissionable = commissionable;
    return this;
  }

  /**
   * When true the Rate Plan is commissionable.
   * @return commissionable
   */
  
  @Schema(name = "commissionable", example = "true", description = "When true the Rate Plan is commissionable.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("commissionable")
  public Boolean getCommissionable() {
    return commissionable;
  }

  public void setCommissionable(Boolean commissionable) {
    this.commissionable = commissionable;
  }

  public OfferDetailsRatePlan commissionDescription(String commissionDescription) {
    this.commissionDescription = commissionDescription;
    return this;
  }

  /**
   * Description of the commission associated to the rate plan.
   * @return commissionDescription
   */
  
  @Schema(name = "commissionDescription", example = "Commission of 10% available.", description = "Description of the commission associated to the rate plan.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("commissionDescription")
  public String getCommissionDescription() {
    return commissionDescription;
  }

  public void setCommissionDescription(String commissionDescription) {
    this.commissionDescription = commissionDescription;
  }

  public OfferDetailsRatePlan commission(OfferRatePlanCommission commission) {
    this.commission = commission;
    return this;
  }

  /**
   * Get commission
   * @return commission
   */
  @Valid 
  @Schema(name = "commission", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("commission")
  public OfferRatePlanCommission getCommission() {
    return commission;
  }

  public void setCommission(OfferRatePlanCommission commission) {
    this.commission = commission;
  }

  public OfferDetailsRatePlan packages(List<@Valid RatePackage> packages) {
    this.packages = packages;
    return this;
  }

  public OfferDetailsRatePlan addPackagesItem(RatePackage packagesItem) {
    if (this.packages == null) {
      this.packages = new ArrayList<>();
    }
    this.packages.add(packagesItem);
    return this;
  }

  /**
   * List of package elements and/or package groups associateded to the rate plan.
   * @return packages
   */
  @Valid 
  @Schema(name = "packages", description = "List of package elements and/or package groups associateded to the rate plan.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("packages")
  public List<@Valid RatePackage> getPackages() {
    return packages;
  }

  public void setPackages(List<@Valid RatePackage> packages) {
    this.packages = packages;
  }

  public OfferDetailsRatePlan mealPlan(OfferMealPlan mealPlan) {
    this.mealPlan = mealPlan;
    return this;
  }

  /**
   * Get mealPlan
   * @return mealPlan
   */
  @Valid 
  @Schema(name = "mealPlan", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mealPlan")
  public OfferMealPlan getMealPlan() {
    return mealPlan;
  }

  public void setMealPlan(OfferMealPlan mealPlan) {
    this.mealPlan = mealPlan;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferDetailsRatePlan offerDetailsRatePlan = (OfferDetailsRatePlan) o;
    return Objects.equals(this.ratePlanCode, offerDetailsRatePlan.ratePlanCode) &&
        Objects.equals(this.accessCode, offerDetailsRatePlan.accessCode) &&
        Objects.equals(this.ratePlanType, offerDetailsRatePlan.ratePlanType) &&
        Objects.equals(this.identificationRequired, offerDetailsRatePlan.identificationRequired) &&
        Objects.equals(this.accountId, offerDetailsRatePlan.accountId) &&
        Objects.equals(this.availabilityStatus, offerDetailsRatePlan.availabilityStatus) &&
        Objects.equals(this.additionalDetails, offerDetailsRatePlan.additionalDetails) &&
        Objects.equals(this.taxInclusive, offerDetailsRatePlan.taxInclusive) &&
        Objects.equals(this.promotionCodes, offerDetailsRatePlan.promotionCodes) &&
        Objects.equals(this.restrictions, offerDetailsRatePlan.restrictions) &&
        Objects.equals(this.ratePlanName, offerDetailsRatePlan.ratePlanName) &&
        Objects.equals(this.ratePlanLevel, offerDetailsRatePlan.ratePlanLevel) &&
        Objects.equals(this.ratePlanCategory, offerDetailsRatePlan.ratePlanCategory) &&
        Objects.equals(this.gdsDescription, offerDetailsRatePlan.gdsDescription) &&
        Objects.equals(this.commissionable, offerDetailsRatePlan.commissionable) &&
        Objects.equals(this.commissionDescription, offerDetailsRatePlan.commissionDescription) &&
        Objects.equals(this.commission, offerDetailsRatePlan.commission) &&
        Objects.equals(this.packages, offerDetailsRatePlan.packages) &&
        Objects.equals(this.mealPlan, offerDetailsRatePlan.mealPlan);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ratePlanCode, accessCode, ratePlanType, identificationRequired, accountId, availabilityStatus, additionalDetails, taxInclusive, promotionCodes, restrictions, ratePlanName, ratePlanLevel, ratePlanCategory, gdsDescription, commissionable, commissionDescription, commission, packages, mealPlan);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferDetailsRatePlan {\n");
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
    sb.append("    ratePlanName: ").append(toIndentedString(ratePlanName)).append("\n");
    sb.append("    ratePlanLevel: ").append(toIndentedString(ratePlanLevel)).append("\n");
    sb.append("    ratePlanCategory: ").append(toIndentedString(ratePlanCategory)).append("\n");
    sb.append("    gdsDescription: ").append(toIndentedString(gdsDescription)).append("\n");
    sb.append("    commissionable: ").append(toIndentedString(commissionable)).append("\n");
    sb.append("    commissionDescription: ").append(toIndentedString(commissionDescription)).append("\n");
    sb.append("    commission: ").append(toIndentedString(commission)).append("\n");
    sb.append("    packages: ").append(toIndentedString(packages)).append("\n");
    sb.append("    mealPlan: ").append(toIndentedString(mealPlan)).append("\n");
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


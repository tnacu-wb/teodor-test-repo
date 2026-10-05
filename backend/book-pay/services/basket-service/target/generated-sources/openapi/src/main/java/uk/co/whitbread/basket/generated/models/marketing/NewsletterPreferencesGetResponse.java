package uk.co.whitbread.basket.generated.models.marketing;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.marketing.BrandPermission;
import uk.co.whitbread.basket.generated.models.marketing.LoyaltyAccount;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * NewsletterPreferencesGetResponse
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:09.701357+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class NewsletterPreferencesGetResponse {

  @Valid
  private List<@Valid BrandPermission> brandPermissions = new ArrayList<>();

  private @Nullable String contactChannelId;

  private @Nullable String contactChannelValue;

  private @Nullable Boolean deleted;

  private @Nullable String doNotContact;

  private @Nullable String doNotContactAppliedDate;

  private @Nullable Boolean isValid;

  private @Nullable String lastQuarantineDate;

  @Valid
  private List<@Valid LoyaltyAccount> loyaltyAccounts = new ArrayList<>();

  private @Nullable String modified;

  private @Nullable Boolean shared;

  @Valid
  private List<String> sharedBy = new ArrayList<>();

  private @Nullable Boolean valid;

  public NewsletterPreferencesGetResponse brandPermissions(List<@Valid BrandPermission> brandPermissions) {
    this.brandPermissions = brandPermissions;
    return this;
  }

  public NewsletterPreferencesGetResponse addBrandPermissionsItem(BrandPermission brandPermissionsItem) {
    if (this.brandPermissions == null) {
      this.brandPermissions = new ArrayList<>();
    }
    this.brandPermissions.add(brandPermissionsItem);
    return this;
  }

  /**
   * Get brandPermissions
   * @return brandPermissions
   */
  @Valid 
  @Schema(name = "brandPermissions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("brandPermissions")
  public List<@Valid BrandPermission> getBrandPermissions() {
    return brandPermissions;
  }

  public void setBrandPermissions(List<@Valid BrandPermission> brandPermissions) {
    this.brandPermissions = brandPermissions;
  }

  public NewsletterPreferencesGetResponse contactChannelId(String contactChannelId) {
    this.contactChannelId = contactChannelId;
    return this;
  }

  /**
   * Get contactChannelId
   * @return contactChannelId
   */
  
  @Schema(name = "contactChannelId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("contactChannelId")
  public String getContactChannelId() {
    return contactChannelId;
  }

  public void setContactChannelId(String contactChannelId) {
    this.contactChannelId = contactChannelId;
  }

  public NewsletterPreferencesGetResponse contactChannelValue(String contactChannelValue) {
    this.contactChannelValue = contactChannelValue;
    return this;
  }

  /**
   * Get contactChannelValue
   * @return contactChannelValue
   */
  
  @Schema(name = "contactChannelValue", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("contactChannelValue")
  public String getContactChannelValue() {
    return contactChannelValue;
  }

  public void setContactChannelValue(String contactChannelValue) {
    this.contactChannelValue = contactChannelValue;
  }

  public NewsletterPreferencesGetResponse deleted(Boolean deleted) {
    this.deleted = deleted;
    return this;
  }

  /**
   * Get deleted
   * @return deleted
   */
  
  @Schema(name = "deleted", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("deleted")
  public Boolean getDeleted() {
    return deleted;
  }

  public void setDeleted(Boolean deleted) {
    this.deleted = deleted;
  }

  public NewsletterPreferencesGetResponse doNotContact(String doNotContact) {
    this.doNotContact = doNotContact;
    return this;
  }

  /**
   * Get doNotContact
   * @return doNotContact
   */
  
  @Schema(name = "doNotContact", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("doNotContact")
  public String getDoNotContact() {
    return doNotContact;
  }

  public void setDoNotContact(String doNotContact) {
    this.doNotContact = doNotContact;
  }

  public NewsletterPreferencesGetResponse doNotContactAppliedDate(String doNotContactAppliedDate) {
    this.doNotContactAppliedDate = doNotContactAppliedDate;
    return this;
  }

  /**
   * Get doNotContactAppliedDate
   * @return doNotContactAppliedDate
   */
  
  @Schema(name = "doNotContactAppliedDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("doNotContactAppliedDate")
  public String getDoNotContactAppliedDate() {
    return doNotContactAppliedDate;
  }

  public void setDoNotContactAppliedDate(String doNotContactAppliedDate) {
    this.doNotContactAppliedDate = doNotContactAppliedDate;
  }

  public NewsletterPreferencesGetResponse isValid(Boolean isValid) {
    this.isValid = isValid;
    return this;
  }

  /**
   * Get isValid
   * @return isValid
   */
  
  @Schema(name = "isValid", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isValid")
  public Boolean getIsValid() {
    return isValid;
  }

  public void setIsValid(Boolean isValid) {
    this.isValid = isValid;
  }

  public NewsletterPreferencesGetResponse lastQuarantineDate(String lastQuarantineDate) {
    this.lastQuarantineDate = lastQuarantineDate;
    return this;
  }

  /**
   * Get lastQuarantineDate
   * @return lastQuarantineDate
   */
  
  @Schema(name = "lastQuarantineDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastQuarantineDate")
  public String getLastQuarantineDate() {
    return lastQuarantineDate;
  }

  public void setLastQuarantineDate(String lastQuarantineDate) {
    this.lastQuarantineDate = lastQuarantineDate;
  }

  public NewsletterPreferencesGetResponse loyaltyAccounts(List<@Valid LoyaltyAccount> loyaltyAccounts) {
    this.loyaltyAccounts = loyaltyAccounts;
    return this;
  }

  public NewsletterPreferencesGetResponse addLoyaltyAccountsItem(LoyaltyAccount loyaltyAccountsItem) {
    if (this.loyaltyAccounts == null) {
      this.loyaltyAccounts = new ArrayList<>();
    }
    this.loyaltyAccounts.add(loyaltyAccountsItem);
    return this;
  }

  /**
   * Get loyaltyAccounts
   * @return loyaltyAccounts
   */
  @Valid 
  @Schema(name = "loyaltyAccounts", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("loyaltyAccounts")
  public List<@Valid LoyaltyAccount> getLoyaltyAccounts() {
    return loyaltyAccounts;
  }

  public void setLoyaltyAccounts(List<@Valid LoyaltyAccount> loyaltyAccounts) {
    this.loyaltyAccounts = loyaltyAccounts;
  }

  public NewsletterPreferencesGetResponse modified(String modified) {
    this.modified = modified;
    return this;
  }

  /**
   * Get modified
   * @return modified
   */
  
  @Schema(name = "modified", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("modified")
  public String getModified() {
    return modified;
  }

  public void setModified(String modified) {
    this.modified = modified;
  }

  public NewsletterPreferencesGetResponse shared(Boolean shared) {
    this.shared = shared;
    return this;
  }

  /**
   * Get shared
   * @return shared
   */
  
  @Schema(name = "shared", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("shared")
  public Boolean getShared() {
    return shared;
  }

  public void setShared(Boolean shared) {
    this.shared = shared;
  }

  public NewsletterPreferencesGetResponse sharedBy(List<String> sharedBy) {
    this.sharedBy = sharedBy;
    return this;
  }

  public NewsletterPreferencesGetResponse addSharedByItem(String sharedByItem) {
    if (this.sharedBy == null) {
      this.sharedBy = new ArrayList<>();
    }
    this.sharedBy.add(sharedByItem);
    return this;
  }

  /**
   * Get sharedBy
   * @return sharedBy
   */
  
  @Schema(name = "sharedBy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sharedBy")
  public List<String> getSharedBy() {
    return sharedBy;
  }

  public void setSharedBy(List<String> sharedBy) {
    this.sharedBy = sharedBy;
  }

  public NewsletterPreferencesGetResponse valid(Boolean valid) {
    this.valid = valid;
    return this;
  }

  /**
   * Get valid
   * @return valid
   */
  
  @Schema(name = "valid", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("valid")
  public Boolean getValid() {
    return valid;
  }

  public void setValid(Boolean valid) {
    this.valid = valid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NewsletterPreferencesGetResponse newsletterPreferencesGetResponse = (NewsletterPreferencesGetResponse) o;
    return Objects.equals(this.brandPermissions, newsletterPreferencesGetResponse.brandPermissions) &&
        Objects.equals(this.contactChannelId, newsletterPreferencesGetResponse.contactChannelId) &&
        Objects.equals(this.contactChannelValue, newsletterPreferencesGetResponse.contactChannelValue) &&
        Objects.equals(this.deleted, newsletterPreferencesGetResponse.deleted) &&
        Objects.equals(this.doNotContact, newsletterPreferencesGetResponse.doNotContact) &&
        Objects.equals(this.doNotContactAppliedDate, newsletterPreferencesGetResponse.doNotContactAppliedDate) &&
        Objects.equals(this.isValid, newsletterPreferencesGetResponse.isValid) &&
        Objects.equals(this.lastQuarantineDate, newsletterPreferencesGetResponse.lastQuarantineDate) &&
        Objects.equals(this.loyaltyAccounts, newsletterPreferencesGetResponse.loyaltyAccounts) &&
        Objects.equals(this.modified, newsletterPreferencesGetResponse.modified) &&
        Objects.equals(this.shared, newsletterPreferencesGetResponse.shared) &&
        Objects.equals(this.sharedBy, newsletterPreferencesGetResponse.sharedBy) &&
        Objects.equals(this.valid, newsletterPreferencesGetResponse.valid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(brandPermissions, contactChannelId, contactChannelValue, deleted, doNotContact, doNotContactAppliedDate, isValid, lastQuarantineDate, loyaltyAccounts, modified, shared, sharedBy, valid);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NewsletterPreferencesGetResponse {\n");
    sb.append("    brandPermissions: ").append(toIndentedString(brandPermissions)).append("\n");
    sb.append("    contactChannelId: ").append(toIndentedString(contactChannelId)).append("\n");
    sb.append("    contactChannelValue: ").append(toIndentedString(contactChannelValue)).append("\n");
    sb.append("    deleted: ").append(toIndentedString(deleted)).append("\n");
    sb.append("    doNotContact: ").append(toIndentedString(doNotContact)).append("\n");
    sb.append("    doNotContactAppliedDate: ").append(toIndentedString(doNotContactAppliedDate)).append("\n");
    sb.append("    isValid: ").append(toIndentedString(isValid)).append("\n");
    sb.append("    lastQuarantineDate: ").append(toIndentedString(lastQuarantineDate)).append("\n");
    sb.append("    loyaltyAccounts: ").append(toIndentedString(loyaltyAccounts)).append("\n");
    sb.append("    modified: ").append(toIndentedString(modified)).append("\n");
    sb.append("    shared: ").append(toIndentedString(shared)).append("\n");
    sb.append("    sharedBy: ").append(toIndentedString(sharedBy)).append("\n");
    sb.append("    valid: ").append(toIndentedString(valid)).append("\n");
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


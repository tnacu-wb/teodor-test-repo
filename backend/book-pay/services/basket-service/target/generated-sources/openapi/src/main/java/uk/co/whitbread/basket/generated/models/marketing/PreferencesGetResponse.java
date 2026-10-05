package uk.co.whitbread.basket.generated.models.marketing;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.marketing.LoyaltyAccount;
import uk.co.whitbread.basket.generated.models.marketing.Permission;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PreferencesGetResponse
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:09.701357+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PreferencesGetResponse {

  private @Nullable String contactChannelId;

  private @Nullable String contactChannelValue;

  private @Nullable Boolean deleted;

  @Valid
  private List<@Valid LoyaltyAccount> loyaltyAccounts = new ArrayList<>();

  @Valid
  private List<@Valid Permission> permissions = new ArrayList<>();

  private @Nullable Boolean valid;

  public PreferencesGetResponse contactChannelId(String contactChannelId) {
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

  public PreferencesGetResponse contactChannelValue(String contactChannelValue) {
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

  public PreferencesGetResponse deleted(Boolean deleted) {
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

  public PreferencesGetResponse loyaltyAccounts(List<@Valid LoyaltyAccount> loyaltyAccounts) {
    this.loyaltyAccounts = loyaltyAccounts;
    return this;
  }

  public PreferencesGetResponse addLoyaltyAccountsItem(LoyaltyAccount loyaltyAccountsItem) {
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

  public PreferencesGetResponse permissions(List<@Valid Permission> permissions) {
    this.permissions = permissions;
    return this;
  }

  public PreferencesGetResponse addPermissionsItem(Permission permissionsItem) {
    if (this.permissions == null) {
      this.permissions = new ArrayList<>();
    }
    this.permissions.add(permissionsItem);
    return this;
  }

  /**
   * Get permissions
   * @return permissions
   */
  @Valid 
  @Schema(name = "permissions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("permissions")
  public List<@Valid Permission> getPermissions() {
    return permissions;
  }

  public void setPermissions(List<@Valid Permission> permissions) {
    this.permissions = permissions;
  }

  public PreferencesGetResponse valid(Boolean valid) {
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
    PreferencesGetResponse preferencesGetResponse = (PreferencesGetResponse) o;
    return Objects.equals(this.contactChannelId, preferencesGetResponse.contactChannelId) &&
        Objects.equals(this.contactChannelValue, preferencesGetResponse.contactChannelValue) &&
        Objects.equals(this.deleted, preferencesGetResponse.deleted) &&
        Objects.equals(this.loyaltyAccounts, preferencesGetResponse.loyaltyAccounts) &&
        Objects.equals(this.permissions, preferencesGetResponse.permissions) &&
        Objects.equals(this.valid, preferencesGetResponse.valid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contactChannelId, contactChannelValue, deleted, loyaltyAccounts, permissions, valid);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PreferencesGetResponse {\n");
    sb.append("    contactChannelId: ").append(toIndentedString(contactChannelId)).append("\n");
    sb.append("    contactChannelValue: ").append(toIndentedString(contactChannelValue)).append("\n");
    sb.append("    deleted: ").append(toIndentedString(deleted)).append("\n");
    sb.append("    loyaltyAccounts: ").append(toIndentedString(loyaltyAccounts)).append("\n");
    sb.append("    permissions: ").append(toIndentedString(permissions)).append("\n");
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


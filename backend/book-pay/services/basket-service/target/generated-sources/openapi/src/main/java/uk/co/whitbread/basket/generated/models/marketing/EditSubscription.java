package uk.co.whitbread.basket.generated.models.marketing;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.marketing.ContentPermission;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * EditSubscription
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:09.701357+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class EditSubscription {

  @Valid
  private List<String> brandCodes = new ArrayList<>();

  private @Nullable String contactChannelId;

  private Boolean contactChannelPermission;

  private @Nullable String contactChannelSubType;

  /**
   * Gets or Sets contactChannelType
   */
  public enum ContactChannelTypeEnum {
    EMAIL("Email"),
    
    TELEPHONE("Telephone");

    private String value;

    ContactChannelTypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ContactChannelTypeEnum fromValue(String value) {
      for (ContactChannelTypeEnum b : ContactChannelTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private ContactChannelTypeEnum contactChannelType;

  private @Nullable String contactChannelValue;

  private @Nullable ContentPermission contentPermission;

  public EditSubscription() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EditSubscription(List<String> brandCodes, Boolean contactChannelPermission, ContactChannelTypeEnum contactChannelType) {
    this.brandCodes = brandCodes;
    this.contactChannelPermission = contactChannelPermission;
    this.contactChannelType = contactChannelType;
  }

  public EditSubscription brandCodes(List<String> brandCodes) {
    this.brandCodes = brandCodes;
    return this;
  }

  public EditSubscription addBrandCodesItem(String brandCodesItem) {
    if (this.brandCodes == null) {
      this.brandCodes = new ArrayList<>();
    }
    this.brandCodes.add(brandCodesItem);
    return this;
  }

  /**
   * Get brandCodes
   * @return brandCodes
   */
  @NotNull 
  @Schema(name = "brandCodes", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("brandCodes")
  public List<String> getBrandCodes() {
    return brandCodes;
  }

  public void setBrandCodes(List<String> brandCodes) {
    this.brandCodes = brandCodes;
  }

  public EditSubscription contactChannelId(String contactChannelId) {
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

  public EditSubscription contactChannelPermission(Boolean contactChannelPermission) {
    this.contactChannelPermission = contactChannelPermission;
    return this;
  }

  /**
   * Get contactChannelPermission
   * @return contactChannelPermission
   */
  @NotNull 
  @Schema(name = "contactChannelPermission", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("contactChannelPermission")
  public Boolean getContactChannelPermission() {
    return contactChannelPermission;
  }

  public void setContactChannelPermission(Boolean contactChannelPermission) {
    this.contactChannelPermission = contactChannelPermission;
  }

  public EditSubscription contactChannelSubType(String contactChannelSubType) {
    this.contactChannelSubType = contactChannelSubType;
    return this;
  }

  /**
   * Get contactChannelSubType
   * @return contactChannelSubType
   */
  
  @Schema(name = "contactChannelSubType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("contactChannelSubType")
  public String getContactChannelSubType() {
    return contactChannelSubType;
  }

  public void setContactChannelSubType(String contactChannelSubType) {
    this.contactChannelSubType = contactChannelSubType;
  }

  public EditSubscription contactChannelType(ContactChannelTypeEnum contactChannelType) {
    this.contactChannelType = contactChannelType;
    return this;
  }

  /**
   * Get contactChannelType
   * @return contactChannelType
   */
  @NotNull 
  @Schema(name = "contactChannelType", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("contactChannelType")
  public ContactChannelTypeEnum getContactChannelType() {
    return contactChannelType;
  }

  public void setContactChannelType(ContactChannelTypeEnum contactChannelType) {
    this.contactChannelType = contactChannelType;
  }

  public EditSubscription contactChannelValue(String contactChannelValue) {
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

  public EditSubscription contentPermission(ContentPermission contentPermission) {
    this.contentPermission = contentPermission;
    return this;
  }

  /**
   * Get contentPermission
   * @return contentPermission
   */
  @Valid 
  @Schema(name = "contentPermission", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("contentPermission")
  public ContentPermission getContentPermission() {
    return contentPermission;
  }

  public void setContentPermission(ContentPermission contentPermission) {
    this.contentPermission = contentPermission;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EditSubscription editSubscription = (EditSubscription) o;
    return Objects.equals(this.brandCodes, editSubscription.brandCodes) &&
        Objects.equals(this.contactChannelId, editSubscription.contactChannelId) &&
        Objects.equals(this.contactChannelPermission, editSubscription.contactChannelPermission) &&
        Objects.equals(this.contactChannelSubType, editSubscription.contactChannelSubType) &&
        Objects.equals(this.contactChannelType, editSubscription.contactChannelType) &&
        Objects.equals(this.contactChannelValue, editSubscription.contactChannelValue) &&
        Objects.equals(this.contentPermission, editSubscription.contentPermission);
  }

  @Override
  public int hashCode() {
    return Objects.hash(brandCodes, contactChannelId, contactChannelPermission, contactChannelSubType, contactChannelType, contactChannelValue, contentPermission);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EditSubscription {\n");
    sb.append("    brandCodes: ").append(toIndentedString(brandCodes)).append("\n");
    sb.append("    contactChannelId: ").append(toIndentedString(contactChannelId)).append("\n");
    sb.append("    contactChannelPermission: ").append(toIndentedString(contactChannelPermission)).append("\n");
    sb.append("    contactChannelSubType: ").append(toIndentedString(contactChannelSubType)).append("\n");
    sb.append("    contactChannelType: ").append(toIndentedString(contactChannelType)).append("\n");
    sb.append("    contactChannelValue: ").append(toIndentedString(contactChannelValue)).append("\n");
    sb.append("    contentPermission: ").append(toIndentedString(contentPermission)).append("\n");
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


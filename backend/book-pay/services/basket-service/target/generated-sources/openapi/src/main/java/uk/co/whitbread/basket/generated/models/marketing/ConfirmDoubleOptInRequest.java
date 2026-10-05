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
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ConfirmDoubleOptInRequest
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:09.701357+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ConfirmDoubleOptInRequest {

  @Valid
  private List<String> brandCodes = new ArrayList<>();

  /**
   * Gets or Sets contactSubType
   */
  public enum ContactSubTypeEnum {
    MOBILE("mobile"),
    
    LANDLINE("landline");

    private String value;

    ContactSubTypeEnum(String value) {
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
    public static ContactSubTypeEnum fromValue(String value) {
      for (ContactSubTypeEnum b : ContactSubTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable ContactSubTypeEnum contactSubType;

  /**
   * Gets or Sets contactType
   */
  public enum ContactTypeEnum {
    EMAIL("email"),
    
    PHONE("phone");

    private String value;

    ContactTypeEnum(String value) {
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
    public static ContactTypeEnum fromValue(String value) {
      for (ContactTypeEnum b : ContactTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private ContactTypeEnum contactType;

  private @Nullable String customerId;

  public ConfirmDoubleOptInRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ConfirmDoubleOptInRequest(List<String> brandCodes, ContactTypeEnum contactType) {
    this.brandCodes = brandCodes;
    this.contactType = contactType;
  }

  public ConfirmDoubleOptInRequest brandCodes(List<String> brandCodes) {
    this.brandCodes = brandCodes;
    return this;
  }

  public ConfirmDoubleOptInRequest addBrandCodesItem(String brandCodesItem) {
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

  public ConfirmDoubleOptInRequest contactSubType(ContactSubTypeEnum contactSubType) {
    this.contactSubType = contactSubType;
    return this;
  }

  /**
   * Get contactSubType
   * @return contactSubType
   */
  
  @Schema(name = "contactSubType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("contactSubType")
  public ContactSubTypeEnum getContactSubType() {
    return contactSubType;
  }

  public void setContactSubType(ContactSubTypeEnum contactSubType) {
    this.contactSubType = contactSubType;
  }

  public ConfirmDoubleOptInRequest contactType(ContactTypeEnum contactType) {
    this.contactType = contactType;
    return this;
  }

  /**
   * Get contactType
   * @return contactType
   */
  @NotNull 
  @Schema(name = "contactType", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("contactType")
  public ContactTypeEnum getContactType() {
    return contactType;
  }

  public void setContactType(ContactTypeEnum contactType) {
    this.contactType = contactType;
  }

  public ConfirmDoubleOptInRequest customerId(String customerId) {
    this.customerId = customerId;
    return this;
  }

  /**
   * Get customerId
   * @return customerId
   */
  
  @Schema(name = "customerId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customerId")
  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConfirmDoubleOptInRequest confirmDoubleOptInRequest = (ConfirmDoubleOptInRequest) o;
    return Objects.equals(this.brandCodes, confirmDoubleOptInRequest.brandCodes) &&
        Objects.equals(this.contactSubType, confirmDoubleOptInRequest.contactSubType) &&
        Objects.equals(this.contactType, confirmDoubleOptInRequest.contactType) &&
        Objects.equals(this.customerId, confirmDoubleOptInRequest.customerId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(brandCodes, contactSubType, contactType, customerId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConfirmDoubleOptInRequest {\n");
    sb.append("    brandCodes: ").append(toIndentedString(brandCodes)).append("\n");
    sb.append("    contactSubType: ").append(toIndentedString(contactSubType)).append("\n");
    sb.append("    contactType: ").append(toIndentedString(contactType)).append("\n");
    sb.append("    customerId: ").append(toIndentedString(customerId)).append("\n");
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


package uk.co.whitbread.basket.generated.models.marketing;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Subscription
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:09.701357+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Subscription {

  /**
   * Gets or Sets brandCode
   */
  public enum BrandCodeEnum {
    BARB("BARB"),
    
    BEEF("BEEF"),
    
    BREW("BREW"),
    
    COOK("COOK"),
    
    PGER("PGER"),
    
    PHUB("PHUB"),
    
    PINN("PINN"),
    
    PZIP("PZIP"),
    
    TABL("TABL"),
    
    TAYB("TAYB"),
    
    WINN("WINN");

    private String value;

    BrandCodeEnum(String value) {
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
    public static BrandCodeEnum fromValue(String value) {
      for (BrandCodeEnum b : BrandCodeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private BrandCodeEnum brandCode;

  /**
   * Gets or Sets contactType
   */
  public enum ContactTypeEnum {
    EMAIL("Email"),
    
    TELEPHONE("Telephone");

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

  private String contactValue;

  private Boolean subscribe;

  public Subscription() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Subscription(BrandCodeEnum brandCode, ContactTypeEnum contactType, String contactValue, Boolean subscribe) {
    this.brandCode = brandCode;
    this.contactType = contactType;
    this.contactValue = contactValue;
    this.subscribe = subscribe;
  }

  public Subscription brandCode(BrandCodeEnum brandCode) {
    this.brandCode = brandCode;
    return this;
  }

  /**
   * Get brandCode
   * @return brandCode
   */
  @NotNull 
  @Schema(name = "brandCode", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("brandCode")
  public BrandCodeEnum getBrandCode() {
    return brandCode;
  }

  public void setBrandCode(BrandCodeEnum brandCode) {
    this.brandCode = brandCode;
  }

  public Subscription contactType(ContactTypeEnum contactType) {
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

  public Subscription contactValue(String contactValue) {
    this.contactValue = contactValue;
    return this;
  }

  /**
   * Get contactValue
   * @return contactValue
   */
  @NotNull 
  @Schema(name = "contactValue", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("contactValue")
  public String getContactValue() {
    return contactValue;
  }

  public void setContactValue(String contactValue) {
    this.contactValue = contactValue;
  }

  public Subscription subscribe(Boolean subscribe) {
    this.subscribe = subscribe;
    return this;
  }

  /**
   * Get subscribe
   * @return subscribe
   */
  @NotNull 
  @Schema(name = "subscribe", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("subscribe")
  public Boolean getSubscribe() {
    return subscribe;
  }

  public void setSubscribe(Boolean subscribe) {
    this.subscribe = subscribe;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Subscription subscription = (Subscription) o;
    return Objects.equals(this.brandCode, subscription.brandCode) &&
        Objects.equals(this.contactType, subscription.contactType) &&
        Objects.equals(this.contactValue, subscription.contactValue) &&
        Objects.equals(this.subscribe, subscription.subscribe);
  }

  @Override
  public int hashCode() {
    return Objects.hash(brandCode, contactType, contactValue, subscribe);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Subscription {\n");
    sb.append("    brandCode: ").append(toIndentedString(brandCode)).append("\n");
    sb.append("    contactType: ").append(toIndentedString(contactType)).append("\n");
    sb.append("    contactValue: ").append(toIndentedString(contactValue)).append("\n");
    sb.append("    subscribe: ").append(toIndentedString(subscribe)).append("\n");
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


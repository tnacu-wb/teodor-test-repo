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
import uk.co.whitbread.basket.generated.models.marketing.Customer;
import uk.co.whitbread.basket.generated.models.marketing.SourceDetails;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UpdatePreferencesRequest
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:09.701357+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdatePreferencesRequest {

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

  private Customer customer;

  private @Nullable Boolean doubleOptIn;

  private @Nullable Boolean optIn;

  private @Nullable Boolean secondPartyOptIn;

  private @Nullable SourceDetails sourceDetails;

  private @Nullable Boolean thirdPartyVendorsOptIn;

  public UpdatePreferencesRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdatePreferencesRequest(List<String> brandCodes, Customer customer) {
    this.brandCodes = brandCodes;
    this.customer = customer;
  }

  public UpdatePreferencesRequest brandCodes(List<String> brandCodes) {
    this.brandCodes = brandCodes;
    return this;
  }

  public UpdatePreferencesRequest addBrandCodesItem(String brandCodesItem) {
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

  public UpdatePreferencesRequest contactSubType(ContactSubTypeEnum contactSubType) {
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

  public UpdatePreferencesRequest customer(Customer customer) {
    this.customer = customer;
    return this;
  }

  /**
   * Get customer
   * @return customer
   */
  @NotNull @Valid 
  @Schema(name = "customer", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("customer")
  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public UpdatePreferencesRequest doubleOptIn(Boolean doubleOptIn) {
    this.doubleOptIn = doubleOptIn;
    return this;
  }

  /**
   * Get doubleOptIn
   * @return doubleOptIn
   */
  
  @Schema(name = "doubleOptIn", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("doubleOptIn")
  public Boolean getDoubleOptIn() {
    return doubleOptIn;
  }

  public void setDoubleOptIn(Boolean doubleOptIn) {
    this.doubleOptIn = doubleOptIn;
  }

  public UpdatePreferencesRequest optIn(Boolean optIn) {
    this.optIn = optIn;
    return this;
  }

  /**
   * Get optIn
   * @return optIn
   */
  
  @Schema(name = "optIn", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("optIn")
  public Boolean getOptIn() {
    return optIn;
  }

  public void setOptIn(Boolean optIn) {
    this.optIn = optIn;
  }

  public UpdatePreferencesRequest secondPartyOptIn(Boolean secondPartyOptIn) {
    this.secondPartyOptIn = secondPartyOptIn;
    return this;
  }

  /**
   * Get secondPartyOptIn
   * @return secondPartyOptIn
   */
  
  @Schema(name = "secondPartyOptIn", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("secondPartyOptIn")
  public Boolean getSecondPartyOptIn() {
    return secondPartyOptIn;
  }

  public void setSecondPartyOptIn(Boolean secondPartyOptIn) {
    this.secondPartyOptIn = secondPartyOptIn;
  }

  public UpdatePreferencesRequest sourceDetails(SourceDetails sourceDetails) {
    this.sourceDetails = sourceDetails;
    return this;
  }

  /**
   * Get sourceDetails
   * @return sourceDetails
   */
  @Valid 
  @Schema(name = "sourceDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sourceDetails")
  public SourceDetails getSourceDetails() {
    return sourceDetails;
  }

  public void setSourceDetails(SourceDetails sourceDetails) {
    this.sourceDetails = sourceDetails;
  }

  public UpdatePreferencesRequest thirdPartyVendorsOptIn(Boolean thirdPartyVendorsOptIn) {
    this.thirdPartyVendorsOptIn = thirdPartyVendorsOptIn;
    return this;
  }

  /**
   * Get thirdPartyVendorsOptIn
   * @return thirdPartyVendorsOptIn
   */
  
  @Schema(name = "thirdPartyVendorsOptIn", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("thirdPartyVendorsOptIn")
  public Boolean getThirdPartyVendorsOptIn() {
    return thirdPartyVendorsOptIn;
  }

  public void setThirdPartyVendorsOptIn(Boolean thirdPartyVendorsOptIn) {
    this.thirdPartyVendorsOptIn = thirdPartyVendorsOptIn;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdatePreferencesRequest updatePreferencesRequest = (UpdatePreferencesRequest) o;
    return Objects.equals(this.brandCodes, updatePreferencesRequest.brandCodes) &&
        Objects.equals(this.contactSubType, updatePreferencesRequest.contactSubType) &&
        Objects.equals(this.customer, updatePreferencesRequest.customer) &&
        Objects.equals(this.doubleOptIn, updatePreferencesRequest.doubleOptIn) &&
        Objects.equals(this.optIn, updatePreferencesRequest.optIn) &&
        Objects.equals(this.secondPartyOptIn, updatePreferencesRequest.secondPartyOptIn) &&
        Objects.equals(this.sourceDetails, updatePreferencesRequest.sourceDetails) &&
        Objects.equals(this.thirdPartyVendorsOptIn, updatePreferencesRequest.thirdPartyVendorsOptIn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(brandCodes, contactSubType, customer, doubleOptIn, optIn, secondPartyOptIn, sourceDetails, thirdPartyVendorsOptIn);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdatePreferencesRequest {\n");
    sb.append("    brandCodes: ").append(toIndentedString(brandCodes)).append("\n");
    sb.append("    contactSubType: ").append(toIndentedString(contactSubType)).append("\n");
    sb.append("    customer: ").append(toIndentedString(customer)).append("\n");
    sb.append("    doubleOptIn: ").append(toIndentedString(doubleOptIn)).append("\n");
    sb.append("    optIn: ").append(toIndentedString(optIn)).append("\n");
    sb.append("    secondPartyOptIn: ").append(toIndentedString(secondPartyOptIn)).append("\n");
    sb.append("    sourceDetails: ").append(toIndentedString(sourceDetails)).append("\n");
    sb.append("    thirdPartyVendorsOptIn: ").append(toIndentedString(thirdPartyVendorsOptIn)).append("\n");
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


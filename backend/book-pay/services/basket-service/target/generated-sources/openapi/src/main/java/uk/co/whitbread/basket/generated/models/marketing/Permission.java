package uk.co.whitbread.basket.generated.models.marketing;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Permission
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:09.701357+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Permission {

  private @Nullable Boolean _2ndOptIn;

  private @Nullable Boolean _2ndOptInReq;

  private @Nullable String brand;

  private @Nullable String brandCode;

  private @Nullable Boolean optIn;

  private @Nullable Boolean secondPartyOptIn;

  private @Nullable Boolean thirdPartyVendorsOptIn;

  public Permission _2ndOptIn(Boolean _2ndOptIn) {
    this._2ndOptIn = _2ndOptIn;
    return this;
  }

  /**
   * Get _2ndOptIn
   * @return _2ndOptIn
   */
  
  @Schema(name = "2ndOptIn", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("2ndOptIn")
  public Boolean get2ndOptIn() {
    return _2ndOptIn;
  }

  public void set2ndOptIn(Boolean _2ndOptIn) {
    this._2ndOptIn = _2ndOptIn;
  }

  public Permission _2ndOptInReq(Boolean _2ndOptInReq) {
    this._2ndOptInReq = _2ndOptInReq;
    return this;
  }

  /**
   * Get _2ndOptInReq
   * @return _2ndOptInReq
   */
  
  @Schema(name = "2ndOptInReq", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("2ndOptInReq")
  public Boolean get2ndOptInReq() {
    return _2ndOptInReq;
  }

  public void set2ndOptInReq(Boolean _2ndOptInReq) {
    this._2ndOptInReq = _2ndOptInReq;
  }

  public Permission brand(String brand) {
    this.brand = brand;
    return this;
  }

  /**
   * Get brand
   * @return brand
   */
  
  @Schema(name = "brand", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("brand")
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public Permission brandCode(String brandCode) {
    this.brandCode = brandCode;
    return this;
  }

  /**
   * Get brandCode
   * @return brandCode
   */
  
  @Schema(name = "brandCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("brandCode")
  public String getBrandCode() {
    return brandCode;
  }

  public void setBrandCode(String brandCode) {
    this.brandCode = brandCode;
  }

  public Permission optIn(Boolean optIn) {
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

  public Permission secondPartyOptIn(Boolean secondPartyOptIn) {
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

  public Permission thirdPartyVendorsOptIn(Boolean thirdPartyVendorsOptIn) {
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
    Permission permission = (Permission) o;
    return Objects.equals(this._2ndOptIn, permission._2ndOptIn) &&
        Objects.equals(this._2ndOptInReq, permission._2ndOptInReq) &&
        Objects.equals(this.brand, permission.brand) &&
        Objects.equals(this.brandCode, permission.brandCode) &&
        Objects.equals(this.optIn, permission.optIn) &&
        Objects.equals(this.secondPartyOptIn, permission.secondPartyOptIn) &&
        Objects.equals(this.thirdPartyVendorsOptIn, permission.thirdPartyVendorsOptIn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_2ndOptIn, _2ndOptInReq, brand, brandCode, optIn, secondPartyOptIn, thirdPartyVendorsOptIn);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Permission {\n");
    sb.append("    _2ndOptIn: ").append(toIndentedString(_2ndOptIn)).append("\n");
    sb.append("    _2ndOptInReq: ").append(toIndentedString(_2ndOptInReq)).append("\n");
    sb.append("    brand: ").append(toIndentedString(brand)).append("\n");
    sb.append("    brandCode: ").append(toIndentedString(brandCode)).append("\n");
    sb.append("    optIn: ").append(toIndentedString(optIn)).append("\n");
    sb.append("    secondPartyOptIn: ").append(toIndentedString(secondPartyOptIn)).append("\n");
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


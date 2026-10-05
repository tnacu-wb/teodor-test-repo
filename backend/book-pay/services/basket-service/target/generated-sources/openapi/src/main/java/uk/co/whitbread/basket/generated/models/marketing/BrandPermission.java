package uk.co.whitbread.basket.generated.models.marketing;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.marketing.ContentPermissionData;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * BrandPermission
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:09.701357+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BrandPermission {

  private @Nullable Boolean _2ndOptIn;

  private @Nullable Boolean _2ndOptInReq;

  private @Nullable String brand;

  private @Nullable String brandCode;

  private @Nullable ContentPermissionData contentPermission;

  private @Nullable String lastModifiedBy;

  private @Nullable String lastOptInDate;

  private @Nullable String lastOptOutDate;

  private @Nullable String lastSourceBusinessKey;

  private @Nullable String lastSourceCustomerId;

  private @Nullable Boolean optIn;

  @Valid
  private List<String> sharedBy = new ArrayList<>();

  public BrandPermission _2ndOptIn(Boolean _2ndOptIn) {
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

  public BrandPermission _2ndOptInReq(Boolean _2ndOptInReq) {
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

  public BrandPermission brand(String brand) {
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

  public BrandPermission brandCode(String brandCode) {
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

  public BrandPermission contentPermission(ContentPermissionData contentPermission) {
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
  public ContentPermissionData getContentPermission() {
    return contentPermission;
  }

  public void setContentPermission(ContentPermissionData contentPermission) {
    this.contentPermission = contentPermission;
  }

  public BrandPermission lastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
    return this;
  }

  /**
   * Get lastModifiedBy
   * @return lastModifiedBy
   */
  
  @Schema(name = "lastModifiedBy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastModifiedBy")
  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public BrandPermission lastOptInDate(String lastOptInDate) {
    this.lastOptInDate = lastOptInDate;
    return this;
  }

  /**
   * Get lastOptInDate
   * @return lastOptInDate
   */
  
  @Schema(name = "lastOptInDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastOptInDate")
  public String getLastOptInDate() {
    return lastOptInDate;
  }

  public void setLastOptInDate(String lastOptInDate) {
    this.lastOptInDate = lastOptInDate;
  }

  public BrandPermission lastOptOutDate(String lastOptOutDate) {
    this.lastOptOutDate = lastOptOutDate;
    return this;
  }

  /**
   * Get lastOptOutDate
   * @return lastOptOutDate
   */
  
  @Schema(name = "lastOptOutDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastOptOutDate")
  public String getLastOptOutDate() {
    return lastOptOutDate;
  }

  public void setLastOptOutDate(String lastOptOutDate) {
    this.lastOptOutDate = lastOptOutDate;
  }

  public BrandPermission lastSourceBusinessKey(String lastSourceBusinessKey) {
    this.lastSourceBusinessKey = lastSourceBusinessKey;
    return this;
  }

  /**
   * Get lastSourceBusinessKey
   * @return lastSourceBusinessKey
   */
  
  @Schema(name = "lastSourceBusinessKey", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastSourceBusinessKey")
  public String getLastSourceBusinessKey() {
    return lastSourceBusinessKey;
  }

  public void setLastSourceBusinessKey(String lastSourceBusinessKey) {
    this.lastSourceBusinessKey = lastSourceBusinessKey;
  }

  public BrandPermission lastSourceCustomerId(String lastSourceCustomerId) {
    this.lastSourceCustomerId = lastSourceCustomerId;
    return this;
  }

  /**
   * Get lastSourceCustomerId
   * @return lastSourceCustomerId
   */
  
  @Schema(name = "lastSourceCustomerId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastSourceCustomerId")
  public String getLastSourceCustomerId() {
    return lastSourceCustomerId;
  }

  public void setLastSourceCustomerId(String lastSourceCustomerId) {
    this.lastSourceCustomerId = lastSourceCustomerId;
  }

  public BrandPermission optIn(Boolean optIn) {
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

  public BrandPermission sharedBy(List<String> sharedBy) {
    this.sharedBy = sharedBy;
    return this;
  }

  public BrandPermission addSharedByItem(String sharedByItem) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BrandPermission brandPermission = (BrandPermission) o;
    return Objects.equals(this._2ndOptIn, brandPermission._2ndOptIn) &&
        Objects.equals(this._2ndOptInReq, brandPermission._2ndOptInReq) &&
        Objects.equals(this.brand, brandPermission.brand) &&
        Objects.equals(this.brandCode, brandPermission.brandCode) &&
        Objects.equals(this.contentPermission, brandPermission.contentPermission) &&
        Objects.equals(this.lastModifiedBy, brandPermission.lastModifiedBy) &&
        Objects.equals(this.lastOptInDate, brandPermission.lastOptInDate) &&
        Objects.equals(this.lastOptOutDate, brandPermission.lastOptOutDate) &&
        Objects.equals(this.lastSourceBusinessKey, brandPermission.lastSourceBusinessKey) &&
        Objects.equals(this.lastSourceCustomerId, brandPermission.lastSourceCustomerId) &&
        Objects.equals(this.optIn, brandPermission.optIn) &&
        Objects.equals(this.sharedBy, brandPermission.sharedBy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_2ndOptIn, _2ndOptInReq, brand, brandCode, contentPermission, lastModifiedBy, lastOptInDate, lastOptOutDate, lastSourceBusinessKey, lastSourceCustomerId, optIn, sharedBy);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BrandPermission {\n");
    sb.append("    _2ndOptIn: ").append(toIndentedString(_2ndOptIn)).append("\n");
    sb.append("    _2ndOptInReq: ").append(toIndentedString(_2ndOptInReq)).append("\n");
    sb.append("    brand: ").append(toIndentedString(brand)).append("\n");
    sb.append("    brandCode: ").append(toIndentedString(brandCode)).append("\n");
    sb.append("    contentPermission: ").append(toIndentedString(contentPermission)).append("\n");
    sb.append("    lastModifiedBy: ").append(toIndentedString(lastModifiedBy)).append("\n");
    sb.append("    lastOptInDate: ").append(toIndentedString(lastOptInDate)).append("\n");
    sb.append("    lastOptOutDate: ").append(toIndentedString(lastOptOutDate)).append("\n");
    sb.append("    lastSourceBusinessKey: ").append(toIndentedString(lastSourceBusinessKey)).append("\n");
    sb.append("    lastSourceCustomerId: ").append(toIndentedString(lastSourceCustomerId)).append("\n");
    sb.append("    optIn: ").append(toIndentedString(optIn)).append("\n");
    sb.append("    sharedBy: ").append(toIndentedString(sharedBy)).append("\n");
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


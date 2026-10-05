package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.BusinessAllowance;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * BusinessItems
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BusinessItems {

  @Valid
  private List<@Valid BusinessAllowance> businessAllowances = new ArrayList<>();

  private String businessNotes;

  private @Nullable String customReferenceNumber;

  private @Nullable String purchaseOrderNumber;

  public BusinessItems() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BusinessItems(List<@Valid BusinessAllowance> businessAllowances, String businessNotes) {
    this.businessAllowances = businessAllowances;
    this.businessNotes = businessNotes;
  }

  public BusinessItems businessAllowances(List<@Valid BusinessAllowance> businessAllowances) {
    this.businessAllowances = businessAllowances;
    return this;
  }

  public BusinessItems addBusinessAllowancesItem(BusinessAllowance businessAllowancesItem) {
    if (this.businessAllowances == null) {
      this.businessAllowances = new ArrayList<>();
    }
    this.businessAllowances.add(businessAllowancesItem);
    return this;
  }

  /**
   * Get businessAllowances
   * @return businessAllowances
   */
  @NotNull @Valid 
  @Schema(name = "businessAllowances", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("businessAllowances")
  public List<@Valid BusinessAllowance> getBusinessAllowances() {
    return businessAllowances;
  }

  public void setBusinessAllowances(List<@Valid BusinessAllowance> businessAllowances) {
    this.businessAllowances = businessAllowances;
  }

  public BusinessItems businessNotes(String businessNotes) {
    this.businessNotes = businessNotes;
    return this;
  }

  /**
   * Get businessNotes
   * @return businessNotes
   */
  @NotNull 
  @Schema(name = "businessNotes", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("businessNotes")
  public String getBusinessNotes() {
    return businessNotes;
  }

  public void setBusinessNotes(String businessNotes) {
    this.businessNotes = businessNotes;
  }

  public BusinessItems customReferenceNumber(String customReferenceNumber) {
    this.customReferenceNumber = customReferenceNumber;
    return this;
  }

  /**
   * Get customReferenceNumber
   * @return customReferenceNumber
   */
  
  @Schema(name = "customReferenceNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customReferenceNumber")
  public String getCustomReferenceNumber() {
    return customReferenceNumber;
  }

  public void setCustomReferenceNumber(String customReferenceNumber) {
    this.customReferenceNumber = customReferenceNumber;
  }

  public BusinessItems purchaseOrderNumber(String purchaseOrderNumber) {
    this.purchaseOrderNumber = purchaseOrderNumber;
    return this;
  }

  /**
   * Get purchaseOrderNumber
   * @return purchaseOrderNumber
   */
  
  @Schema(name = "purchaseOrderNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("purchaseOrderNumber")
  public String getPurchaseOrderNumber() {
    return purchaseOrderNumber;
  }

  public void setPurchaseOrderNumber(String purchaseOrderNumber) {
    this.purchaseOrderNumber = purchaseOrderNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BusinessItems businessItems = (BusinessItems) o;
    return Objects.equals(this.businessAllowances, businessItems.businessAllowances) &&
        Objects.equals(this.businessNotes, businessItems.businessNotes) &&
        Objects.equals(this.customReferenceNumber, businessItems.customReferenceNumber) &&
        Objects.equals(this.purchaseOrderNumber, businessItems.purchaseOrderNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(businessAllowances, businessNotes, customReferenceNumber, purchaseOrderNumber);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BusinessItems {\n");
    sb.append("    businessAllowances: ").append(toIndentedString(businessAllowances)).append("\n");
    sb.append("    businessNotes: ").append(toIndentedString(businessNotes)).append("\n");
    sb.append("    customReferenceNumber: ").append(toIndentedString(customReferenceNumber)).append("\n");
    sb.append("    purchaseOrderNumber: ").append(toIndentedString(purchaseOrderNumber)).append("\n");
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


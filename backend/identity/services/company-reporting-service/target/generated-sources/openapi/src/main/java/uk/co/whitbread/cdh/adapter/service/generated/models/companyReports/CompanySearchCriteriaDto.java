package uk.co.whitbread.cdh.adapter.service.generated.models.companyReports;

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
 * CompanySearchCriteriaDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:17.912171+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CompanySearchCriteriaDto {

  private String accessContext;

  private String accessedBy;

  private @Nullable String addressLine1;

  private @Nullable String addressLine2;

  private @Nullable String addressLine3;

  private @Nullable String addressLine4;

  private @Nullable String addressLine5;

  private @Nullable String cellCode;

  private @Nullable String companyName;

  private @Nullable String companyType;

  private @Nullable String countryCode;

  private @Nullable Integer globalCompanyId;

  private @Nullable Integer pageNumber;

  private @Nullable Integer pageSize;

  private @Nullable String postCode;

  private @Nullable String sortBy;

  private @Nullable String sortDirection;

  public CompanySearchCriteriaDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CompanySearchCriteriaDto(String accessContext, String accessedBy) {
    this.accessContext = accessContext;
    this.accessedBy = accessedBy;
  }

  public CompanySearchCriteriaDto accessContext(String accessContext) {
    this.accessContext = accessContext;
    return this;
  }

  /**
   * Get accessContext
   * @return accessContext
   */
  @NotNull 
  @Schema(name = "accessContext", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("accessContext")
  public String getAccessContext() {
    return accessContext;
  }

  public void setAccessContext(String accessContext) {
    this.accessContext = accessContext;
  }

  public CompanySearchCriteriaDto accessedBy(String accessedBy) {
    this.accessedBy = accessedBy;
    return this;
  }

  /**
   * Get accessedBy
   * @return accessedBy
   */
  @NotNull 
  @Schema(name = "accessedBy", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("accessedBy")
  public String getAccessedBy() {
    return accessedBy;
  }

  public void setAccessedBy(String accessedBy) {
    this.accessedBy = accessedBy;
  }

  public CompanySearchCriteriaDto addressLine1(String addressLine1) {
    this.addressLine1 = addressLine1;
    return this;
  }

  /**
   * Get addressLine1
   * @return addressLine1
   */
  
  @Schema(name = "addressLine1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addressLine1")
  public String getAddressLine1() {
    return addressLine1;
  }

  public void setAddressLine1(String addressLine1) {
    this.addressLine1 = addressLine1;
  }

  public CompanySearchCriteriaDto addressLine2(String addressLine2) {
    this.addressLine2 = addressLine2;
    return this;
  }

  /**
   * Get addressLine2
   * @return addressLine2
   */
  
  @Schema(name = "addressLine2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addressLine2")
  public String getAddressLine2() {
    return addressLine2;
  }

  public void setAddressLine2(String addressLine2) {
    this.addressLine2 = addressLine2;
  }

  public CompanySearchCriteriaDto addressLine3(String addressLine3) {
    this.addressLine3 = addressLine3;
    return this;
  }

  /**
   * Get addressLine3
   * @return addressLine3
   */
  
  @Schema(name = "addressLine3", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addressLine3")
  public String getAddressLine3() {
    return addressLine3;
  }

  public void setAddressLine3(String addressLine3) {
    this.addressLine3 = addressLine3;
  }

  public CompanySearchCriteriaDto addressLine4(String addressLine4) {
    this.addressLine4 = addressLine4;
    return this;
  }

  /**
   * Get addressLine4
   * @return addressLine4
   */
  
  @Schema(name = "addressLine4", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addressLine4")
  public String getAddressLine4() {
    return addressLine4;
  }

  public void setAddressLine4(String addressLine4) {
    this.addressLine4 = addressLine4;
  }

  public CompanySearchCriteriaDto addressLine5(String addressLine5) {
    this.addressLine5 = addressLine5;
    return this;
  }

  /**
   * Get addressLine5
   * @return addressLine5
   */
  
  @Schema(name = "addressLine5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addressLine5")
  public String getAddressLine5() {
    return addressLine5;
  }

  public void setAddressLine5(String addressLine5) {
    this.addressLine5 = addressLine5;
  }

  public CompanySearchCriteriaDto cellCode(String cellCode) {
    this.cellCode = cellCode;
    return this;
  }

  /**
   * Get cellCode
   * @return cellCode
   */
  
  @Schema(name = "cellCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cellCode")
  public String getCellCode() {
    return cellCode;
  }

  public void setCellCode(String cellCode) {
    this.cellCode = cellCode;
  }

  public CompanySearchCriteriaDto companyName(String companyName) {
    this.companyName = companyName;
    return this;
  }

  /**
   * Get companyName
   * @return companyName
   */
  
  @Schema(name = "companyName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyName")
  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public CompanySearchCriteriaDto companyType(String companyType) {
    this.companyType = companyType;
    return this;
  }

  /**
   * Get companyType
   * @return companyType
   */
  
  @Schema(name = "companyType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyType")
  public String getCompanyType() {
    return companyType;
  }

  public void setCompanyType(String companyType) {
    this.companyType = companyType;
  }

  public CompanySearchCriteriaDto countryCode(String countryCode) {
    this.countryCode = countryCode;
    return this;
  }

  /**
   * Get countryCode
   * @return countryCode
   */
  
  @Schema(name = "countryCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("countryCode")
  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public CompanySearchCriteriaDto globalCompanyId(Integer globalCompanyId) {
    this.globalCompanyId = globalCompanyId;
    return this;
  }

  /**
   * Get globalCompanyId
   * @return globalCompanyId
   */
  
  @Schema(name = "globalCompanyId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("globalCompanyId")
  public Integer getGlobalCompanyId() {
    return globalCompanyId;
  }

  public void setGlobalCompanyId(Integer globalCompanyId) {
    this.globalCompanyId = globalCompanyId;
  }

  public CompanySearchCriteriaDto pageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
    return this;
  }

  /**
   * Get pageNumber
   * @return pageNumber
   */
  
  @Schema(name = "pageNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageNumber")
  public Integer getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
  }

  public CompanySearchCriteriaDto pageSize(Integer pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  /**
   * Get pageSize
   * @return pageSize
   */
  
  @Schema(name = "pageSize", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageSize")
  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public CompanySearchCriteriaDto postCode(String postCode) {
    this.postCode = postCode;
    return this;
  }

  /**
   * Get postCode
   * @return postCode
   */
  
  @Schema(name = "postCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("postCode")
  public String getPostCode() {
    return postCode;
  }

  public void setPostCode(String postCode) {
    this.postCode = postCode;
  }

  public CompanySearchCriteriaDto sortBy(String sortBy) {
    this.sortBy = sortBy;
    return this;
  }

  /**
   * Get sortBy
   * @return sortBy
   */
  
  @Schema(name = "sortBy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sortBy")
  public String getSortBy() {
    return sortBy;
  }

  public void setSortBy(String sortBy) {
    this.sortBy = sortBy;
  }

  public CompanySearchCriteriaDto sortDirection(String sortDirection) {
    this.sortDirection = sortDirection;
    return this;
  }

  /**
   * Get sortDirection
   * @return sortDirection
   */
  
  @Schema(name = "sortDirection", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sortDirection")
  public String getSortDirection() {
    return sortDirection;
  }

  public void setSortDirection(String sortDirection) {
    this.sortDirection = sortDirection;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompanySearchCriteriaDto companySearchCriteriaDto = (CompanySearchCriteriaDto) o;
    return Objects.equals(this.accessContext, companySearchCriteriaDto.accessContext) &&
        Objects.equals(this.accessedBy, companySearchCriteriaDto.accessedBy) &&
        Objects.equals(this.addressLine1, companySearchCriteriaDto.addressLine1) &&
        Objects.equals(this.addressLine2, companySearchCriteriaDto.addressLine2) &&
        Objects.equals(this.addressLine3, companySearchCriteriaDto.addressLine3) &&
        Objects.equals(this.addressLine4, companySearchCriteriaDto.addressLine4) &&
        Objects.equals(this.addressLine5, companySearchCriteriaDto.addressLine5) &&
        Objects.equals(this.cellCode, companySearchCriteriaDto.cellCode) &&
        Objects.equals(this.companyName, companySearchCriteriaDto.companyName) &&
        Objects.equals(this.companyType, companySearchCriteriaDto.companyType) &&
        Objects.equals(this.countryCode, companySearchCriteriaDto.countryCode) &&
        Objects.equals(this.globalCompanyId, companySearchCriteriaDto.globalCompanyId) &&
        Objects.equals(this.pageNumber, companySearchCriteriaDto.pageNumber) &&
        Objects.equals(this.pageSize, companySearchCriteriaDto.pageSize) &&
        Objects.equals(this.postCode, companySearchCriteriaDto.postCode) &&
        Objects.equals(this.sortBy, companySearchCriteriaDto.sortBy) &&
        Objects.equals(this.sortDirection, companySearchCriteriaDto.sortDirection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessContext, accessedBy, addressLine1, addressLine2, addressLine3, addressLine4, addressLine5, cellCode, companyName, companyType, countryCode, globalCompanyId, pageNumber, pageSize, postCode, sortBy, sortDirection);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CompanySearchCriteriaDto {\n");
    sb.append("    accessContext: ").append(toIndentedString(accessContext)).append("\n");
    sb.append("    accessedBy: ").append(toIndentedString(accessedBy)).append("\n");
    sb.append("    addressLine1: ").append(toIndentedString(addressLine1)).append("\n");
    sb.append("    addressLine2: ").append(toIndentedString(addressLine2)).append("\n");
    sb.append("    addressLine3: ").append(toIndentedString(addressLine3)).append("\n");
    sb.append("    addressLine4: ").append(toIndentedString(addressLine4)).append("\n");
    sb.append("    addressLine5: ").append(toIndentedString(addressLine5)).append("\n");
    sb.append("    cellCode: ").append(toIndentedString(cellCode)).append("\n");
    sb.append("    companyName: ").append(toIndentedString(companyName)).append("\n");
    sb.append("    companyType: ").append(toIndentedString(companyType)).append("\n");
    sb.append("    countryCode: ").append(toIndentedString(countryCode)).append("\n");
    sb.append("    globalCompanyId: ").append(toIndentedString(globalCompanyId)).append("\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    postCode: ").append(toIndentedString(postCode)).append("\n");
    sb.append("    sortBy: ").append(toIndentedString(sortBy)).append("\n");
    sb.append("    sortDirection: ").append(toIndentedString(sortDirection)).append("\n");
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


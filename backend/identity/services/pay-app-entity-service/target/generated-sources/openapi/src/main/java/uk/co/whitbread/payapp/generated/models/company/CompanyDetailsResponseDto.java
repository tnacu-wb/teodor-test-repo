package uk.co.whitbread.payapp.generated.models.company;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.payapp.generated.models.company.CellCodeDto;
import uk.co.whitbread.payapp.generated.models.company.CompanyDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CompanyDetailsResponseDto
 */

@JsonTypeName("CompanyDetailsResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:38.523560+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CompanyDetailsResponseDto {

  private @Nullable Boolean allowCentralCreditCard;

  @Valid
  private List<@Valid CellCodeDto> companyCellCodes = new ArrayList<>();

  private @Nullable Boolean companyLockedForEditing;

  private @Nullable Boolean marketingAllowed;

  private @Nullable CompanyDto requestedCompany;

  private @Nullable Boolean success;

  public CompanyDetailsResponseDto allowCentralCreditCard(Boolean allowCentralCreditCard) {
    this.allowCentralCreditCard = allowCentralCreditCard;
    return this;
  }

  /**
   * Get allowCentralCreditCard
   * @return allowCentralCreditCard
   */
  
  @Schema(name = "allowCentralCreditCard", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("allowCentralCreditCard")
  public Boolean getAllowCentralCreditCard() {
    return allowCentralCreditCard;
  }

  public void setAllowCentralCreditCard(Boolean allowCentralCreditCard) {
    this.allowCentralCreditCard = allowCentralCreditCard;
  }

  public CompanyDetailsResponseDto companyCellCodes(List<@Valid CellCodeDto> companyCellCodes) {
    this.companyCellCodes = companyCellCodes;
    return this;
  }

  public CompanyDetailsResponseDto addCompanyCellCodesItem(CellCodeDto companyCellCodesItem) {
    if (this.companyCellCodes == null) {
      this.companyCellCodes = new ArrayList<>();
    }
    this.companyCellCodes.add(companyCellCodesItem);
    return this;
  }

  /**
   * Get companyCellCodes
   * @return companyCellCodes
   */
  @Valid 
  @Schema(name = "companyCellCodes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyCellCodes")
  public List<@Valid CellCodeDto> getCompanyCellCodes() {
    return companyCellCodes;
  }

  public void setCompanyCellCodes(List<@Valid CellCodeDto> companyCellCodes) {
    this.companyCellCodes = companyCellCodes;
  }

  public CompanyDetailsResponseDto companyLockedForEditing(Boolean companyLockedForEditing) {
    this.companyLockedForEditing = companyLockedForEditing;
    return this;
  }

  /**
   * Get companyLockedForEditing
   * @return companyLockedForEditing
   */
  
  @Schema(name = "companyLockedForEditing", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyLockedForEditing")
  public Boolean getCompanyLockedForEditing() {
    return companyLockedForEditing;
  }

  public void setCompanyLockedForEditing(Boolean companyLockedForEditing) {
    this.companyLockedForEditing = companyLockedForEditing;
  }

  public CompanyDetailsResponseDto marketingAllowed(Boolean marketingAllowed) {
    this.marketingAllowed = marketingAllowed;
    return this;
  }

  /**
   * Get marketingAllowed
   * @return marketingAllowed
   */
  
  @Schema(name = "marketingAllowed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("marketingAllowed")
  public Boolean getMarketingAllowed() {
    return marketingAllowed;
  }

  public void setMarketingAllowed(Boolean marketingAllowed) {
    this.marketingAllowed = marketingAllowed;
  }

  public CompanyDetailsResponseDto requestedCompany(CompanyDto requestedCompany) {
    this.requestedCompany = requestedCompany;
    return this;
  }

  /**
   * Get requestedCompany
   * @return requestedCompany
   */
  @Valid 
  @Schema(name = "requestedCompany", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("requestedCompany")
  public CompanyDto getRequestedCompany() {
    return requestedCompany;
  }

  public void setRequestedCompany(CompanyDto requestedCompany) {
    this.requestedCompany = requestedCompany;
  }

  public CompanyDetailsResponseDto success(Boolean success) {
    this.success = success;
    return this;
  }

  /**
   * Get success
   * @return success
   */
  
  @Schema(name = "success", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("success")
  public Boolean getSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompanyDetailsResponseDto companyDetailsResponse = (CompanyDetailsResponseDto) o;
    return Objects.equals(this.allowCentralCreditCard, companyDetailsResponse.allowCentralCreditCard) &&
        Objects.equals(this.companyCellCodes, companyDetailsResponse.companyCellCodes) &&
        Objects.equals(this.companyLockedForEditing, companyDetailsResponse.companyLockedForEditing) &&
        Objects.equals(this.marketingAllowed, companyDetailsResponse.marketingAllowed) &&
        Objects.equals(this.requestedCompany, companyDetailsResponse.requestedCompany) &&
        Objects.equals(this.success, companyDetailsResponse.success);
  }

  @Override
  public int hashCode() {
    return Objects.hash(allowCentralCreditCard, companyCellCodes, companyLockedForEditing, marketingAllowed, requestedCompany, success);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CompanyDetailsResponseDto {\n");
    sb.append("    allowCentralCreditCard: ").append(toIndentedString(allowCentralCreditCard)).append("\n");
    sb.append("    companyCellCodes: ").append(toIndentedString(companyCellCodes)).append("\n");
    sb.append("    companyLockedForEditing: ").append(toIndentedString(companyLockedForEditing)).append("\n");
    sb.append("    marketingAllowed: ").append(toIndentedString(marketingAllowed)).append("\n");
    sb.append("    requestedCompany: ").append(toIndentedString(requestedCompany)).append("\n");
    sb.append("    success: ").append(toIndentedString(success)).append("\n");
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


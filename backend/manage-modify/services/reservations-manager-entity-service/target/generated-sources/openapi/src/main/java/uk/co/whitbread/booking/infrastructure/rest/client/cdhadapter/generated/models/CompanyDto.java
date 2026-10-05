package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.BookingAlertsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.BookingAllowancesDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.CellCodeDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.CompanyAddressDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.CompanyCellCodeDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.CompanyManagementDetailsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.MainContactDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CompanyDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:45.778333+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CompanyDto {

  private @Nullable Boolean accountToCompany;

  private @Nullable Boolean allowCentralCreditCard;

  private @Nullable String alternateCompanyName;

  private @Nullable BookingAlertsDto bookingAlerts;

  private @Nullable BookingAllowancesDto bookingAllowances;

  @Valid
  private List<@Valid CellCodeDto> cellCodes = new ArrayList<>();

  private @Nullable String companyAccountId;

  private @Nullable CompanyAddressDto companyAddress;

  @Valid
  private List<@Valid CompanyCellCodeDto> companyCellCodes = new ArrayList<>();

  private @Nullable Boolean companyLockedForEditing;

  private @Nullable CompanyManagementDetailsDto companyManagementDetails;

  private @Nullable String companyName;

  private @Nullable String companyType;

  private @Nullable String crmCompanyId;

  private @Nullable Integer globalCompanyId;

  private @Nullable MainContactDto mainContact;

  private @Nullable Boolean managedAccount;

  private @Nullable Boolean manualValidationRequired;

  private @Nullable Boolean manuallyValidated;

  private @Nullable Boolean marketingAllowed;

  private @Nullable Integer numberOfEmployees;

  private @Nullable String status;

  public CompanyDto accountToCompany(Boolean accountToCompany) {
    this.accountToCompany = accountToCompany;
    return this;
  }

  /**
   * Get accountToCompany
   * @return accountToCompany
   */
  
  @Schema(name = "accountToCompany", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accountToCompany")
  public Boolean getAccountToCompany() {
    return accountToCompany;
  }

  public void setAccountToCompany(Boolean accountToCompany) {
    this.accountToCompany = accountToCompany;
  }

  public CompanyDto allowCentralCreditCard(Boolean allowCentralCreditCard) {
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

  public CompanyDto alternateCompanyName(String alternateCompanyName) {
    this.alternateCompanyName = alternateCompanyName;
    return this;
  }

  /**
   * Get alternateCompanyName
   * @return alternateCompanyName
   */
  
  @Schema(name = "alternateCompanyName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("alternateCompanyName")
  public String getAlternateCompanyName() {
    return alternateCompanyName;
  }

  public void setAlternateCompanyName(String alternateCompanyName) {
    this.alternateCompanyName = alternateCompanyName;
  }

  public CompanyDto bookingAlerts(BookingAlertsDto bookingAlerts) {
    this.bookingAlerts = bookingAlerts;
    return this;
  }

  /**
   * Get bookingAlerts
   * @return bookingAlerts
   */
  @Valid 
  @Schema(name = "bookingAlerts", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingAlerts")
  public BookingAlertsDto getBookingAlerts() {
    return bookingAlerts;
  }

  public void setBookingAlerts(BookingAlertsDto bookingAlerts) {
    this.bookingAlerts = bookingAlerts;
  }

  public CompanyDto bookingAllowances(BookingAllowancesDto bookingAllowances) {
    this.bookingAllowances = bookingAllowances;
    return this;
  }

  /**
   * Get bookingAllowances
   * @return bookingAllowances
   */
  @Valid 
  @Schema(name = "bookingAllowances", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingAllowances")
  public BookingAllowancesDto getBookingAllowances() {
    return bookingAllowances;
  }

  public void setBookingAllowances(BookingAllowancesDto bookingAllowances) {
    this.bookingAllowances = bookingAllowances;
  }

  public CompanyDto cellCodes(List<@Valid CellCodeDto> cellCodes) {
    this.cellCodes = cellCodes;
    return this;
  }

  public CompanyDto addCellCodesItem(CellCodeDto cellCodesItem) {
    if (this.cellCodes == null) {
      this.cellCodes = new ArrayList<>();
    }
    this.cellCodes.add(cellCodesItem);
    return this;
  }

  /**
   * Get cellCodes
   * @return cellCodes
   */
  @Valid 
  @Schema(name = "cellCodes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cellCodes")
  public List<@Valid CellCodeDto> getCellCodes() {
    return cellCodes;
  }

  public void setCellCodes(List<@Valid CellCodeDto> cellCodes) {
    this.cellCodes = cellCodes;
  }

  public CompanyDto companyAccountId(String companyAccountId) {
    this.companyAccountId = companyAccountId;
    return this;
  }

  /**
   * Get companyAccountId
   * @return companyAccountId
   */
  
  @Schema(name = "companyAccountId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyAccountId")
  public String getCompanyAccountId() {
    return companyAccountId;
  }

  public void setCompanyAccountId(String companyAccountId) {
    this.companyAccountId = companyAccountId;
  }

  public CompanyDto companyAddress(CompanyAddressDto companyAddress) {
    this.companyAddress = companyAddress;
    return this;
  }

  /**
   * Get companyAddress
   * @return companyAddress
   */
  @Valid 
  @Schema(name = "companyAddress", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyAddress")
  public CompanyAddressDto getCompanyAddress() {
    return companyAddress;
  }

  public void setCompanyAddress(CompanyAddressDto companyAddress) {
    this.companyAddress = companyAddress;
  }

  public CompanyDto companyCellCodes(List<@Valid CompanyCellCodeDto> companyCellCodes) {
    this.companyCellCodes = companyCellCodes;
    return this;
  }

  public CompanyDto addCompanyCellCodesItem(CompanyCellCodeDto companyCellCodesItem) {
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
  public List<@Valid CompanyCellCodeDto> getCompanyCellCodes() {
    return companyCellCodes;
  }

  public void setCompanyCellCodes(List<@Valid CompanyCellCodeDto> companyCellCodes) {
    this.companyCellCodes = companyCellCodes;
  }

  public CompanyDto companyLockedForEditing(Boolean companyLockedForEditing) {
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

  public CompanyDto companyManagementDetails(CompanyManagementDetailsDto companyManagementDetails) {
    this.companyManagementDetails = companyManagementDetails;
    return this;
  }

  /**
   * Get companyManagementDetails
   * @return companyManagementDetails
   */
  @Valid 
  @Schema(name = "companyManagementDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyManagementDetails")
  public CompanyManagementDetailsDto getCompanyManagementDetails() {
    return companyManagementDetails;
  }

  public void setCompanyManagementDetails(CompanyManagementDetailsDto companyManagementDetails) {
    this.companyManagementDetails = companyManagementDetails;
  }

  public CompanyDto companyName(String companyName) {
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

  public CompanyDto companyType(String companyType) {
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

  public CompanyDto crmCompanyId(String crmCompanyId) {
    this.crmCompanyId = crmCompanyId;
    return this;
  }

  /**
   * Get crmCompanyId
   * @return crmCompanyId
   */
  
  @Schema(name = "crmCompanyId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("crmCompanyId")
  public String getCrmCompanyId() {
    return crmCompanyId;
  }

  public void setCrmCompanyId(String crmCompanyId) {
    this.crmCompanyId = crmCompanyId;
  }

  public CompanyDto globalCompanyId(Integer globalCompanyId) {
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

  public CompanyDto mainContact(MainContactDto mainContact) {
    this.mainContact = mainContact;
    return this;
  }

  /**
   * Get mainContact
   * @return mainContact
   */
  @Valid 
  @Schema(name = "mainContact", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mainContact")
  public MainContactDto getMainContact() {
    return mainContact;
  }

  public void setMainContact(MainContactDto mainContact) {
    this.mainContact = mainContact;
  }

  public CompanyDto managedAccount(Boolean managedAccount) {
    this.managedAccount = managedAccount;
    return this;
  }

  /**
   * Get managedAccount
   * @return managedAccount
   */
  
  @Schema(name = "managedAccount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("managedAccount")
  public Boolean getManagedAccount() {
    return managedAccount;
  }

  public void setManagedAccount(Boolean managedAccount) {
    this.managedAccount = managedAccount;
  }

  public CompanyDto manualValidationRequired(Boolean manualValidationRequired) {
    this.manualValidationRequired = manualValidationRequired;
    return this;
  }

  /**
   * Get manualValidationRequired
   * @return manualValidationRequired
   */
  
  @Schema(name = "manualValidationRequired", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("manualValidationRequired")
  public Boolean getManualValidationRequired() {
    return manualValidationRequired;
  }

  public void setManualValidationRequired(Boolean manualValidationRequired) {
    this.manualValidationRequired = manualValidationRequired;
  }

  public CompanyDto manuallyValidated(Boolean manuallyValidated) {
    this.manuallyValidated = manuallyValidated;
    return this;
  }

  /**
   * Get manuallyValidated
   * @return manuallyValidated
   */
  
  @Schema(name = "manuallyValidated", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("manuallyValidated")
  public Boolean getManuallyValidated() {
    return manuallyValidated;
  }

  public void setManuallyValidated(Boolean manuallyValidated) {
    this.manuallyValidated = manuallyValidated;
  }

  public CompanyDto marketingAllowed(Boolean marketingAllowed) {
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

  public CompanyDto numberOfEmployees(Integer numberOfEmployees) {
    this.numberOfEmployees = numberOfEmployees;
    return this;
  }

  /**
   * Get numberOfEmployees
   * @return numberOfEmployees
   */
  
  @Schema(name = "numberOfEmployees", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("numberOfEmployees")
  public Integer getNumberOfEmployees() {
    return numberOfEmployees;
  }

  public void setNumberOfEmployees(Integer numberOfEmployees) {
    this.numberOfEmployees = numberOfEmployees;
  }

  public CompanyDto status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompanyDto companyDto = (CompanyDto) o;
    return Objects.equals(this.accountToCompany, companyDto.accountToCompany) &&
        Objects.equals(this.allowCentralCreditCard, companyDto.allowCentralCreditCard) &&
        Objects.equals(this.alternateCompanyName, companyDto.alternateCompanyName) &&
        Objects.equals(this.bookingAlerts, companyDto.bookingAlerts) &&
        Objects.equals(this.bookingAllowances, companyDto.bookingAllowances) &&
        Objects.equals(this.cellCodes, companyDto.cellCodes) &&
        Objects.equals(this.companyAccountId, companyDto.companyAccountId) &&
        Objects.equals(this.companyAddress, companyDto.companyAddress) &&
        Objects.equals(this.companyCellCodes, companyDto.companyCellCodes) &&
        Objects.equals(this.companyLockedForEditing, companyDto.companyLockedForEditing) &&
        Objects.equals(this.companyManagementDetails, companyDto.companyManagementDetails) &&
        Objects.equals(this.companyName, companyDto.companyName) &&
        Objects.equals(this.companyType, companyDto.companyType) &&
        Objects.equals(this.crmCompanyId, companyDto.crmCompanyId) &&
        Objects.equals(this.globalCompanyId, companyDto.globalCompanyId) &&
        Objects.equals(this.mainContact, companyDto.mainContact) &&
        Objects.equals(this.managedAccount, companyDto.managedAccount) &&
        Objects.equals(this.manualValidationRequired, companyDto.manualValidationRequired) &&
        Objects.equals(this.manuallyValidated, companyDto.manuallyValidated) &&
        Objects.equals(this.marketingAllowed, companyDto.marketingAllowed) &&
        Objects.equals(this.numberOfEmployees, companyDto.numberOfEmployees) &&
        Objects.equals(this.status, companyDto.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountToCompany, allowCentralCreditCard, alternateCompanyName, bookingAlerts, bookingAllowances, cellCodes, companyAccountId, companyAddress, companyCellCodes, companyLockedForEditing, companyManagementDetails, companyName, companyType, crmCompanyId, globalCompanyId, mainContact, managedAccount, manualValidationRequired, manuallyValidated, marketingAllowed, numberOfEmployees, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CompanyDto {\n");
    sb.append("    accountToCompany: ").append(toIndentedString(accountToCompany)).append("\n");
    sb.append("    allowCentralCreditCard: ").append(toIndentedString(allowCentralCreditCard)).append("\n");
    sb.append("    alternateCompanyName: ").append(toIndentedString(alternateCompanyName)).append("\n");
    sb.append("    bookingAlerts: ").append(toIndentedString(bookingAlerts)).append("\n");
    sb.append("    bookingAllowances: ").append(toIndentedString(bookingAllowances)).append("\n");
    sb.append("    cellCodes: ").append(toIndentedString(cellCodes)).append("\n");
    sb.append("    companyAccountId: ").append(toIndentedString(companyAccountId)).append("\n");
    sb.append("    companyAddress: ").append(toIndentedString(companyAddress)).append("\n");
    sb.append("    companyCellCodes: ").append(toIndentedString(companyCellCodes)).append("\n");
    sb.append("    companyLockedForEditing: ").append(toIndentedString(companyLockedForEditing)).append("\n");
    sb.append("    companyManagementDetails: ").append(toIndentedString(companyManagementDetails)).append("\n");
    sb.append("    companyName: ").append(toIndentedString(companyName)).append("\n");
    sb.append("    companyType: ").append(toIndentedString(companyType)).append("\n");
    sb.append("    crmCompanyId: ").append(toIndentedString(crmCompanyId)).append("\n");
    sb.append("    globalCompanyId: ").append(toIndentedString(globalCompanyId)).append("\n");
    sb.append("    mainContact: ").append(toIndentedString(mainContact)).append("\n");
    sb.append("    managedAccount: ").append(toIndentedString(managedAccount)).append("\n");
    sb.append("    manualValidationRequired: ").append(toIndentedString(manualValidationRequired)).append("\n");
    sb.append("    manuallyValidated: ").append(toIndentedString(manuallyValidated)).append("\n");
    sb.append("    marketingAllowed: ").append(toIndentedString(marketingAllowed)).append("\n");
    sb.append("    numberOfEmployees: ").append(toIndentedString(numberOfEmployees)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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


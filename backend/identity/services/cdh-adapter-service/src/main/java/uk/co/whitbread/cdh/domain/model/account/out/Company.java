package uk.co.whitbread.cdh.domain.model.account.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {
  @JsonProperty("CompanyAccountId")
  private String companyAccountId;
  @JsonProperty("GlobalCompanyId")
  private Integer globalCompanyId;
  @JsonProperty("AllowCentralCreditCard")
  private boolean allowCentralCreditCard;
  @JsonProperty("CompanyName")
  private String companyName;
  @JsonProperty("CrmCompanyId")
  private String crmCompanyId;
  @JsonProperty("AlternateCompanyName")
  private String alternateCompanyName;
  @JsonProperty("Status")
  private String status;
  @JsonProperty("CompanyLockedForEditing")
  private boolean companyLockedForEditing;
  @JsonProperty("MarketingAllowed")
  private boolean marketingAllowed;
  @JsonProperty("ManualValidationRequired")
  private boolean manualValidationRequired;
  @JsonProperty("AccountToCompany")
  private boolean accountToCompany;
  @JsonProperty("CompanyType")
  private String companyType;
  @JsonProperty("ManagedAccount")
  private boolean managedAccount;
  @JsonProperty("CompanyAddress")
  private CompanyAddress companyAddress;
  @JsonProperty("ManuallyValidated")
  private boolean manuallyValidated;
  @JsonProperty("MainContact")
  private MainContact mainContact;
  @JsonProperty("NumberOfEmployees")
  private Integer numberOfEmployees;
  @JsonProperty("CellCodes")
  private List<CellCode> cellCodes;
  @JsonProperty("SuppressRates")
  private List<String> suppressRates;
  @JsonProperty("BookingAlerts")
  private BookingAlerts bookingAlerts;
  @JsonProperty("BookingAllowances")
  private BookingAllowances bookingAllowances;
  @JsonProperty("CompanyManagementDetails")
  private CompanyManagementDetails companyManagementDetails;
  @JsonProperty("CompanyCellCodes")
  private List<CompanyCellCode> companyCellCodes;
}

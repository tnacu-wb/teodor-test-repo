package uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
  private String companyAccountId;
  private Integer globalCompanyId;
  private boolean allowCentralCreditCard;
  private String companyName;
  private String crmCompanyId;
  private String alternateCompanyName;
  private String status;
  private boolean companyLockedForEditing;
  private boolean marketingAllowed;
  private boolean manualValidationRequired;
  private boolean accountToCompany;
  private String companyType;
  private boolean managedAccount;
  private CompanyAddressDto companyAddress;
  private boolean manuallyValidated;
  private MainContactDto mainContact;
  private Integer numberOfEmployees;
  private List<CellCodeDto> cellCodes;
  private List<String> suppressRates;
  private BookingAlertsDto bookingAlerts;
  private BookingAllowancesDto bookingAllowances;
  private CompanyManagementDetailsDto companyManagementDetails;
  private List<CompanyCellCodeDto> companyCellCodes;
}

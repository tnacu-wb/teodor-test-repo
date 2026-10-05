package uk.co.whitbread.shared.cdh.model.company;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.co.whitbread.shared.cdh.model.BusinessAddress;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetCompanyResponse {

  @JsonProperty("CompanyAccountId")
  private String companyAccountId;
  @JsonProperty("GlobalCompanyId")
  private long globalCompanyId;
  @JsonProperty("CrmCompanyId")
  private Integer crmCompanyId;
  @JsonProperty("AllowCentralCreditCard")
  private boolean allowCentralCreditCard;
  @JsonProperty("CompanyName")
  private String companyName;
  @JsonProperty("AlternateCompanyName")
  private String alternateCompanyName;
  @JsonProperty("Status")
  private String status;
  @JsonProperty("CompanyLockedForEditing")
  private boolean companyLockedForEditing;
  @JsonProperty("CompanyType")
  private String companyType;
  @JsonProperty("MarketingAllowed")
  private boolean marketingAllowed;
  @JsonProperty("CompanyAddress")
  private BusinessAddress companyAddress;
  @JsonProperty("MainContact")
  private GetEmployeeResponse mainContact;
  @JsonProperty("NumberOfEmployees")
  private int numberOfEmployees;
  @JsonProperty("CellCodes")
  private List<CompanyCellCode> companyCellCodes;
  @JsonProperty("BookingAlerts")
  private BookingAlerts bookingAlerts;
  @JsonProperty("BookingAllowances")
  private BookingAllowances bookingAllowances;
  @JsonProperty("CompanyManagementDetails")
  private CompanyManagementDetails companyManagementDetails;
  @JsonProperty("CompanySector")
  private String companySector;
  @JsonProperty("AverageMonthlyBooking")
  private String averageMonthlyBooking;
  @JsonProperty("NumberOfEmployee")
  private String numberOfEmployee;
  @JsonProperty("SocialMediaHandle")
  private String socialMediaHandle;
  @JsonProperty("UniqueTaxReference")
  private String uniqueTaxReference;
}

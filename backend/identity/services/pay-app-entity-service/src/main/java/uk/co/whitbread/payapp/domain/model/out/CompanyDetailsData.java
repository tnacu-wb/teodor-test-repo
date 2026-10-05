package uk.co.whitbread.payapp.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDetailsData {

  private String vatRegistrationNumber;
  private String estMonthlySpend;
  private String companyType;
  private String charityNumber;
  private String companyRegNum;
  private PartnerDetails partnerDetails;
  private String timeTradingId;
  private Address registrationAddress;
  private Address correspondenceAddress;
  private ContactDetails correspondenceContactInfo;
  private String hotelBrandPolicy;
  private String parentCompanyName;
  private String industrySector;
  private String numberOfEmployees;
  private String companyNameOnCard;

}

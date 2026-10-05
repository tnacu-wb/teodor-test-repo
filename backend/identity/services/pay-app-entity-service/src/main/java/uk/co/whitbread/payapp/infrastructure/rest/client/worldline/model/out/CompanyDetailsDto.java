package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDetailsDto {

  private String companyName;
  private String vatRegistrationNumber;
  private String estMonthlySpend;
  private String companyType;
  private String charityNumber;
  private String companyRegNum;
  private PartnerDetailsDto partnerDetails;
  @JsonProperty("timeTradingID")
  private String timeTradingId;
  private AddressDto registrationAddress;
  private AddressDto correspondenceAddress;
  private ContactDetailsDto correspondenceContactInfo;
  private String hotelBrandPolicy;
  private String parentCompanyName;
  private String industrySector;
  private String numberOfEmployees;
  private String companyNameOnCard;

}

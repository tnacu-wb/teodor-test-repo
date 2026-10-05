package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.validation.ModelValidator;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
public class AppCompanyDetailsDto extends ModelValidator<AppCompanyDetailsDto> {

  @NotBlank
  private String companyName;

  private String vatRegistrationNumber;

  @NotBlank
  private String estMonthlySpend;

  @NotBlank
  private String companyType;

  private String charityNumber;

  private String companyRegNum;

  private PartnerDetailsDto partnerDetails;

  private String timeTradingId;

  private AddressDto registrationAddress;

  private AddressDto correspondenceAddress;

  private ContactInfoDto correspondenceContactInfo;

  private String hotelBrandPolicy;

  private String parentCompanyName;

  private String industrySector;

  private String numberOfEmployees;

  private String companyNameOnCard;

  @SuppressWarnings("java:S107")
  public AppCompanyDetailsDto(String companyName, String vatRegistrationNumber,
      String estMonthlySpend, String companyType, String charityNumber, String companyRegNum,
      PartnerDetailsDto partnerDetails, String timeTradingId, AddressDto registrationAddress,
      AddressDto correspondenceAddress, ContactInfoDto correspondenceContactInfo,
      String hotelBrandPolicy, String parentCompanyName, String industrySector,
      String numberOfEmployees, String companyNameOnCard) {
    this.companyName = companyName;
    this.vatRegistrationNumber = vatRegistrationNumber;
    this.estMonthlySpend = estMonthlySpend;
    this.companyType = companyType;
    this.charityNumber = charityNumber;
    this.companyRegNum = companyRegNum;
    this.partnerDetails = partnerDetails;
    this.timeTradingId = timeTradingId;
    this.registrationAddress = registrationAddress;
    this.correspondenceAddress = correspondenceAddress;
    this.correspondenceContactInfo = correspondenceContactInfo;
    this.hotelBrandPolicy = hotelBrandPolicy;
    this.parentCompanyName = parentCompanyName;
    this.industrySector = industrySector;
    this.numberOfEmployees = numberOfEmployees;
    this.companyNameOnCard = companyNameOnCard;
    this.validate();
  }
}

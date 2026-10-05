package uk.co.whitbread.payapp.domain.model.in;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.payapp.domain.model.validation.DomainValidator;

@Value
@Builder
@EqualsAndHashCode(callSuper = false)
public class AppCompanyDetails extends DomainValidator<AppCompanyDetails> {

  @NotBlank
  String companyName;

  String vatRegistrationNumber;

  @NotBlank
  String estMonthlySpend;

  @NotBlank
  String companyType;

  String charityNumber;

  String companyRegNum;

  PartnerDetails partnerDetails;

  String timeTradingId;

  Address registrationAddress;

  Address correspondenceAddress;

  ContactInfo correspondenceContactInfo;

  String hotelBrandPolicy;

  String parentCompanyName;

  String industrySector;

  String numberOfEmployees;

  String companyNameOnCard;

  @SuppressWarnings("java:S107")
  public AppCompanyDetails(String companyName, String vatRegistrationNumber,
      String estMonthlySpend, String companyType, String charityNumber, String companyRegNum,
      PartnerDetails partnerDetails, String timeTradingId, Address registrationAddress,
      Address correspondenceAddress, ContactInfo correspondenceContactInfo,
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
    this.validateSelf();
  }
}

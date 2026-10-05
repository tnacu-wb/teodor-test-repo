package uk.co.whitbread.ohip.infrastructure.rest.client.profile.mapper;

import static java.util.stream.Collectors.groupingBy;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Company;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyProfileTypeAddresses;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyProfileTypeTelephones;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyProfileTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CountryNameType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.DirectBillingType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileRestrictions;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileStatusType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileSummaryInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileSummaryType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileSummaryTypeFormerName;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.TelephoneInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.TelephoneType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.profile.out.Address;
import uk.co.whitbread.ohip.domain.model.profile.out.CompanyProfile;

@Component
public class OhipProfileTransformer {

  private static final String EMPTY_STRING = "";
  private static final String BUSINESS = "BUSINESS";
  private static final String CORPORATE_ID = "CorporateId";
  private static final String PROFILE_ID = "Profile";
  private static final String ACTIVE = "ACTIVE";

  @Named("extractCompaniesFrom")
  public List<CompanyProfile> extractCompaniesFrom(List<ProfileSummaryInfoType> profileSummaryInfoTypes) {
    return Optional.ofNullable(profileSummaryInfoTypes)
        .orElseGet(Collections::emptyList)
        .stream()
        .map(this::toCompanyProfileFrom)
        .toList();
  }

  @Named("extractCompanyFrom")
  public CompanyProfile extractCompanyFrom(Company company) {
    var profileTypeToIdMapping = Optional.ofNullable(company)
        .map(Company::getCompanyIdList)
        .orElseGet(Collections::emptyList)
        .stream()
        .collect(groupingBy(UniqueIDType::getType));

    var profileMayBe = Optional.ofNullable(company)
        .map(Company::getCompanyDetails);

    var companyMayBe = profileMayBe
        .map(CompanyProfileType::getCompany);

    var companyName = companyMayBe
        .map(CompanyType::getCompanyName)
        .orElse(null);

    var language = companyMayBe
        .map(CompanyType::getLanguage)
        .orElse(Locale.UK.getLanguage());

    var phoneTypeToIdMapping = profileMayBe
        .map(CompanyProfileType::getTelephones)
        .map(CompanyProfileTypeTelephones::getTelephoneInfo)
        .orElseGet(Collections::emptyList)
        .stream()
        .collect(groupingBy(TelephoneInfoType::getType));

    var profileType = profileMayBe
        .map(CompanyProfileType::getProfileType)
        .map(CompanyProfileTypeType::getValue)
        .orElse(null);

    var isProfileActive = profileMayBe
        .map(CompanyProfileType::getStatusCode)
        .map(ProfileStatusType::getValue)
        .map(value -> value.equalsIgnoreCase(ACTIVE))
        .orElse(false);

    var addressTypeToIdMapping = profileMayBe
        .map(CompanyProfileType::getAddresses)
        .map(CompanyProfileTypeAddresses::getAddressInfo)
        .orElseGet(Collections::emptyList)
        .stream()
        .collect(groupingBy(AddressInfoType::getType));

    return CompanyProfile.builder()
        .name(companyName)
        .telephoneNumber(getTelephoneFromProfile(phoneTypeToIdMapping, BUSINESS))
        .profileType(profileType)
        .corpId(getIdFromProfile(profileTypeToIdMapping, CORPORATE_ID))
        .companyId(getIdFromProfile(profileTypeToIdMapping, PROFILE_ID))
        .language(language)
        .active(isProfileActive)
        .address(toCompanyProfileAddress(
            getAddressFromProfile(addressTypeToIdMapping, BUSINESS)))
        .build();
  }

  private CompanyProfile toCompanyProfileFrom(ProfileSummaryInfoType profileSummaryInfoType) {
    var profileTypeToIdMapping = Optional.ofNullable(profileSummaryInfoType)
        .map(ProfileSummaryInfoType::getProfileIdList)
        .orElseGet(Collections::emptyList)
        .stream()
        .collect(groupingBy(UniqueIDType::getType));

    var profileMayBe = Optional.ofNullable(profileSummaryInfoType)
        .map(ProfileSummaryInfoType::getProfile);

    var formerNameMayBe = profileMayBe
        .map(ProfileSummaryType::getFormerName);
    var companyName = formerNameMayBe
        .map(ProfileSummaryTypeFormerName::getFullName)
        .orElse(EMPTY_STRING);
    var language = formerNameMayBe
        .map(ProfileSummaryTypeFormerName::getLanguage)
        .orElse(Locale.UK.getLanguage());

    var telephoneNumber = profileMayBe
        .map(ProfileSummaryType::getTelephoneInfo)
        .map(TelephoneInfoType::getTelephone)
        .map(TelephoneType::getPhoneNumber)
        .orElse(EMPTY_STRING);

    var profileType = profileMayBe
        .map(ProfileSummaryType::getProfileType)
        .map(ProfileTypeType::getValue)
        .orElse(EMPTY_STRING);

    var arNumber = profileMayBe
        .map(ProfileSummaryType::getaRAccount)
        .map(DirectBillingType::getaRNumber)
        .orElse(EMPTY_STRING);

    var isProfileActive = profileMayBe
        .map(ProfileSummaryType::getStatusCode)
        .map(ProfileStatusType::getValue)
        .map(value -> value.equalsIgnoreCase(ACTIVE))
        .orElse(false);

    var profileRestrictionsMayBe = profileMayBe
        .map(ProfileSummaryType::getProfileRestrictions);
    var isCompanyRestricted = profileRestrictionsMayBe
        .map(ProfileRestrictions::getRestricted)
        .orElse(false);
    var restrictionReason = profileRestrictionsMayBe
        .map(ProfileRestrictions::getReason)
        .orElse(EMPTY_STRING);

    var address = profileMayBe
        .map(ProfileSummaryType::getAddressInfo)
        .map(AddressInfoType::getAddress)
        .map(this::toCompanyProfileAddress)
        .orElse(null);

    return CompanyProfile.builder()
        .name(companyName)
        .telephoneNumber(telephoneNumber)
        .profileType(profileType)
        .corpId(getIdFromProfile(profileTypeToIdMapping, CORPORATE_ID))
        .companyId(getIdFromProfile(profileTypeToIdMapping, PROFILE_ID))
        .language(language)
        .arNumber(arNumber)
        .active(isProfileActive)
        .restricted(isCompanyRestricted)
        .restrictedReason(restrictionReason)
        .address(address)
        .build();
  }

  private Address toCompanyProfileAddress(AddressType addressType) {
    var addressMayBe = Optional.ofNullable(addressType);
    var addressLines = addressMayBe.map(AddressType::getAddressLine)
        .orElseGet(Collections::emptyList);

    return Address.builder()
        .addressLine1(
            !addressLines.isEmpty() && StringUtils.isNotEmpty(addressLines.get(0)) ? addressLines.get(0) : EMPTY_STRING)
        .addressLine2(
            addressLines.size() > 1 && StringUtils.isNotEmpty(addressLines.get(1)) ? addressLines.get(1) : EMPTY_STRING)
        .addressLine3(
            addressLines.size() > 2 && StringUtils.isNotEmpty(addressLines.get(2)) ? addressLines.get(2) : EMPTY_STRING)
        .addressLine4(
            addressLines.size() > 3 && StringUtils.isNotEmpty(addressLines.get(3)) ? addressLines.get(3) : EMPTY_STRING)
        .city(addressMayBe.map(AddressType::getCityName).orElse(EMPTY_STRING))
        .country(addressMayBe.map(AddressType::getCountry).map(CountryNameType::getValue).orElse(EMPTY_STRING))
        .postalCode(addressMayBe.map(AddressType::getPostalCode).orElse(EMPTY_STRING))
        .build();
  }

  private static String getIdFromProfile(Map<String, List<UniqueIDType>> profileTypeToIdMapping, String profileName) {
    return Optional.ofNullable(profileTypeToIdMapping.get(profileName))
        .orElseGet(Collections::emptyList)
        .stream()
        .findFirst()
        .map(UniqueIDType::getId)
        .orElse(EMPTY_STRING);
  }

  private static AddressType getAddressFromProfile(Map<String,
      List<AddressInfoType>> addressTypeToIdMapping, String addressType) {
    return Optional.ofNullable(addressTypeToIdMapping.get(addressType))
        .orElseGet(Collections::emptyList)
        .stream()
        .findFirst()
        .map(AddressInfoType::getAddress)
        .orElse(null);
  }

  private static String getTelephoneFromProfile(Map<String,
      List<TelephoneInfoType>> phoneTypeToIdMapping, String telephoneType) {
    return Optional.ofNullable(phoneTypeToIdMapping.get(telephoneType))
        .orElseGet(Collections::emptyList)
        .stream()
        .findFirst()
        .map(TelephoneInfoType::getTelephone)
        .map(TelephoneType::getPhoneNumber)
        .orElse(EMPTY_STRING);
  }

}

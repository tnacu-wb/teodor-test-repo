package uk.co.whitbread.company.infrastructure.rest.client.cdh.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import uk.co.whitbread.cdh.generated.models.CompaniesDto;
import uk.co.whitbread.cdh.generated.models.MainContactDto;
import uk.co.whitbread.company.domain.model.out.Address;
import uk.co.whitbread.company.domain.model.out.CompanyProfile;

@Component
public class CdhCompanyTransformer {

  private static final String EMPTY_STRING = "";
  private static final String ACTIVE = "ACTIVE";

  @Named("extractCompaniesFrom")
  public List<CompanyProfile> extractCompaniesFrom(List<CompaniesDto> results) {
    return Optional.ofNullable(results)
        .orElseGet(Collections::emptyList)
        .stream()
        .map(this::toCompanyProfileFrom)
        .toList();
  }

  private CompanyProfile toCompanyProfileFrom(CompaniesDto companiesDto) {
    var companyMayBe = Optional.ofNullable(companiesDto);

    var name = companyMayBe
        .map(CompaniesDto::getCompanyName)
        .orElse(EMPTY_STRING);

    var telephoneNumber = companyMayBe
        .map(CompaniesDto::getMainContact)
        .map(MainContactDto::getPhoneNumber)
        .orElse(EMPTY_STRING);

    if (StringUtils.isEmpty(telephoneNumber)) {
      telephoneNumber = companyMayBe
          .map(CompaniesDto::getMainContact)
          .map(MainContactDto::getMobileNumber)
          .orElse(EMPTY_STRING);
    }

    var profileType = companyMayBe
        .map(CompaniesDto::getCompanyType)
        .orElse(EMPTY_STRING);

    var corpId = companyMayBe
        .map(CompaniesDto::getGlobalCompanyId)
        .map(String::valueOf)
        .orElseGet(() -> EMPTY_STRING);

    var active = companyMayBe
        .map(CompaniesDto::getStatus)
        .map(value -> value.equalsIgnoreCase(ACTIVE))
        .orElse(false);

    var address  = companyMayBe
        .map(CompaniesDto::getCompanyAddress)
        .map(companyAddressDto -> Address
            .builder()
            .addressLine1(companyAddressDto.getAddressLine1())
            .addressLine2(companyAddressDto.getAddressLine2())
            .addressLine3(companyAddressDto.getAddressLine3())
            .addressLine4(companyAddressDto.getAddressLine4())
            .country(companyAddressDto.getCountryCode())
            .postalCode(companyAddressDto.getPostCode())
            .build())
        .orElse(null);


    return CompanyProfile.builder()
        .name(name)
        .telephoneNumber(telephoneNumber)
        .profileType(profileType)
        .corpId(corpId)
        .active(active)
        .address(address)
        .build();
  }
}

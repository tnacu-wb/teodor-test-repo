package uk.co.whitbread.basket.infrastructure.rest.client.cdh.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.basket.domain.model.payments.out.CdhSearchCompaniesResponse;
import uk.co.whitbread.basket.domain.model.payments.out.Company;
import uk.co.whitbread.basket.domain.model.payments.out.CompanyAddress;
import uk.co.whitbread.basket.generated.models.cdh.CompaniesDto;
import uk.co.whitbread.basket.generated.models.cdh.CompanyAddressDto;
import uk.co.whitbread.basket.generated.models.cdh.CompanySearchResponseDto;

@Mapper(componentModel = "spring")
public interface CdhSearchCompaniesResponseMapper {

  CdhSearchCompaniesResponse toModel(CompanySearchResponseDto companySearchResponseDto);

  default List<Company> toCompanyListModel(List<CompaniesDto> companiesDtoList) {
    if (companiesDtoList == null) {
      return null;
    }

    return companiesDtoList.stream()
            .map(this::toCompanyModel)
            .toList();
  }

  default Company toCompanyModel(CompaniesDto companiesDto) {
    if (companiesDto == null) {
      return null;
    }

    CompanyAddressDto companyAddressDto = companiesDto.getCompanyAddress();
    CompanyAddress companyAddress = CompanyAddress.builder()
            .companyName(companiesDto.getCompanyName())
            .alternateCompanyName(companiesDto.getAlternateCompanyName())
            .addressLine1(companyAddressDto != null ? companyAddressDto.getAddressLine1() : null)
            .addressLine2(companyAddressDto != null ? companyAddressDto.getAddressLine2() : null)
            .addressLine3(companyAddressDto != null ? companyAddressDto.getAddressLine3() : null)
            .addressLine4(companyAddressDto != null ? companyAddressDto.getAddressLine4() : null)
            .addressLine5(companyAddressDto != null ? companyAddressDto.getAddressLine5() : null)
            .countryCode(companyAddressDto != null ? companyAddressDto.getCountryCode() : null)
            .postCode(companyAddressDto != null ? companyAddressDto.getPostCode() : null)
            .build();

    return Company.builder()
            .companyAddress(companyAddress)
            .build();
  }
}


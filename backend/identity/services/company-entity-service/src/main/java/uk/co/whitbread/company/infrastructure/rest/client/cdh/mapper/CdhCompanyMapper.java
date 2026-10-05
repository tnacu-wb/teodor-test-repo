package uk.co.whitbread.company.infrastructure.rest.client.cdh.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.cdh.generated.models.CompanySearchResponseDto;
import uk.co.whitbread.company.domain.model.out.CompaniesProfile;
import uk.co.whitbread.company.domain.model.out.CompanyProfile;
import uk.co.whitbread.ohip.generated.models.CompanyProfileDto;

@Mapper(componentModel = "spring", uses = {
    CdhCompanyTransformer.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CdhCompanyMapper {
  @Mapping(target = "totalResults", source = "companySearchResponseDto.totalResults")
  @Mapping(target = "companies",
      source = "companySearchResponseDto.results",
      qualifiedByName = "extractCompaniesFrom")
  CompaniesProfile toCompaniesResponseDto(CompanySearchResponseDto companySearchResponseDto);

  CompanyProfile toCompanyProfileDto(CompanyProfileDto companyProfileDto);
}

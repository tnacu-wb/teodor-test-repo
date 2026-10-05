package uk.co.whitbread.company.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.company.domain.model.out.CompaniesProfile;
import uk.co.whitbread.company.domain.model.out.CompanyProfile;
import uk.co.whitbread.ohip.generated.models.CompaniesProfileDto;
import uk.co.whitbread.ohip.generated.models.CompanyProfileDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProfileOhipMapper {
  CompaniesProfile toCompaniesProfileRequestDto(CompaniesProfileDto companiesProfileDto);

  CompanyProfile toCompanyProfileDto(CompanyProfileDto companyProfileDto);
}

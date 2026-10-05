package uk.co.whitbread.company.infrastructure.rest.controller.company.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.company.domain.model.in.CompaniesProfileRequest;
import uk.co.whitbread.company.domain.model.in.CompaniesSearchRequest;
import uk.co.whitbread.company.domain.model.out.CompaniesProfile;
import uk.co.whitbread.company.domain.model.out.CompanyProfile;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.in.CompaniesProfileRequestDto;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.in.CompaniesRequestDto;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.out.CompaniesResponseDto;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.out.CompanyResponseDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CompaniesMapper {

  CompaniesProfileRequest toCompaniesProfileRequestModel(CompaniesProfileRequestDto companiesProfileRequestDto);

  @Mapping(target = "companyName", source = "companyName")
  @Mapping(target = "pageSize", source = "limit")
  @Mapping(target = "pageNumber", source = "offset")
  CompaniesSearchRequest toCompaniesSearchRequestModel(CompaniesRequestDto companiesRequestDto);

  CompaniesResponseDto toCompaniesProfileDto(CompaniesProfile companiesProfile);

  CompanyResponseDto toCompanyResponseDto(CompanyProfile companyProfile);
}

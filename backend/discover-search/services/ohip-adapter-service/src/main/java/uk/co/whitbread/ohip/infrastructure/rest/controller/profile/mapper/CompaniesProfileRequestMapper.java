package uk.co.whitbread.ohip.infrastructure.rest.controller.profile.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.profile.in.CompaniesProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.out.CompaniesProfile;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.in.CompaniesProfileRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.out.CompaniesProfileDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CompaniesProfileRequestMapper {

  CompaniesProfileRequest toCompaniesProfileDomainModel(CompaniesProfileRequestDto companiesProfileRequestDto);

  CompaniesProfileDto toCompaniesProfileDto(CompaniesProfile companiesProfile);
}

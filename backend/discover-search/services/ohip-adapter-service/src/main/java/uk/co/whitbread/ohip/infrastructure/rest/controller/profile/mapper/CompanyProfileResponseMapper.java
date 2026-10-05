package uk.co.whitbread.ohip.infrastructure.rest.controller.profile.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.profile.out.CompanyProfile;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.out.CompanyProfileDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CompanyProfileResponseMapper {
  CompanyProfileDto toCompanyProfileDto(CompanyProfile companyProfile);
}

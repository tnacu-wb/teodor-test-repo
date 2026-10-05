package uk.co.whitbread.cdh.infrastructure.rest.controller.account.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.cdh.domain.model.account.out.Company;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.CompanyDto;

@Mapper(componentModel = "spring")
public interface CompanyResponseMapper {
  CompanyDto toDto(Company company);
}


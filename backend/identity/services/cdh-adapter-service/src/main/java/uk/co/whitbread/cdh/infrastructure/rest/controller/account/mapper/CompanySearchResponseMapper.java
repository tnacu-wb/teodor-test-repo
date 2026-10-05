package uk.co.whitbread.cdh.infrastructure.rest.controller.account.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.cdh.domain.model.account.out.CompanySearch;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.CompanySearchResponseDto;

@Mapper(componentModel = "spring")
public interface CompanySearchResponseMapper {
  CompanySearchResponseDto toDto(CompanySearch companySearch);
}

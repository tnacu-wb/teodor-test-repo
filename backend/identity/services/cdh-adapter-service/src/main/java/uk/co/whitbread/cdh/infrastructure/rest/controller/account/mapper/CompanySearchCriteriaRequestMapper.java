package uk.co.whitbread.cdh.infrastructure.rest.controller.account.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.cdh.domain.model.account.in.CompanySearchCriteria;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.in.CompanySearchCriteriaDto;

@Mapper(componentModel = "spring")
public interface CompanySearchCriteriaRequestMapper {
  CompanySearchCriteria toModel(CompanySearchCriteriaDto companySearchCriteriaDto);
}

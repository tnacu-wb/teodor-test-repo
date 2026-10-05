package uk.co.whitbread.spending.infrastructure.rest.controller.spending.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.spending.domain.model.in.CompanySpendingRequest;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.in.CompanySpendingRequestDto;

@Mapper(componentModel = "spring")
public interface CompanySpendingRequestDtoMapper {

  CompanySpendingRequest toModel(CompanySpendingRequestDto companySpendingRequestDto);
}

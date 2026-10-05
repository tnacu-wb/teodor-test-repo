package uk.co.whitbread.spending.infrastructure.rest.controller.spending.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.spending.domain.model.out.CompanySpendingResponse;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.CompanySpendingResponseDto;

@Mapper(componentModel = "spring")
public interface CompanySpendingResponseDtoMapper {

  @Mapping(target = "companySpendingDtoList", source = "companySpendingList")
  CompanySpendingResponseDto toDto(CompanySpendingResponse companySpendingResponse);

}

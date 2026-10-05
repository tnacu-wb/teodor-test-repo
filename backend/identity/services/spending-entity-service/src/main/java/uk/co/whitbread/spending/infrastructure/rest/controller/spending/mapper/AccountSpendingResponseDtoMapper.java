package uk.co.whitbread.spending.infrastructure.rest.controller.spending.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.spending.domain.model.out.AccountSpendingResponse;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.AccountSpendingResponseDto;

@Mapper(componentModel = "spring")
public interface AccountSpendingResponseDtoMapper {

  @Mapping(target = "accountSpendingDtoList", source = "accountSpendingList")
  AccountSpendingResponseDto toDto(AccountSpendingResponse accountSpendingResponse);

}

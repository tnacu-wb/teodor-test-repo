package uk.co.whitbread.spending.infrastructure.rest.controller.spending.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.spending.domain.model.in.AccountSpendingRequest;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.in.AccountSpendingRequestDto;

@Mapper(componentModel = "spring")
public interface AccountSpendingRequestDtoMapper {

  AccountSpendingRequest toModel(AccountSpendingRequestDto accountSpendingRequestDto);
}

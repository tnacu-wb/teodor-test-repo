package uk.co.whitbread.account.infrastructure.rest.controller.account.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.account.domain.model.in.AccountRegistrationRequest;
import uk.co.whitbread.account.domain.model.out.AccountRegistrationResponse;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.AccountRegistrationRequestDto;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.out.AccountRegistrationResponseDto;

@Mapper(componentModel = "spring")
public interface AccountRegistrationMapper {
  AccountRegistrationRequest toModel(AccountRegistrationRequestDto accountRegistrationRequestDto);

  AccountRegistrationResponseDto toDto(AccountRegistrationResponse accountRegistrationResponse);
}

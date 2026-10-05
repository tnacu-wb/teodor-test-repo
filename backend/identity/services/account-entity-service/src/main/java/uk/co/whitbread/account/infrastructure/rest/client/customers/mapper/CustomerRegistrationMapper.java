package uk.co.whitbread.account.infrastructure.rest.client.customers.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.account.domain.model.in.AccountRegistrationRequest;
import uk.co.whitbread.account.domain.model.out.AccountRegistrationResponse;
import uk.co.whitbread.account.infrastructure.rest.client.customers.model.in.CustomerRegistrationRequestDto;
import uk.co.whitbread.account.infrastructure.rest.client.customers.model.out.CustomerRegistrationResponseDto;

@Mapper(componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CustomerRegistrationMapper {
  AccountRegistrationResponse toModel(CustomerRegistrationResponseDto customerRegistrationResponse);

  CustomerRegistrationRequestDto toRequestDto(
      AccountRegistrationRequest accountRegistrationRequest);
}

package uk.co.whitbread.payments.infrastructure.rest.client.reservation.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.payments.domain.model.out.DepositsResponse;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.mapper.AccountMapperTransformer;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out.DepositsResponseDto;

@Mapper(componentModel = "spring", uses = AccountMapperTransformer.class,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface DepositFolioMapper {

  DepositsResponse toDepositFolioModel(DepositsResponseDto depositsResponse);
}
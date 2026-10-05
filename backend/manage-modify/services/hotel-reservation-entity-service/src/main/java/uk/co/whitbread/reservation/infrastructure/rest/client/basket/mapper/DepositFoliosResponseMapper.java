package uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PrepaidDepositsDto;
import uk.co.whitbread.reservation.domain.model.out.DepositFoliosResponse;

@Mapper(componentModel = "spring", uses = {DepositFolioResponseMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DepositFoliosResponseMapper {

  @Mapping(target = "depositFolios", source = "prepaidDepositsDto")
  DepositFoliosResponse toModel(PrepaidDepositsDto prepaidDepositsDto);
}

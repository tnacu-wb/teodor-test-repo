package uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PrepaidDepositsRequestDto;
import uk.co.whitbread.reservation.domain.model.out.DepositFoliosResponse;


@Mapper(componentModel = "spring", uses = {DepositFoliosMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DepositFoliosRequestMapper {

  @Mapping(target = "prepaidDeposits", source = "depositFolios")
  PrepaidDepositsRequestDto toDto(DepositFoliosResponse depositFoliosResponse);

}

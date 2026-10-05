package uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFoliosResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PrepaidDepositDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PrepaidDepositsDto;
import uk.co.whitbread.reservation.domain.model.in.DepositFoliosRequest;
import uk.co.whitbread.reservation.domain.model.out.DepositFolio;
import uk.co.whitbread.reservation.domain.model.out.DepositFoliosResponse;
import uk.co.whitbread.reservation.domain.model.out.PrepaidDeposits;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.DepositFoliosRequestDto;


@Mapper(componentModel = "spring", uses = {DepositFolioChargeMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DepositFoliosMapper {

  PrepaidDepositDto toDto(DepositFolio depositFolio);

  DepositFoliosResponseDto toDto(DepositFoliosResponse depositFoliosResponse);

  DepositFoliosRequest toDto(DepositFoliosRequestDto depositFoliosRequestDto);

  PrepaidDeposits toModel(PrepaidDepositsDto prepaidDepositDto);

  DepositFoliosResponse toModel(DepositFoliosRequestDto depositFoliosResponse);
}

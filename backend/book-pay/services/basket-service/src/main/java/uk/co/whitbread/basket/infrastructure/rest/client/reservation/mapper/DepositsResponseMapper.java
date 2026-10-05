package uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.reservation.in.DepositFoliosRequest;
import uk.co.whitbread.basket.domain.model.reservation.out.DepositFoliosResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.DepositsResponse;
import uk.co.whitbread.basket.generated.models.reservation.DepositFoliosRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.DepositFoliosResponseDto;
import uk.co.whitbread.basket.generated.models.reservation.DepositsResponseDto;

@Mapper(componentModel = "spring")
public interface DepositsResponseMapper {

  DepositsResponse toModel(DepositsResponseDto depositsResponseDto);

  DepositFoliosRequestDto toDepositModel(DepositFoliosRequest depositFolioRequest);

  DepositFoliosResponse toDto(DepositFoliosResponseDto depositFolioResponse);
}

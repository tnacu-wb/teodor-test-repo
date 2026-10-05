package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFoliosRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFoliosResponseDto;
import uk.co.whitbread.reservation.domain.model.in.DepositFoliosRequest;
import uk.co.whitbread.reservation.domain.model.out.DepositFoliosResponse;

@Mapper(componentModel = "spring")
public interface DepositFoliosResponseOhipMapper {

  DepositFoliosResponse toModel(DepositFoliosResponseDto depositFoliosResponseDto);

  DepositFoliosRequestDto toDto(DepositFoliosResponse depositFoliosResponse);

  DepositFoliosRequestDto toDto(DepositFoliosRequest depositFoliosRequest);
}

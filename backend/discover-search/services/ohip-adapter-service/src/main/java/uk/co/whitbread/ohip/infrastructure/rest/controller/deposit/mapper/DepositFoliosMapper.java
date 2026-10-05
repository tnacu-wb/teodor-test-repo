package uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.mapper;


import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFoliosResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.model.in.DepositFoliosRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.model.out.DepositFoliosResponseDto;

@Mapper(componentModel = "spring")
public interface DepositFoliosMapper {

  DepositFoliosResponseDto toDto(DepositFoliosResponse depositFoliosResponse);

  DepositFoliosResponse toModel(DepositFoliosRequestDto depositFoliosResponse);

}

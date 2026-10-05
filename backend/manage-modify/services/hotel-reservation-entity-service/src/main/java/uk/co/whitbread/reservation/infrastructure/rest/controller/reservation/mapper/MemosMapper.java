package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.CreateMemoRequest;
import uk.co.whitbread.reservation.domain.model.out.MemosResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.CreateMemoRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.MemosResponseDto;

@Mapper(componentModel = "spring")
public interface MemosMapper {

  CreateMemoRequest toModel(CreateMemoRequestDto createMemoRequestDto);

  MemosResponseDto toDto(MemosResponse memosResponse);
}

package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.CreateMemoRequest;
import uk.co.whitbread.ohip.domain.model.reservation.out.MemosResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.CreateMemoRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.MemosResponseDto;

@Mapper(componentModel = "spring")
public interface MemosMapper {

  MemosResponseDto toDto(MemosResponse memosResponse);

  CreateMemoRequest toModel(CreateMemoRequestDto createMemoRequestDto);

}

package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CreateMemoRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MemosResponseDto;
import uk.co.whitbread.reservation.domain.model.in.CreateMemoRequest;
import uk.co.whitbread.reservation.domain.model.out.MemosResponse;

@Mapper(componentModel = "spring")
public interface MemosOhipMapper {

  CreateMemoRequestDto toDto(CreateMemoRequest createMemoRequest);

  MemosResponse toModel(MemosResponseDto memosResponseDto);

}

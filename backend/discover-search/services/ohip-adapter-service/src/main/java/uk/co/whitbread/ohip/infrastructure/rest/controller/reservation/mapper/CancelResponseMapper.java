package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.out.CancelInformationResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.CancelInformationResponseDto;

@Mapper(componentModel = "spring")
public interface CancelResponseMapper {

  CancelInformationResponseDto toDto(CancelInformationResponse cancelInformationResponse);
}

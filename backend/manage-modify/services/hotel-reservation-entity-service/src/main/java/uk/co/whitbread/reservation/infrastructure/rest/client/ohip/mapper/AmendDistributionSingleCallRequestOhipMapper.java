package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.AmendDistributionSingleCallRequest;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model.AmendDistributionSingleCallRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.AmendDistributionSingleCallResponseDto;

@Mapper(componentModel = "spring")
public interface AmendDistributionSingleCallRequestOhipMapper {
  AmendDistributionSingleCallRequestDto toDto(AmendDistributionSingleCallRequest amendDistributionSingleCallRequest);

  AmendDistributionSingleCallResponseDto toAmendDistributionDto(
          AmendDistributionSingleCallRequest amendDistributionSingleCallRequest);
}
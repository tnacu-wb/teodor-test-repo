package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.out.CancellationPoliciesResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.CancellationPoliciesResponseDto;


@Mapper(componentModel = "spring")
public interface CancellationPoliciesResponseMapper {
  CancellationPoliciesResponseDto toDto(CancellationPoliciesResponse cancellationPoliciesResponse);
}
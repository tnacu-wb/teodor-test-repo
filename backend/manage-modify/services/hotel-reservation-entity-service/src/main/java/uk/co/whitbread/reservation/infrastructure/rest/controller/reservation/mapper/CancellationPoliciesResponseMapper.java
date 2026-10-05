package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.CancellationPoliciesResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.CancellationPoliciesResponseDto;


@Mapper(componentModel = "spring")
public interface CancellationPoliciesResponseMapper {
  CancellationPoliciesResponseDto toDto(CancellationPoliciesResponse cancellationPoliciesResponse);
}

package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateCancellationPoliciesRequestDto;
import uk.co.whitbread.reservation.domain.model.in.UpdateCancellationPoliciesRequest;
import uk.co.whitbread.reservation.domain.model.out.CancellationPoliciesResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.CancellationPoliciesResponseDto;


@Mapper(componentModel = "spring")
public interface CancellationPoliciesOhipMapper {

  CancellationPoliciesResponse toModel(
      CancellationPoliciesResponseDto cancellationPoliciesResponse);

  UpdateCancellationPoliciesRequestDto toDto(UpdateCancellationPoliciesRequest request);
}

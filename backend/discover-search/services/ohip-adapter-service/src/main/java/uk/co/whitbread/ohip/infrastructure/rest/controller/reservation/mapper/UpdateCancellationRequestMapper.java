package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateCancellationPoliciesRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateCancellationPolicyRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateCancellationPoliciesRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateCancellationPolicyRequestDto;


@Mapper(componentModel = "spring")
public interface UpdateCancellationRequestMapper {

  UpdateCancellationPolicyRequest toModel(
      UpdateCancellationPolicyRequestDto updateCancellationPolicyRequestDto);

  UpdateCancellationPoliciesRequest toModel(
      UpdateCancellationPoliciesRequestDto updateCancellationPolicyRequestDto);
}

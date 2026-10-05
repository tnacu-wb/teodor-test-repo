package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateReasonForStayRequestDto;
import uk.co.whitbread.reservation.domain.model.in.UpdateReasonForStayRequest;

@Mapper(componentModel = "spring")
public interface UpdateReasonForStayOhipMapper {

  UpdateReasonForStayRequestDto toDto(UpdateReasonForStayRequest updateReasonForStayRequest);
}

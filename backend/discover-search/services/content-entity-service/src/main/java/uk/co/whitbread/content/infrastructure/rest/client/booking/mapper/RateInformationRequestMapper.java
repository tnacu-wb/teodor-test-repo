package uk.co.whitbread.content.infrastructure.rest.client.booking.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.booking.in.RateInformationRequest;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.out.RateInformationRequestAemDto;

@Mapper(componentModel = "spring")
public interface RateInformationRequestMapper {

  RateInformationRequestAemDto toDto(RateInformationRequest rateInformationRequest);
}

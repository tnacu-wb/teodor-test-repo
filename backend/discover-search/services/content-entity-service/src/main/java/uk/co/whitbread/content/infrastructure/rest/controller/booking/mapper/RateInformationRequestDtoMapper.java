package uk.co.whitbread.content.infrastructure.rest.controller.booking.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.booking.in.RateInformationRequest;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.model.in.RateInformationRequestDto;

@Mapper(componentModel = "spring")
public interface RateInformationRequestDtoMapper {

  RateInformationRequest toDomainModel(RateInformationRequestDto rateInformationRequestDto);
}

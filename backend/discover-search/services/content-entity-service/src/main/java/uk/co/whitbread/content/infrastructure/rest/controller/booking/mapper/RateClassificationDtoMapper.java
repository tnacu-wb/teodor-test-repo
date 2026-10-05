package uk.co.whitbread.content.infrastructure.rest.controller.booking.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.booking.out.RateClassification;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.model.out.RateClassificationDto;

@Mapper(componentModel = "spring")
public interface RateClassificationDtoMapper {

  RateClassificationDto toDto(RateClassification rateClassification);

}

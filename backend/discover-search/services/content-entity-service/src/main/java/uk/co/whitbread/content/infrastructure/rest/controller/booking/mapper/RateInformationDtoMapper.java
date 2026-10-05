package uk.co.whitbread.content.infrastructure.rest.controller.booking.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.booking.out.RateInformation;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.model.out.RateInformationDto;

@Mapper(componentModel = "spring", uses = {
    RateClassificationDtoMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RateInformationDtoMapper {

  @Mapping(source = "rateClassifications", target = "rateClassifications")
  RateInformationDto toDto(RateInformation rateInformation);
}

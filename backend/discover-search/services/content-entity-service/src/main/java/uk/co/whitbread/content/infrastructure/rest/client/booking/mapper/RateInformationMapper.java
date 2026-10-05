package uk.co.whitbread.content.infrastructure.rest.client.booking.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.booking.out.RateInformation;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.AemRateInformationDto;

@Mapper(componentModel = "spring", uses = {
    RateClassificationMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RateInformationMapper {

  RateInformation toDomainModel(AemRateInformationDto aemRateInformationDto);

}

package uk.co.whitbread.infrastructure.rest.client.opera.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.domain.model.opera.out.HotelStatus;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelStatusDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HotelStatusMapper {

  HotelStatus toDomainModel(HotelStatusDto hotelStatusDto);

}

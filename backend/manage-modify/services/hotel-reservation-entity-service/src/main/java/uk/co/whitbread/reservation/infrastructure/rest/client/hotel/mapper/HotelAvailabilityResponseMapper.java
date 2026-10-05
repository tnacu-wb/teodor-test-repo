package uk.co.whitbread.reservation.infrastructure.rest.client.hotel.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.entity.service.generated.models.hotel.HotelAvailabilityByIdsDto;
import uk.co.whitbread.hotel.entity.service.generated.models.hotel.HotelAvailabilityByIdsV2Dto;
import uk.co.whitbread.reservation.domain.model.availability.out.HotelAvailabilityByIds;
import uk.co.whitbread.reservation.domain.model.availability.out.HotelAvailabilityByIdsV2;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HotelAvailabilityResponseMapper {

  HotelAvailabilityByIds toDomainModel(HotelAvailabilityByIdsDto hotelAvailability);

  HotelAvailabilityByIdsV2 toDomainModel(HotelAvailabilityByIdsV2Dto hotelAvailability);

}

package uk.co.whitbread.reservation.infrastructure.rest.client.hotel.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.reservation.domain.model.availability.in.HotelAvailabilitiesByIdsRequest;
import uk.co.whitbread.reservation.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotel.model.HotelAvailabilityRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotel.model.HotelAvailabilityV2RequestDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HotelEntityAvailabilityRequestMapper {

  HotelAvailabilityRequestDto toDto(HotelAvailabilitiesByIdsRequest hotelAvailability);

  HotelAvailabilityV2RequestDto toDto(HotelAvailabilityByIdsV2Request hotelAvailability);

}

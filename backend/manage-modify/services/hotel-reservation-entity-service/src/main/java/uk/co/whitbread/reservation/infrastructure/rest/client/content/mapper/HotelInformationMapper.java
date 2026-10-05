package uk.co.whitbread.reservation.infrastructure.rest.client.content.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.entity.service.generated.models.content.HotelInformationExtendedDto;
import uk.co.whitbread.reservation.domain.model.out.HotelInfoResponse;

@Mapper(componentModel = "spring")
public interface HotelInformationMapper {

  HotelInfoResponse toModel(HotelInformationExtendedDto hotelInformationDto);
}

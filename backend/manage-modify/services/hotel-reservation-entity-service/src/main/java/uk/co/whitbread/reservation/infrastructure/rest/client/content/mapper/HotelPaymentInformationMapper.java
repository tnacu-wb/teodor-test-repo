package uk.co.whitbread.reservation.infrastructure.rest.client.content.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.entity.service.generated.models.content.HotelPaymentInformationDto;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.HotelPaymentInformation;

@Mapper(componentModel = "spring")
public interface HotelPaymentInformationMapper {

  HotelPaymentInformation toModel(HotelPaymentInformationDto hotelPaymentInformationDto);

}

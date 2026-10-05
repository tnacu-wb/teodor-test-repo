package uk.co.whitbread.basket.infrastructure.rest.client.content.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.content.out.BusinessNotesResponse;
import uk.co.whitbread.basket.domain.model.content.out.HotelPaymentInformation;
import uk.co.whitbread.basket.generated.models.content.BusinessNotesResponseDto;
import uk.co.whitbread.basket.generated.models.content.HotelPaymentInformationDto;

@Mapper(componentModel = "spring")
public interface ContentResponseMapper {

  HotelPaymentInformation toModel(HotelPaymentInformationDto hotelPaymentInformationDto);

  BusinessNotesResponse toModel(BusinessNotesResponseDto businessNotesDto);
}

package uk.co.whitbread.availabilitycacheservice.infrastructure.client.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.availabilitycacheservice.domain.model.content.HotelCityTax;
import uk.co.whitbread.content.entity.service.generated.models.content.HotelCityTaxDto;

@Mapper(componentModel = "spring")
public interface HotelCityTaxMapper {

  HotelCityTax toModel(HotelCityTaxDto hotelCityTaxDto);
}

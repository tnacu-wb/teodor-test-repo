package uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.snowdrop;

import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.availabilitycacheservice.domain.model.snowdrop.HotelDetails;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.snowdrop.HotelDetailsDto;

@Mapper(componentModel = "spring")
public interface HotelDetailsMapper {

  List<HotelDetails> toModel(List<HotelDetailsDto> hotelDetailsDto);
}

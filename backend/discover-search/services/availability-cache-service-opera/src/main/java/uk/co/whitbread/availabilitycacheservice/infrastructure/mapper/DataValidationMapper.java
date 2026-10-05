package uk.co.whitbread.availabilitycacheservice.infrastructure.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.HotelAvailResultSetDto;

@Mapper(componentModel = "spring")
public interface DataValidationMapper {

  List<HotelAvailResultSetDto> toHotelAvailResultSetDtoList(
      List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSets);

}

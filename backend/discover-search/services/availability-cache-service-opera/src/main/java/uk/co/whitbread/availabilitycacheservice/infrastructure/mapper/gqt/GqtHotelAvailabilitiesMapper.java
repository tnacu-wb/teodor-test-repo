package uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.gqt;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.gqt.GqtOperaHotelAvailabilitiesDto;

@Mapper(componentModel = "spring")
public interface GqtHotelAvailabilitiesMapper {

  GqtHotelAvailabilitiesMapper INSTANCE = Mappers.getMapper(GqtHotelAvailabilitiesMapper.class);

  GqtOperaHotelAvailabilitiesDto toGqtOperaHotelAvailabilitiesDto(
      GqtOperaHotelAvailabilities gqtOperaHotelAvailabilities);

  List<GqtOperaHotelAvailabilitiesDto> toGqtOperaHotelAvailabilitiesDtoList(
      List<GqtOperaHotelAvailabilities> gqtOperaHotelAvailabilitiesList);

}

package uk.co.whitbread.content.infrastructure.rest.controller.hotel.mapper;

import java.time.LocalDate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.hotel.in.HotelInformationRequest;
import uk.co.whitbread.content.domain.model.hotel.in.HotelShortInformationRequest;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.in.HotelInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.in.HotelShortInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.in.SlugRequestDto;

@Mapper(componentModel = "spring")
public interface HotelInformationRequestDtoMapper {

  HotelShortInformationRequest toDomainModel(HotelShortInformationRequestDto hotelShortInformationRequestDto);

  HotelInformationRequest toDomainModel(String hotelId,
      HotelInformationRequestDto hotelInformationRequestDto);

  @Mapping(source = "slugRequestDto.slug", target = "slug")
  HotelInformationRequest toDomainModel(SlugRequestDto slugRequestDto,
      HotelInformationRequestDto hotelInformationRequestDto,
      LocalDate stayStartDate,
      LocalDate stayEndDate);
}

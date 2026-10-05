package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.content.domain.model.hotel.in.HotelInformationRequest;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.out.HotelInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.out.HotelsInformationDto;

@Mapper(componentModel = "spring")
public interface HotelInformationRequestMapper {

  HotelInformationDto toDtoModel(HotelInformationRequest hotelInformationRequest);

  //This @Mapping forces the source to be the parameter hotelId instead of the field from the hotelInformationRequest
  @Mapping(source = "hotelId", target = "hotelIds", qualifiedByName = "toListOfHotelIdModel")
  HotelsInformationDto toDtoModel(String hotelId, HotelInformationRequest hotelInformationRequest);

  @Mapping(source = "hotelId", target = "hotelIds", qualifiedByName = "toListOfHotelIdModel")
  HotelsInformationDto toHotelsDtoModel(HotelInformationRequest hotelInformationRequest);

  @Named("toListOfHotelIdModel")
  default List<String> toListOfHotelIdModel(String hotelId) {
    return Optional.ofNullable(hotelId)
        .map(List::of)
        .orElseGet(Collections::emptyList);
  }
}

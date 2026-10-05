package uk.co.whitbread.infrastructure.rest.controller.availability.mapper;

import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.availability.in.CorporateRate;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsRequest;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.domain.model.availability.in.Room;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.CorporateRateDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.HotelAvailabilitiesByIdsRequestDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.HotelAvailabilitiesByIdsRequestV2Dto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.HotelAvailabilitiesByIdsRequestV3Dto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.RoomDto;

@Mapper(componentModel = "spring")
public interface HotelAvailabilitiesByIdsRequestDtoMapper {

  HotelAvailabilityByIdsRequest toModel(
      HotelAvailabilitiesByIdsRequestDto hotelAvailabilitiesByIdsRequestDto);


  HotelAvailabilityByIdsV2Request toV2Model(HotelAvailabilitiesByIdsRequestV2Dto requestDto);

  default List<CorporateRate> mapCorporateRates(CorporateRateDto corporateRateDto) {
    if (corporateRateDto == null) {
      return Collections.emptyList();
    }
    return Collections.singletonList(mapCorporateRate(corporateRateDto));
  }

  default CorporateRate mapCorporateRate(CorporateRateDto corporateRateDto) {

    CorporateRate.CorporateRateBuilder corporateRate = CorporateRate.builder();
    corporateRate.corporateId(corporateRateDto.getCorporateId());
    corporateRate.ratePlanSets(corporateRateDto.getRatePlanSets());
    return corporateRate.build();

  }

  HotelAvailabilityByIdsV2Request toV3Model(HotelAvailabilitiesByIdsRequestV3Dto requestDto);

  default Room roomDtoToRoom(RoomDto roomDto) {
    if (roomDto == null) {
      return null;
    }

    Room.RoomBuilder room = Room.builder();

    room.tag(roomDto.getTag());
    room.adults(roomDto.getAdults());
    room.children(roomDto.getChildren());
    room.numberOfRooms(roomDto.getNumberOfRooms());
    room.roomType(roomDto.getPmsRoomType());

    return room.build();
  }
}

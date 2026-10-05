package uk.co.whitbread.infrastructure.rest.client.availability.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.domain.model.availability.out.HotelAvailability;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityByIds;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityByIdsV2;
import uk.co.whitbread.domain.model.availability.out.MultiAvailabilityResponse;
import uk.co.whitbread.domain.model.srp.out.Cost;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilityResponse;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AvailabilityByIdsResponseV2Dto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelAvailabilityResultDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelAvailabilityResultV2Dto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MultiAvailabilityResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MultiAvailabilityResponseV2Dto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomRateInfoDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.HotelAvailabilityByIdsDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.HotelAvailabilityDto;


@Mapper(componentModel = "spring")
public interface HotelAvailabilityMapper {

  HotelAvailability toModel(HotelAvailabilityDto hotelAvailabilityOhipDto);

  HotelAvailabilityByIds toAvailabilityByIdsModel(
      HotelAvailabilityByIdsDto hotelAvailabilityByIdsOhipDto);

  MultiAvailabilityResponse toMultiAvaDomainModel(
          MultiAvailabilityResponseDto multiHotelAvailabilityOhip);

  @Mapping(target = "hotelAvailabilityList", source = "multiHotelAvailabilityDto",
      qualifiedByName = "mapMultiHotelAvaResponse")
  @Mapping(target = "total", source = "multiHotelAvailabilityDto", qualifiedByName = "mapTotalNumberOfHotels")
  HotelAvailabilitiesResponse toHotelAvaResponse(
          MultiAvailabilityResponseDto multiHotelAvailabilityDto);

  @Mapping(target = "hotelAvailabilityList", source = "hotelAvailabilityResults",
      qualifiedByName = "mapNewOperaResponse")
  HotelAvailabilitiesResponse toNewHotelAvaResponse(MultiAvailabilityResponseV2Dto multiAvailabilityResponseV2Dto);

  @Named("mapMultiHotelAvaResponse")
  default List<HotelAvailabilityResponse> mapMultiHotelAvaResponse(
          MultiAvailabilityResponseDto multiHotelAvailabilityDto) {
    List<HotelAvailabilityResponse> hotelAvailabilityResponseList = new ArrayList<>();
    if (!multiHotelAvailabilityDto.getHotelAvailabilityResults().isEmpty()) {
      multiHotelAvailabilityDto.getHotelAvailabilityResults().forEach(hotelAvailabilityResult -> {
        hotelAvailabilityResponseList.add(HotelAvailabilityResponse.builder()
            .hotelId(hotelAvailabilityResult.getHotelId())
            .available(hotelAvailabilityResult.getAvailable())
            .limitedAvailability(false)
            .lowestRoomRate(hotelAvailabilityResult.getAvailable()
                ? Cost.builder()
                .netTotal(getLowestRoomRate(hotelAvailabilityResult).getNetTotal())
                .currencyCode(getLowestRoomRate(hotelAvailabilityResult).getCurrencyCode())
                .build() : null)
            .build());
      });
    }
    return hotelAvailabilityResponseList;
  }

  @Named("mapNewOperaResponse")
  default List<HotelAvailabilityResponse> mapNewOperaResponse(
      List<HotelAvailabilityResultV2Dto> hotelAvailabilityResults) {
    List<HotelAvailabilityResponse> hotelAvailabilityResponseList = new ArrayList<>();
    if (!hotelAvailabilityResults.isEmpty()) {
      hotelAvailabilityResults.forEach(response -> {
        hotelAvailabilityResponseList.add(HotelAvailabilityResponse.builder()
            .hotelId(response.getHotelId())
            .available(response.getAvailable())
            .lowestRoomRate(response.getAvailable()
                ? Cost.builder()
                .netTotal(response.getMinimumRate())
                .currencyCode(response.getCurrency())
                .build() : null)
            .limitedAvailability(false)
            .build());
      });
    }

    return hotelAvailabilityResponseList;
  }

  default Cost getLowestRoomRate(HotelAvailabilityResultDto hotelAvailabilityResult) {

    String currency =
        hotelAvailabilityResult.getRoomTypes().get(0).getRoomRates().get(0).getCurrency();
    var minPriceList = hotelAvailabilityResult.getRoomTypes().stream()
        .map(roomType -> roomType.getRoomRates().stream().map(RoomRateInfoDto::getTotalPrice)
            .min(Comparator.naturalOrder()).get()
        ).toList();
    return new Cost(minPriceList.stream().reduce(BigDecimal.ZERO, BigDecimal::add), currency);
  }

  @Named("mapTotalNumberOfHotels")
  default Integer mapTotalNumberOfHotels(MultiAvailabilityResponseDto multiHotelAvailabilityDto) {
    return multiHotelAvailabilityDto.getHotelAvailabilityResults().size();
  }

  HotelAvailabilityByIdsV2 toAvailabilityByIdsV2Model(AvailabilityByIdsResponseV2Dto hotelAvailabilityByIds);
}

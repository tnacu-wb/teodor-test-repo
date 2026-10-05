package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AvailabilityByIdsResponseV2Dto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelAvailabilityResultDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelAvailabilityResultV2Dto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MultiAvailabilityResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MultiAvailabilityResponseV2Dto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomRateInfoDto;
import uk.co.whitbread.reservation.domain.model.availability.out.HotelAvailability;
import uk.co.whitbread.reservation.domain.model.availability.out.HotelAvailabilityByIds;
import uk.co.whitbread.reservation.domain.model.availability.out.HotelAvailabilityByIdsV2;
import uk.co.whitbread.reservation.domain.model.availability.out.MultiAvailabilityResponse;
import uk.co.whitbread.reservation.domain.model.srp.out.Cost;
import uk.co.whitbread.reservation.domain.model.srp.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.reservation.domain.model.srp.out.HotelAvailabilityResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out.HotelAvailabilityByIdsDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out.HotelAvailabilityDto;

@Mapper(componentModel = "spring")
public interface HotelAvailabilityMapper {

  HotelAvailability toModel(HotelAvailabilityDto hotelAvailabilityOhipDto);

  HotelAvailabilityByIds toAvailabilityByIdsModel(
      HotelAvailabilityByIdsDto hotelAvailabilityByIdsOhipDto);

  MultiAvailabilityResponse toMultiAvaDomainModel(
      MultiAvailabilityResponseDto multiHotelAvailabilityOhip);

  HotelAvailabilitiesResponse toHotelAvaResponseModel(
      MultiAvailabilityResponseDto multiHotelAvailabilityDto);

  HotelAvailabilitiesResponse toNewHotelAvaResponseModel(MultiAvailabilityResponseV2Dto multiAvailabilityResponseV2Dto);

  default List<HotelAvailabilityResponse> toMultiHotelAvaResponseModel(
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
                .netTotal(toLowestRoomRateModel(hotelAvailabilityResult).getNetTotal())
                .currencyCode(toLowestRoomRateModel(hotelAvailabilityResult).getCurrencyCode())
                .build() : null)
            .build());
      });
    }
    return hotelAvailabilityResponseList;
  }

  default List<HotelAvailabilityResponse> toNewOperaResponseModel(
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

  default Cost toLowestRoomRateModel(HotelAvailabilityResultDto hotelAvailabilityResult) {

    String currency =
        hotelAvailabilityResult.getRoomTypes().get(0).getRoomRates().get(0).getCurrency();
    var minPriceList = hotelAvailabilityResult.getRoomTypes().stream()
        .map(roomType -> roomType.getRoomRates().stream().map(RoomRateInfoDto::getTotalPrice)
            .min(Comparator.naturalOrder()).get()
        ).toList();
    return new Cost(minPriceList.stream().reduce(BigDecimal.ZERO, BigDecimal::add), currency);
  }

  default Integer toTotalNumberOfHotelsModel(MultiAvailabilityResponseDto multiHotelAvailabilityDto) {
    return multiHotelAvailabilityDto.getHotelAvailabilityResults().size();
  }

  HotelAvailabilityByIdsV2 toAvailabilityByIdsV2Model(AvailabilityByIdsResponseV2Dto hotelAvailabilityByIds);
}

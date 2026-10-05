package uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.domain.constants.HotelEntityConstants;
import uk.co.whitbread.domain.exceptions.ErrorCode;
import uk.co.whitbread.domain.model.srp.out.Cost;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilityResponse;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.exception.AvailabilityCacheV1Exception;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out.AvailableCostsDistrDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out.HotelAvailabilitiesDistrDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out.RatePlanOperaDistrDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out.RoomOperaDistrDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.HotelAvailabilitiesDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.HotelOperaDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.RatePlanOperaDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.RoomOperaDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.TotalCostDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AvailabilityCacheV1ResponseMapper {

  @Mapping(target = "hotelAvailabilityList", source = "hotelAvailabilitiesDto",
      qualifiedByName = "getListOfAvailableHotels")
  HotelAvailabilitiesResponse toModel(HotelAvailabilitiesDto hotelAvailabilitiesDto);

  HotelAvailabilitiesDto toHotelAvailabilitiesDto(HotelAvailabilitiesDistrDto hotelAvailabilitiesDistrDto);

  @Mapping(target = "code", source = "ratePlanOperaDistrDto.ratePlanCode")
  @Mapping(target = "rooms", source = "ratePlanOperaDistrDto.rooms",
      qualifiedByName = "toRoomOperaDtoList")
  RatePlanOperaDto toRatePlanOperaDto(RatePlanOperaDistrDto ratePlanOperaDistrDto);

  @Named("getListOfAvailableHotels")
  default List<HotelAvailabilityResponse> getListOfAvailableHotels(
      HotelAvailabilitiesDto hotelAvailabilitiesDto) {
    return mapOperaHotels(hotelAvailabilitiesDto);
  }

  @Named("toRoomOperaDtoList")
  default List<List<RoomOperaDto>> toRoomOperaDtoList(List<RoomOperaDistrDto> distrRooms) {
    if (distrRooms == null || distrRooms.isEmpty()) {
      return Collections.emptyList();
    }

    List<RoomOperaDto> roomList = new ArrayList<>();
    distrRooms.forEach(distrRoom -> {
      Optional<AvailableCostsDistrDto> availableCost = Optional.ofNullable(distrRoom.getAvailableCosts())
          .filter(l -> !l.isEmpty())
          .map(l -> l.get(0));
      RoomOperaDto roomOpera = RoomOperaDto.builder()
          .type(distrRoom.getRoomType())
          .cotRequired(distrRoom.getCotRequired())
          .qtyRequested(distrRoom.getQtyRequested())
          .quantityAvailable(availableCost.map(AvailableCostsDistrDto::getQtyAvailable).orElse(0))
          .totalCost(TotalCostDto.builder()
              .amount(availableCost.map(AvailableCostsDistrDto::getAmount).orElse(BigDecimal.ZERO))
              .currency(availableCost.map(AvailableCostsDistrDto::getCurrency).orElse(""))
              .build())
          .build();
      roomList.add(roomOpera);
    });

    return List.of(roomList);
  }

  default List<HotelAvailabilityResponse> mapOperaHotels(
      HotelAvailabilitiesDto hotelAvailabilitiesDto) {
    List<HotelAvailabilityResponse> hotelAvailabilityResponseList = new ArrayList<>();

    List<HotelOperaDto> operaHotelsList = hotelAvailabilitiesDto.getOperaHotelAvailabilities();
    if (!operaHotelsList.isEmpty()) {
      hotelAvailabilitiesDto.getOperaHotelAvailabilities().forEach(operaHotel -> {
        var lowestRoomRate = getLowestRoomRateForOpera(operaHotel);
        hotelAvailabilityResponseList.add(HotelAvailabilityResponse.builder()
            .hotelId(operaHotel.getHotelCode())
            .name(operaHotel.getHotelName())
            .available(operaHotel.getAvailable())
            .limitedAvailability(operaHotel.getLimitedAvailability())
            .lowestRoomRate(lowestRoomRate != null
                ? Cost.builder()
                .netTotal(lowestRoomRate)
                .currencyCode(
                    operaHotel.getRates().get(0).getRooms().get(0).get(0).getTotalCost().getCurrency())
                .build() : null)
            .pmsSource(operaHotel.getPmsSource())
            .cellCode(operaHotel.getCellCode())
            .hasMlosRestriction(operaHotel.getHasMlosRestriction())
            .numberOfRoomsAvailable(operaHotel.getNumberOfRoomsAvailable())
            .build());
      });
    }
    return hotelAvailabilityResponseList;
  }


  default BigDecimal getLowestRoomRateForOpera(HotelOperaDto operaHotelDto) {
    if (!operaHotelDto.getRates().isEmpty()) {
      Map<String, BigDecimal> minimumRoomRatesList = new HashMap<>();
      operaHotelDto.getRates().forEach(operaRate -> {
        List<BigDecimal> lowestRoomRates = new ArrayList<>();
        operaRate.getRooms().forEach(operaRoom -> {
          BigDecimal minCost = operaRoom.stream()
              .map(roomOperaDto -> roomOperaDto.getTotalCost().getAmount()
                  .multiply(BigDecimal.valueOf(roomOperaDto.getQtyRequested())))
                  .reduce(BigDecimal.ZERO, BigDecimal::add);
          lowestRoomRates.add(minCost);
        });
        BigDecimal sumOfLowestRoomRates = lowestRoomRates.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        minimumRoomRatesList.put(operaRate.getCode(), sumOfLowestRoomRates);
      });
      BigDecimal minRate = minimumRoomRatesList.values().stream()
              .min(Comparator.naturalOrder())
              .orElseThrow(() -> new AvailabilityCacheV1Exception(ErrorCode.DIGITAL_LOWEST_ROOM_RATE_EXCEPTION,
                        "Error while trying to get the lowestRoomRate"));
      String ratePlanCodeForMinRate = minimumRoomRatesList.keySet()
              .stream()
              .filter(Objects::nonNull)
              .filter(key -> minRate.equals(minimumRoomRatesList.get(key)))
              .findFirst().orElse(StringUtils.EMPTY);
      if (StringUtils.equalsIgnoreCase(HotelEntityConstants.EMPLOYEE_RATE_CODE, ratePlanCodeForMinRate)) {
        operaHotelDto.setCellCode(HotelEntityConstants.EMPLOYEE_RATE_PLAN_CODE);
      }
      return minRate;
    } else {
      return null;
    }
  }
}

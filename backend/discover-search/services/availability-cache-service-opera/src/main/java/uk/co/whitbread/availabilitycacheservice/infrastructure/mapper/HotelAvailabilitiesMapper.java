package uk.co.whitbread.availabilitycacheservice.infrastructure.mapper;

import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Room;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.HotelDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.OperaHotelAvailabilitiesDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.OperaHotelDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.OperaRatePlanDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.OperaRoomDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.PriceDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.RatePlanDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.RoomDto;

@Slf4j
@Component
public final class HotelAvailabilitiesMapper {

  public static final String PMS_SOURCE_OPERA = "OPERA";
  public static final String PMS_SOURCE_BART = "BART";

  private HotelAvailabilitiesMapper() {
    //adding a private constructor to hide the implicit public constructor
  }

  public static List<HotelDto> mapHotelToHotelDto(final List<Hotel> hotels) {
    log.trace("Mapping Hotel to HotelDto");
    if (CollectionUtils.isEmpty(hotels)) {
      return Collections.emptyList();
    }

    final List<HotelDto> hotelDtos = new ArrayList<>();

    hotels.forEach(hotel -> {
      final var hotelDto = new HotelDto();
      hotelDto.setHotelCode(hotel.getHotelCode());
      hotelDto.setHotelName(hotel.getHotelName());
      hotelDto.setHotelBrand(hotel.getHotelBrand());
      hotelDto.setAvailable(hotel.getAvailable());
      hotelDto.setLimitedAvailability(hotel.getLimitedAvailability());
      hotelDto.setArrivalDateToday(hotel.getArrivalDateToday());
      hotelDto.setEuroCurrencyHotel(hotel.getEuroCurrencyHotel());
      hotelDto.setPmsSource(PMS_SOURCE_BART);

      final List<RatePlanDto> rates = mapRatePlanToRatePlanDto(
          Optional.ofNullable(hotel.getRates()));
      hotelDto.setRates(rates);
      hotelDtos.add(hotelDto);
    });
    return hotelDtos;
  }


  private static List<RatePlanDto> mapRatePlanToRatePlanDto(
      final Optional<List<RatePlan>> ratePlans) {
    log.trace("Mapping ratePlan to ratePlanDto");
    final List<RatePlanDto> ratePlanDtos = new ArrayList<>();

    if (ratePlans.isPresent()) {
      ratePlans.get().forEach(ratePlan -> {
        final var ratePlanDto = new RatePlanDto();
        PriceDto priceDto = new PriceDto(ratePlan.getTotalPrice().getAmount(),
            ratePlan.getTotalPrice().getCurrency());
        ratePlanDto.setCode(ratePlan.getCode());
        ratePlanDto.setName(ratePlan.getName());
        ratePlanDto.setClassification(ratePlan.getClassification());
        ratePlanDto.setDescription(ratePlan.getDescription());
        ratePlanDto.setOrder(ratePlan.getOrder());
        ratePlanDto.setRooms(mapRoomToRoomDto(ratePlan.getRooms()));
        ratePlanDto.setTotalCost(priceDto);
        ratePlanDtos.add(ratePlanDto);
      });
    }
    return ratePlanDtos;
  }

  private static List<RoomDto> mapRoomToRoomDto(final List<Room> rooms) {
    log.trace("Mapping roomEntity hotels to roomDto");
    final List<RoomDto> roomDtos = new ArrayList<>();
    rooms.forEach(room -> {
      final var roomDto = new RoomDto();
      roomDto.setType(room.getType());
      roomDto.setAdults(room.getAdults());
      roomDto.setChildren(room.getChildren());
      roomDto.setCotRequired(room.getCotRequired());
      roomDto.setTotalCost(new PriceDto(room.getTotalPrice().getAmount(),
          room.getTotalPrice().getCurrency()));
      roomDtos.add(roomDto);
    });
    return roomDtos;
  }

  public static List<OperaHotelDto> mapHotelToOperaHotelDto(
      final List<Hotel> hotels, final MultiValueMap<String, String> roomTypeMap) {
    log.trace("Mapping Hotel to OperaHotelDto");
    if (CollectionUtils.isEmpty(hotels)) {
      return Collections.emptyList();
    }

    final List<OperaHotelDto> operaHotelDtos = new ArrayList<>();

    hotels.forEach(hotel -> {
      final var operaHotelDto = new OperaHotelDto();
      operaHotelDto.setHotelCode(hotel.getHotelCode());
      operaHotelDto.setHotelName(hotel.getHotelName());
      operaHotelDto.setHotelBrand(hotel.getHotelBrand());
      operaHotelDto.setAvailable(hotel.getAvailable());
      operaHotelDto.setHasMlosRestriction(hotel.getHasMlosRestriction());
      operaHotelDto.setLimitedAvailability(hotel.getLimitedAvailability());
      operaHotelDto.setArrivalDateToday(hotel.getArrivalDateToday());
      operaHotelDto.setPmsSource(PMS_SOURCE_OPERA);

      final List<OperaRatePlanDto> rates = mapRatePlanToOperaRatePlanDto(
          Optional.ofNullable(hotel.getRates()), roomTypeMap);
      operaHotelDto.setRates(rates);
      operaHotelDtos.add(operaHotelDto);
    });
    return operaHotelDtos;
  }

  private static List<OperaRatePlanDto> mapRatePlanToOperaRatePlanDto(
      final Optional<List<RatePlan>> ratePlans, final MultiValueMap<String, String> roomTypeMap) {
    log.trace("Mapping ratePlan to ratePlanDto");
    final List<OperaRatePlanDto> operaRatePlanDtos = new ArrayList<>();

    if (ratePlans.isPresent()) {
      ratePlans.get().forEach(ratePlan -> {
        log.trace("for each ratePlan code- {} and classification - {}", ratePlan.getCode(),
            ratePlan.getClassification());
        final var operaRatePlanDto = new OperaRatePlanDto();
        operaRatePlanDto.setCode(ratePlan.getCode());
        operaRatePlanDto.setName(ratePlan.getName());
        operaRatePlanDto.setClassification(ratePlan.getClassification());
        operaRatePlanDto.setDescription(ratePlan.getDescription());
        operaRatePlanDto.setOrder(ratePlan.getOrder());
        log.trace("input to mapRoomToOperaRoomDto are - {} and roomTypeMap - {}", ratePlan.getRooms(),
            sanitize(roomTypeMap));
        operaRatePlanDto.setRooms(mapRoomToOperaRoomDto(ratePlan.getRooms(), roomTypeMap));
        operaRatePlanDtos.add(operaRatePlanDto);
      });
    }
    return operaRatePlanDtos;
  }

  private static List<List<OperaRoomDto>> mapRoomToOperaRoomDto(
      final List<Room> rooms, final MultiValueMap<String, String> roomTypeMap) {
    log.trace("Mapping roomEntity hotels to roomDto");

    List<List<OperaRoomDto>> operaRoomDtoListList = new ArrayList<>();
    final int roomTypeListSize = roomTypeMap.get("roomTypes").size();
    final List<String> roomQtyList = roomTypeMap.get("roomQty") != null
        ? roomTypeMap.get("roomQty") : Collections.emptyList();
    final String roomQtyStrings = roomQtyList.isEmpty() ? "0" : roomQtyList.get(0);
    final List<String> qryRequestedList =
        Arrays.stream(roomQtyStrings.split(",")).collect(Collectors.toList());
    int qryRequestedListSize = qryRequestedList.size();
    int qtyRequested = 0;

    for (int i = 0; i < roomTypeListSize; i++) {
      final String roomTypesString = roomTypeMap.get("roomTypes").get(i);

      if (i <= (qryRequestedListSize - 1)) {
        qtyRequested = Integer.valueOf(qryRequestedList.get(i));
      }

      final List<String> roomTypesList =
          Arrays.stream(roomTypesString.split(",")).collect(Collectors.toList());

      List<OperaRoomDto> operaRoomDtoList = new ArrayList<>();
      for (final String roomType : roomTypesList) {
        final Optional<Room> roomOptional =
            rooms.stream().filter(room -> room.getType().equalsIgnoreCase(roomType)).findFirst();
        if (roomOptional.isPresent()) {
          final Room roomObj = roomOptional.get();
          log.trace("For RoomType - {} ,qtyRequested - {} amd qtyAvailable - {}",
              roomObj.getType(), qtyRequested, roomObj.getQuantityAvailable());
          final var operaRoomDto = new OperaRoomDto();
          operaRoomDto.setType(roomObj.getType());
          operaRoomDto.setCotRequired(roomObj.getCotRequired());
          operaRoomDto.setTotalCost(new PriceDto(roomObj.getTotalPrice().getAmount(),
              roomObj.getTotalPrice().getCurrency()));
          if (qtyRequested > 0) {
            operaRoomDto.setQtyRequested(qtyRequested);
          }
          operaRoomDto.setQuantityAvailable(roomObj.getQuantityAvailable());
          operaRoomDto.setLimitedAvailability(roomObj.getLimitedAvailability());
          operaRoomDtoList.add(operaRoomDto);
        }
      }
      if (operaRoomDtoList.size() > 0) {
        operaRoomDtoListList.add(operaRoomDtoList);
      }

    }
    return operaRoomDtoListList;
  }

  public static OperaHotelAvailabilitiesDto mapHotelsToOperaHotelAvailabilitiesDto(
      final List<Hotel> availableHotels, final MultiValueMap<String, String> roomTypeMap) {
    log.debug("Processing processHotelsToOperaHotelAvailabilitiesDto with hotels - {}", availableHotels);
    if (availableHotels.isEmpty()) {
      log.info("availableHotels is empty so, return empty response");
      new OperaHotelAvailabilitiesDto(availableHotels.size(), Collections.emptyList());
    }

    final List<Hotel> operaAvailableHotels = availableHotels.stream()
        .filter(hotel -> hotel.getPmsSource().equals(PMS_SOURCE_OPERA))
        .collect(Collectors.toList());

    final List<OperaHotelDto> operaHotelAvailabilities =
        mapHotelToOperaHotelDto(operaAvailableHotels, roomTypeMap);

    return new OperaHotelAvailabilitiesDto(availableHotels.size(), operaHotelAvailabilities);
  }
}

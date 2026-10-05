package uk.co.whitbread.infrastructure.rest.client.availabilitycachev1;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static uk.co.whitbread.domain.model.availability.in.BookingChannel.DISTR_BOOKING_CHANNEL;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.domain.constants.HotelEntityConstants;
import uk.co.whitbread.domain.exceptions.ErrorCode;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsRequest;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.domain.model.availability.out.DailyPrice;
import uk.co.whitbread.domain.model.availability.out.HotelAvailability;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityByIds;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityByIdsV2;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityResultV2;
import uk.co.whitbread.domain.model.availability.out.PriceInfo;
import uk.co.whitbread.domain.model.availability.out.Room;
import uk.co.whitbread.domain.model.availability.out.RoomPriceBreakdown;
import uk.co.whitbread.domain.model.availability.out.RoomRate;
import uk.co.whitbread.domain.model.availability.out.RoomRateInfoV2;
import uk.co.whitbread.domain.model.availability.out.RoomRateV2;
import uk.co.whitbread.domain.model.availability.out.RoomStay;
import uk.co.whitbread.domain.model.availability.out.RoomTypeInfo;
import uk.co.whitbread.domain.model.availability.out.RoomTypeV2;
import uk.co.whitbread.domain.model.hotel.out.RoomTypesInfo;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitution;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitutionRequestDetails;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.domain.ports.primary.HotelInfoInPort;
import uk.co.whitbread.domain.ports.secondary.AvailabilityCacheV1SearchOutPort;
import uk.co.whitbread.infrastructure.rest.client.AvailabilityCacheV1Client;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.exception.NoAvailabilityException;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.mapper.AvailabilityCacheV1RequestMapper;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.mapper.AvailabilityCacheV1ResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out.AvailableCostsDistrDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out.HotelOperaDistrDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out.RatePlanOperaDistrDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.in.AvailabilityCacheRequestV1;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.HotelAvailabilitiesDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.HotelOperaDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.RatePlanOperaDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.RoomOperaDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.properties.AvailabilityProperties;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.properties.DistributionAvailabilityProperties;
import uk.co.whitbread.infrastructure.rest.client.ohip.exceptions.OhipClientException;

@Slf4j
@RequiredArgsConstructor
@Component
public class AvailabilityCacheV1SearchOutPortImpl implements AvailabilityCacheV1SearchOutPort {

  private static final String PI_CHANNEL_ID = "PI";
  public static final String ROOM_CLASS_PUBLIC_RATES_ONLY = "STANDARD";
  private static final String ACCESSIBLE_ROOM = "DIS";
  private final AvailabilityCacheV1RequestMapper availabilityCacheV1RequestMapper;
  private final AvailabilityCacheV1ResponseMapper availabilityCacheV1ResponseMapper;
  private final AvailabilityCacheV1Client availabilityCacheV1Client;
  private final HotelInfoInPort hotelInfoInPort;
  private final AvailabilityProperties properties;
  private final DistributionAvailabilityProperties distributionAvailabilityProperties;

  private static void buildRoom(RatePlanOperaDistrDto ratePlanOperaDto, String roomClass,
      ArrayList<Room> rooms, List<RoomSubstitution> substitutions,
      RoomSubstitution roomSubstitution) {
    // Add room
    ratePlanOperaDto.getRooms().stream()
        .filter(r -> r.getRoomType().equals(roomSubstitution.getType()))
        .findFirst()
        .ifPresent(room -> {
          var totalCost = room.getAvailableCosts().stream()
              .map(AvailableCostsDistrDto::getAmount)
              .reduce(BigDecimal.ZERO, BigDecimal::add);

          var roomPriceBreakdownBuilder = RoomPriceBreakdown.builder();
          roomPriceBreakdownBuilder.totalNetAmount(totalCost);
          roomPriceBreakdownBuilder.currencyCode(room.getAvailableCosts().getFirst().getCurrency());

          room.getAvailableCosts().forEach(c -> {
            c.setQtyAvailable(c.getQtyAvailable() - 1);

            roomPriceBreakdownBuilder.dailyPrice(DailyPrice.builder()
                .netPrice(c.getAmount())
                .date(c.getDate())
                .build());
          });

          RoomSubstitution substitution = substitutions.stream()
              .filter(s -> s.getType().equals(room.getRoomType()))
              .findFirst().get();
          rooms.add(Room.builder()
              .pmsRoomType(room.getRoomType())
              .roomClass(roomClass)
              .silentSubstitution(substitution
                  .getSilent())
              .specialRequests(Collections.singletonList(substitution.getSpecialRequest()))
              .roomPriceBreakdown(roomPriceBreakdownBuilder.build())
              .build());
        });
  }

  /**
   * Builds an accessible room if it is not already present in the list of rooms.
   * This method is deprecated and should be replaced with a more robust solution.
   * WARNING: This method is deprecated and will be removed in future versions.
   * please use the buildRoom() and we need to refactor to bring the buildAccessibleRoom()
   * changes in the buildRoom()
   *
   * @param ratePlanOperaDto The rate plan containing room information.
   * @param roomClass        The class of the room.
   * @param rooms            The list of rooms to which the accessible room will be added.
   * @param substitutions    The list of room substitutions available.
   * @param roomSubstitution The specific room substitution to check for accessibility.
   */
  @Deprecated(since = "1.0", forRemoval = true)
  private static void buildAccessibleRoom(RatePlanOperaDistrDto ratePlanOperaDto, String roomClass,
      ArrayList<Room> rooms, List<RoomSubstitution> substitutions,
      RoomSubstitution roomSubstitution) {
    var specialAccessibleRoom = rooms.stream()
        .filter(r -> r.getSpecialRequests() != null
            && roomSubstitution.getAccessibleSpecialRequest() != null
            && r.getSpecialRequests().contains(roomSubstitution.getAccessibleSpecialRequest()))
        .findFirst()
        .isEmpty();

    // Only add if the existing room has different accessible special requests
    // otherwise it means room was already found
    if (specialAccessibleRoom) {
      // Add room
      ratePlanOperaDto.getRooms().stream()
          .filter(r -> r.getRoomType().equals(roomSubstitution.getType()))
          .findFirst()
          .ifPresent(room -> {

            var totalCost = room.getAvailableCosts().stream()
                .map(AvailableCostsDistrDto::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            var roomPriceBreakdownBuilder = RoomPriceBreakdown.builder();
            roomPriceBreakdownBuilder.totalNetAmount(totalCost);
            if (!CollectionUtils.isEmpty(room.getAvailableCosts())) {
              roomPriceBreakdownBuilder.currencyCode(room.getAvailableCosts().getFirst().getCurrency());
            } else {
              log.warn("Unable to set the currencyCode, as room.getAvailableCosts() found empty list");
            }

            // Subtract 1 available unit from the roomTypeAvailability list
            // ONLY if this is the first room
            // Otherwise this rooms will be used as an alternative
            if (rooms.isEmpty()) {
              room.getAvailableCosts().forEach(c -> {
                c.setQtyAvailable(c.getQtyAvailable() - 1);

                roomPriceBreakdownBuilder.dailyPrice(DailyPrice.builder()
                    .netPrice(c.getAmount())
                    .date(c.getDate())
                    .build());
              });
            }

            rooms.add(Room.builder()
                .pmsRoomType(room.getRoomType())
                .roomClass(roomClass)
                .silentSubstitution(substitutions.stream()
                    .filter(s -> s.getType().equals(room.getRoomType()))
                    .findFirst().get()
                    .getSilent())
                    .specialRequests(Collections.singletonList(roomSubstitution.getSpecialRequest()))
                    .roomPriceBreakdown(roomPriceBreakdownBuilder.build())
                .build());
          });
    }
  }

  private static List<RoomSubstitution> getValidSubstitutions(
      RoomSubstitutionRuleResponse substitutionResponse, List<String> roomTypes,
      RatePlanOperaDistrDto ratePlanOperaDto) {
    return substitutionResponse.getSubstitutionList().stream()
        .filter(s -> ratePlanOperaDto.getRooms().stream()
            .anyMatch(r -> r.getRoomType().equals(s.getType())
                && r.getAvailableCosts().stream().allMatch(c -> c.getQtyAvailable() > 0)
                && roomTypes.contains(r.getRoomType())))
        .toList();
  }

  @Override
  public HotelAvailabilityByIds getHotelAvailabilityByIdsFromAvCache(
      HotelAvailabilityByIdsRequest hotelAvailabilityByIdsRequest,
      List<RoomSubstitutionRuleResponse> roomSubstitutionRuleResponses) {
    log.debug(
        "Entered getHotelAvailabilityByIdsFromAvCache with request={}",
        hotelAvailabilityByIdsRequest);
    var availabilityCacheRequestV1 = availabilityCacheV1RequestMapper.toDtoByIds(
        hotelAvailabilityByIdsRequest);

    availabilityCacheRequestV1.setRoomTypesList(List.of(
            distributionAvailabilityProperties.getAccAvailabilityRoomTypes()));
    var rateList = distributionAvailabilityProperties.getAccAvailabilityRates();
    var hotelAvailabilitiesResponse =
            availabilityCacheV1Client.getAvailabilitiesResponseFromCacheV1Distr(availabilityCacheRequestV1, rateList);

    log.debug("Availabilities from AvCache: {}",
            hotelAvailabilitiesResponse);

    // Filter hotels with no availability to improve performance
    var availableHotelIds = hotelAvailabilitiesResponse.getOperaHotelAvailabilities()
        .stream()
        .filter(this::filterNoAvailabilityHotels)
        .map(HotelOperaDistrDto::getHotelCode)
        .toList();

    // Cacheable
    var operaRoomTypes = hotelInfoInPort
        .getRoomTypesInfo(availableHotelIds);

    var response = HotelAvailabilityByIds.builder();

    try {
      return response
          .hotelAvailability(
              hotelAvailabilitiesResponse.getOperaHotelAvailabilities()
                  .stream()
                  .filter(availability -> availableHotelIds.contains(availability.getHotelCode()))
                  .map(r ->
                      mapToDomainResponse(hotelAvailabilityByIdsRequest,
                          roomSubstitutionRuleResponses,
                          operaRoomTypes, r))
                  .toList())
          .build();
    } catch (NoAvailabilityException e) {
      return response.hotelAvailability(new ArrayList<>()).build();
    }
  }

  @Override
  public HotelAvailabilityByIdsV2 getHotelAvailabilityByIdsV2FromAvCache(
      HotelAvailabilityByIdsV2Request hotelAvailabilityByIdsV2Request,
      List<RoomSubstitutionRuleResponse> roomSubstitutionRuleResponses) {
    log.debug(
        "Entered getHotelAvailabilityByIdsFromAvCache with request={}",
        hotelAvailabilityByIdsV2Request);
    var availabilityCacheRequestV1 = availabilityCacheV1RequestMapper.toDtoByIdsV2(
        hotelAvailabilityByIdsV2Request);

    List<String> roomTypesV2 = getRoomTypesFromRoomSubstitutionRuleResponses(roomSubstitutionRuleResponses);

    availabilityCacheRequestV1.setRoomTypesList(List.of(roomTypesV2));

    var hotelAvailabilitiesResponse =
            availabilityCacheV1Client.getAvailabilitiesResponseFromCacheV1Distr(
                    availabilityCacheRequestV1, Collections.emptySet());

    log.debug("Availabilities from AvCache: {}",
        hotelAvailabilitiesResponse);

    // Filter hotels with no availability to improve performance
    var availableHotelIds = hotelAvailabilitiesResponse.getOperaHotelAvailabilities()
        .stream()
        .filter(this::filterNoAvailabilityHotels)
        .map(HotelOperaDistrDto::getHotelCode)
        .toList();

    // Cacheable
    var operaRoomTypes = hotelInfoInPort
        .getRoomTypesInfo(availableHotelIds);

    var responseBuilder = HotelAvailabilityByIdsV2.builder();

    try {
      return responseBuilder
          .hotelAvailability(
              hotelAvailabilitiesResponse.getOperaHotelAvailabilities()
                  .stream()
                  .filter(availability -> availableHotelIds.contains(availability.getHotelCode()))
                  .map(r ->
                      mapToDomainResponseV2(hotelAvailabilityByIdsV2Request,
                          roomSubstitutionRuleResponses,
                          operaRoomTypes, r))
                  .filter(hotelAvailabilityResultV2 -> !CollectionUtils
                      .isEmpty(hotelAvailabilityResultV2.getRoomStays().getFirst().getRoomTypes()))
                  .toList())
          .build();
    } catch (NoAvailabilityException e) {
      return responseBuilder.hotelAvailability(new ArrayList<>()).build();
    }
  }

  private List<String> getRoomTypesFromRoomSubstitutionRuleResponses(
      List<RoomSubstitutionRuleResponse> roomSubstitutionRuleResponses) {
    return roomSubstitutionRuleResponses.stream()
        .map(RoomSubstitutionRuleResponse::getSubstitutionList)
        .flatMap(List::stream)
        .map(RoomSubstitution::getType)
        .distinct()
        .toList();
  }

  private boolean filterNoAvailabilityHotels(HotelOperaDistrDto hotelOperaDistrDto) {
    return !CollectionUtils.isEmpty(hotelOperaDistrDto.getRates())
        && hotelOperaDistrDto.getRates().stream().noneMatch(r -> r.getRooms().isEmpty());
  }

  private HotelAvailability mapToDomainResponse(HotelAvailabilityByIdsRequest request,
      List<RoomSubstitutionRuleResponse> roomSubstitutionRuleResponses,
      Map<String, RoomTypesInfo> operaRoomTypes,
      HotelOperaDistrDto hotelOperaDto) {
    return HotelAvailability.builder()
        .hotelId(hotelOperaDto.getHotelCode())
        .available(hotelOperaDto.getAvailable())
        .timestamp(Instant.now())
        .startDate(request.getArrivalDate())
        .endDate(getEndDate(hotelOperaDto))
        .roomRates(
            mapRoomRates(request, roomSubstitutionRuleResponses,
                operaRoomTypes.get(hotelOperaDto.getHotelCode()),
                hotelOperaDto.getRates()))
        .build();
  }

  private String getEndDate(HotelOperaDistrDto hotelOperaDto) {
    var room = hotelOperaDto.getRates().getFirst().getRooms().getFirst();
    return room.getAvailableCosts().getLast().getDate();
  }

  private List<RoomRate> mapRoomRates(HotelAvailabilityByIdsRequest request,
      List<RoomSubstitutionRuleResponse> roomSubstitutionRuleResponses,
      RoomTypesInfo operaRoomTypes,
      List<RatePlanOperaDistrDto> ratePlanOperaDto) {
    return ratePlanOperaDto.stream().map(r ->
        buildRoomRate(request, roomSubstitutionRuleResponses, operaRoomTypes, r)).toList();
  }

  private RoomRate buildRoomRate(HotelAvailabilityByIdsRequest request,
      List<RoomSubstitutionRuleResponse> roomSubstitutionRuleResponses,
      RoomTypesInfo operaRoomTypes,
      RatePlanOperaDistrDto ratePlanOperaDto) {
    var roomRateBuilder = RoomRate.builder()
        .ratePlanCode(ratePlanOperaDto.getRatePlanCode());

    for (int i = 0; i < request.getRoomTypes().size(); i++) {

      var roomTypeInfoBuilder = RoomTypeInfo.builder()
              .adults(request.getAdultsNumber().get(i))
              .children(request.getChildrenNumber().get(i))
              .cotRequested(request.getCotsRequired().get(i))
              .roomType(request.getRoomTypes().get(i));
      final var substitutionResponse = roomSubstitutionRuleResponses.get(i);

      createOhipRoomTypesByRoomClassMap(substitutionResponse.getSubstitutionList(),
              operaRoomTypes).forEach((roomClass, roomTypes) -> {
                List<Room> rooms = buildRooms(substitutionResponse, roomTypes, ratePlanOperaDto, roomClass);
                if (!rooms.isEmpty())  {
                  roomTypeInfoBuilder.rooms(rooms);
                }
              }
      );

      roomRateBuilder.roomType(roomTypeInfoBuilder.build());
    }

    var roomRate = roomRateBuilder.build();

    if (!roomRate.getRoomTypes().isEmpty()) {
      roomRate.setRoomTypes(
              roomRate.getRoomTypes().stream()
                      .filter(roomType -> !CollectionUtils.isEmpty(roomType.getRooms()))
                      .toList()
      );
    }

    return roomRate;
  }

  private List<Room> buildRooms(RoomSubstitutionRuleResponse substitutionResponse,
      List<String> roomTypes,
      RatePlanOperaDistrDto ratePlanOperaDto,
      String roomClass) {
    var rooms = new ArrayList<Room>();

    // Find substitution rooms that are available both in the inventory response and in the availability response
    var substitutions = getValidSubstitutions(substitutionResponse, roomTypes, ratePlanOperaDto);

    for (var roomSubstitution : substitutions) {

      // Accessible rooms can have multiple variants
      if (ACCESSIBLE_ROOM.equals(substitutionResponse.getRequestDetails().getRoomType())) {
        buildAccessibleRoom(ratePlanOperaDto, roomClass, rooms, substitutions, roomSubstitution);
      } else {
        buildRoom(ratePlanOperaDto, roomClass, rooms, substitutions, roomSubstitution);
        break;
      }
    }

    return rooms;
  }

  private Map<String, List<String>> createOhipRoomTypesByRoomClassMap(
      List<RoomSubstitution> roomSubstitutions,
      RoomTypesInfo roomTypesInfo) {

    return roomTypesInfo.getRoomType().stream()
        .filter(roomTypeInfo -> roomSubstitutions
            .stream().map(RoomSubstitution::getType)
            .anyMatch(roomType -> roomType.equals(roomTypeInfo.getRoomType())))
        .collect(groupingBy(uk.co.whitbread.domain.model.hotel.out.RoomTypeInfo::getRoomClass,
            mapping(uk.co.whitbread.domain.model.hotel.out.RoomTypeInfo::getRoomType, toCollection(ArrayList::new))));
  }



  /**
   * Create a MAP to keep track of the number of occurencies of roomClasses accros a LIST of roomTypes Eg. ST -> 2, PP->
   * 1 etc
   *
   * @param roomTypes The list of roomTypes from which we'll count the occurencies of roomClasses
   * @return A MAP which will hold a key value pair of roomClasses and their occurencies across the roomTypes etc.
   */
  private Map<String, Long> getRoomClassOccurrence(
      List<RoomTypeInfo> roomTypes) {

    return roomTypes
        .stream()
        .map(RoomTypeInfo::getRooms)
        .flatMap(List::stream)
        .map(Room::getRoomClass)
        .collect(Collectors.groupingBy(e -> e, Collectors.counting()));
  }

  public HotelAvailabilitiesResponse getAvailabilitiesFromAvCache(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      List<String> listOfHotelCodesByLocation,
      List<List<String>> roomSubstitutionRuleResponseList, boolean flagMlos) {
    log.debug(
        "Entered getAvailabilitiesFromAvailabilityCache with searchCriteria={}",
        hotelAvailabilitiesRequest);
    List<List<String>> roomSubstitutionDistinctResponseList = roomSubstitutionRuleResponseList.stream()
        .distinct().toList();
    var availabilityCacheRequestV1 = createAvailabilityCacheRequestV1(hotelAvailabilitiesRequest,
        listOfHotelCodesByLocation, roomSubstitutionRuleResponseList, roomSubstitutionDistinctResponseList,
        flagMlos);

    var hotelAvailabilitiesResponse = getHotelAvailabilitiesByChannel(hotelAvailabilitiesRequest,
        availabilityCacheRequestV1);

    Map<String, List<List<RoomOperaDto>>> hotelsOriginalRooms = new HashMap<>();

    if (!CollectionUtils.isEmpty(hotelAvailabilitiesResponse.getOperaHotelAvailabilities())) {
      hotelAvailabilitiesResponse.getOperaHotelAvailabilities().forEach(
          hotel -> {
            //filter out rates
            boolean empRatePlanCodeAvailable = Optional.ofNullable(hotelAvailabilitiesRequest.getRatePlanCodes())
                    .orElse(Collections.emptyList())
                    .stream()
                    .anyMatch(ratePlanCode ->
                            StringUtils.equalsIgnoreCase(HotelEntityConstants.EMPLOYEE_RATE_PLAN_CODE, ratePlanCode));
            Predicate<RatePlanOperaDto> filterClasses;
            if (empRatePlanCodeAvailable) {
              filterClasses = hotelRates -> !properties.getEmployeeRatePlanClasses()
                      .contains(hotelRates.getClassification());
            } else {
              filterClasses = hotelRates -> !properties.getRatePlanClasses()
                      .contains(hotelRates.getClassification());
            }
            var rates = hotel.getRates();
            rates.removeIf(filterClasses);
            Predicate<RatePlanOperaDto> filterRoomSize = rate -> rate.getRooms().size()
                < roomSubstitutionDistinctResponseList.size();
            rates.removeIf(filterRoomSize);
            if (rates.isEmpty()) {
              hotel.setAvailable(false);
              return;
            }

            // create a duplicate list of rooms from the first-rate plan
            List<List<RoomOperaDto>> originalRooms = rates.getFirst().getRooms().stream()
                .map(rooms -> rooms.stream().map(RoomOperaDto::new).toList())
                .toList();
            hotelsOriginalRooms.put(hotel.getHotelCode(), originalRooms);

            rates.forEach(r ->
                logicForQuantityAvailable(r, roomSubstitutionDistinctResponseList, hotel));
          });

      if (PI_CHANNEL_ID.equals(hotelAvailabilitiesRequest.getChannel())) {
        updateHotelsRoomsAvailability(hotelsOriginalRooms,
            hotelAvailabilitiesResponse.getOperaHotelAvailabilities(),
            roomSubstitutionDistinctResponseList);
      }
      log.debug("Av Cache hotels availability response = {}", hotelAvailabilitiesResponse);
    }
    return availabilityCacheV1ResponseMapper.toModel(hotelAvailabilitiesResponse);
  }

  private HotelAvailabilitiesDto getHotelAvailabilitiesByChannel(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      AvailabilityCacheRequestV1 availabilityCacheRequestV1) {
    if (DISTR_BOOKING_CHANNEL.equalsIgnoreCase(hotelAvailabilitiesRequest.getChannel())) {
      return availabilityCacheV1ResponseMapper
              .toHotelAvailabilitiesDto(availabilityCacheV1Client
                      .getAvailabilitiesResponseFromCacheV1Distr(availabilityCacheRequestV1, Collections.emptySet()));
    } else {
      return availabilityCacheV1Client.getAvailabilitiesResponseFromCacheV1(availabilityCacheRequestV1);
    }
  }

  private void updateHotelsRoomsAvailability(Map<String, List<List<RoomOperaDto>>> hotelsOriginalRooms,
      List<HotelOperaDto> hotels, List<List<String>> roomSubstitutionDistinctResponseList) {

    // Filter out hotels with no availability to improve performance
    var availableHotelsIds = hotelsOriginalRooms.keySet().stream().toList();
    if (CollectionUtils.isEmpty(availableHotelsIds)) {
      return;
    }

    try {
      final Map<String, RoomTypesInfo> operaRoomTypes = hotelInfoInPort.getRoomTypesInfo(availableHotelsIds);

      hotels.forEach(hotel -> {
        var originalRooms = hotelsOriginalRooms.get(hotel.getHotelCode());
        if (originalRooms != null) {
          hotel.getRates().forEach(rate -> {
            var roomTypesInfo = operaRoomTypes.get(hotel.getHotelCode());
            if (Boolean.TRUE.equals(hotel.getAvailable()) && roomTypesInfo != null
                && !CollectionUtils.isEmpty(roomTypesInfo.getRoomType())) {
              updateHotelRoomsAvailability(originalRooms, hotel,
                  roomSubstitutionDistinctResponseList, roomTypesInfo.getRoomType());
            }
          });
        }
      });
    } catch (OhipClientException e) {
      log.error("Error getting room types info; skipping number of available rooms computation", e);
    }
  }

  private void updateHotelRoomsAvailability(List<List<RoomOperaDto>> originalRooms,
      HotelOperaDto hotel, List<List<String>> roomSubstitutions,
      List<uk.co.whitbread.domain.model.hotel.out.RoomTypeInfo> roomTypesInfo) {

    if (roomTypesInfo == null || CollectionUtils.isEmpty(roomTypesInfo)) {
      return;
    }
    log.debug("Updating rooms availability for hotel = {}", hotel);

    // create a map of room class to set of room types from room types info
    Map<String, List<String>> roomClassToRoomTypes = roomTypesInfo.stream()
        .collect(Collectors.groupingBy(uk.co.whitbread.domain.model.hotel.out.RoomTypeInfo::getRoomClass,
            Collectors.mapping(uk.co.whitbread.domain.model.hotel.out.RoomTypeInfo::getRoomType, toList())));

    hotel.setNumberOfRoomsAvailable(null);
    for (int roomNo = 0; roomNo < originalRooms.size(); roomNo++) {
      var selectedRoomType = hotel.getRates().getFirst().getRooms().get(roomNo).getFirst().getType();
      var roomClass = roomClassToRoomTypes.entrySet().stream()
          .filter(entry -> entry.getValue().contains(selectedRoomType))
          .map(Map.Entry::getKey)
          .findFirst();
      if (roomClass.isEmpty()) {
        log.error("Room class not found for room type = {}", selectedRoomType);
        continue;
      }
      var substitutions = new HashSet<>(roomSubstitutions.get(roomNo));
      var roomTypes = roomClassToRoomTypes.get(roomClass.get());
      var availableRooms = originalRooms.get(roomNo).stream()
          .filter(room -> roomTypes.contains(room.getType()))
          .filter(room -> substitutions.contains(room.getType()))
          .mapToInt(roomOperaDto -> Math.max(roomOperaDto.getQuantityAvailable(), 0))
          .sum();
      Integer numberOfRoomsAvailable = hotel.getNumberOfRoomsAvailable();
      if (numberOfRoomsAvailable == null) {
        hotel.setNumberOfRoomsAvailable(availableRooms);
      } else {
        hotel.setNumberOfRoomsAvailable(Math.min(numberOfRoomsAvailable, availableRooms));
      }
    }
    if (originalRooms.size() > 1) {
      hotel.setNumberOfRoomsAvailable(hotel.getNumberOfRoomsAvailable() * originalRooms.size());
    }
  }

  private AvailabilityCacheRequestV1 createAvailabilityCacheRequestV1(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      List<String> listOfHotelCodesByLocation,
      List<List<String>> roomSubstitutionRuleResponseList,
      List<List<String>> roomSubstitutionDistinctResponseList,
      boolean flagMlos) {
    var availabilityCacheRequestV1 = availabilityCacheV1RequestMapper.toDto(hotelAvailabilitiesRequest);
    availabilityCacheRequestV1.setHotelCodes(listOfHotelCodesByLocation);
    availabilityCacheRequestV1.setRoomQty(mapRoomQuantity(roomSubstitutionRuleResponseList));
    availabilityCacheRequestV1.setRoomTypesList(roomSubstitutionDistinctResponseList);
    availabilityCacheRequestV1.setChannel(hotelAvailabilitiesRequest.getChannel());
    availabilityCacheRequestV1.setFlagMlos(flagMlos);

    return availabilityCacheRequestV1;
  }

  private void logicForQuantityAvailable(RatePlanOperaDto rate,
      List<List<String>> roomSubstitutionRuleResponseList, HotelOperaDto hotel) {
    List<List<RoomOperaDto>> avCacheRoomsList = rate.getRooms();
    // map with <index, list.size()>
    Map<Integer, Integer> indexAndSize = IntStream.range(0,
        avCacheRoomsList.size()).boxed()
        .collect(Collectors.toMap(Function.identity(), i -> avCacheRoomsList.get(i).size()));
    // sort map by value --> we have room list asc by size
    LinkedHashMap<Integer, Integer> sortedIndexAndSize = indexAndSize.entrySet()
        .stream()
        .sorted(Entry.comparingByValue())
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    // iterate through sorted map and apply logic for rooms availability
    for (Map.Entry<Integer, Integer> entry : sortedIndexAndSize.entrySet()) {
      roomAvailabilityLogic(entry, avCacheRoomsList, roomSubstitutionRuleResponseList, rate, hotel);
    }
  }

  private void roomAvailabilityLogic(Map.Entry<Integer, Integer> entry, List<List<RoomOperaDto>> avCacheRoomsList,
      List<List<String>> roomSubstitutionRuleResponseList, RatePlanOperaDto rate, HotelOperaDto hotel) {
    List<String> roomSubstitutionList = roomSubstitutionRuleResponseList.get(entry.getKey());
    List<RoomOperaDto> theChosenOnes = new ArrayList<>();
    List<RoomOperaDto> avCacheRooms = avCacheRoomsList.get(entry.getKey());
    List<RoomOperaDto> originalAvCacheRooms = avCacheRooms.stream().map(RoomOperaDto::new).toList();
    var originalReqQuantity = avCacheRooms.getFirst().getQtyRequested();
    for (String operaRoomType : roomSubstitutionList) {
      for (RoomOperaDto avCacheRoomType : avCacheRooms) {
        //check if we find same room type in substitution list and av cache response
        if (operaRoomType.equals(avCacheRoomType.getType())) {
          findTheChosenOnes(avCacheRoomType, rate, avCacheRooms, theChosenOnes);
        }
      }
    }
    if (avCacheRooms.getFirst().getQtyRequested() > 0) {
      log.info("Quantity requested > 0, hotel is unavailable = {}", hotel);
      hotel.setRates(Collections.emptyList());
      hotel.setAvailable(false);
      return;
    }
    theChosenOnes.forEach(chosenRoom ->
        adjustQtyRequested(chosenRoom, originalAvCacheRooms, originalReqQuantity));
    // keep in the av cache rooms list only the chosen ones, those who had availability
    // and matched with substitution rooms, all other room with be filtered out
    Predicate<RoomOperaDto> filterBestRooms =
        filterPredicate -> !(theChosenOnes.stream().map(RoomOperaDto::getType).toList())
            .contains(filterPredicate.getType());
    avCacheRooms.removeIf(filterBestRooms);
  }

  private static void adjustQtyRequested(RoomOperaDto chosenRoom,
      List<RoomOperaDto> originalAvCacheRooms, int originalReqQuantity) {
    originalAvCacheRooms.stream()
        .filter(originalRoom -> originalRoom.getType().equals(chosenRoom.getType()))
        .findFirst()
        .ifPresent(originalRoom -> chosenRoom.setQtyRequested(
            originalReqQuantity >= originalRoom.getQuantityAvailable()
                ? originalRoom.getQuantityAvailable()
                : originalReqQuantity));
  }

  private void findTheChosenOnes(RoomOperaDto avCacheRoomType, RatePlanOperaDto rate,
      List<RoomOperaDto> avCacheRooms, List<RoomOperaDto> theChosenOnes) {
    // if we need more than 0 rooms and we have available more rooms than request then
    if ((avCacheRoomType.getQtyRequested() != 0)
        && (avCacheRoomType.getQuantityAvailable() >= avCacheRoomType.getQtyRequested())) {
      var qtyReq = avCacheRoomType.getQtyRequested();
      // we decrease the quantity requested with the qty that is requested
      decreaseQuantityRequested(avCacheRooms, qtyReq);
      // we decrease the quantity available with the qty requested (saved before decrease)
      decreaseQuantityAvailablePerRoomType(avCacheRoomType, rate, qtyReq);
      theChosenOnes.add(avCacheRoomType);
    } else {
      // if we need more than 0 rooms and we have available rooms, but not matching the requested then
      if ((avCacheRoomType.getQtyRequested() != 0)
          && (avCacheRoomType.getQuantityAvailable() > 0)) {
        var qtyAvail = avCacheRoomType.getQuantityAvailable();
        // we decrease the quantity requested with the qty that is available
        decreaseQuantityRequested(avCacheRooms, qtyAvail);
        // we decrease the quantity available with the qty that is available
        decreaseQuantityAvailablePerRoomType(avCacheRoomType, rate, qtyAvail);

        theChosenOnes.add(avCacheRoomType);
      }
    }
  }

  private void decreaseQuantityAvailablePerRoomType(RoomOperaDto avCacheRoomType,
      RatePlanOperaDto rate, Integer qty) {
    rate.getRooms().forEach(roomList -> {
      roomList.stream()
          .filter(room -> (room.getType()).equals(avCacheRoomType.getType()))
          .findFirst()
          .ifPresent(prefRoom -> {
            if (prefRoom.getQuantityAvailable() > 0) {
              prefRoom.setQuantityAvailable(prefRoom.getQuantityAvailable() - qty);
            }
          });
    });
  }

  private void decreaseQuantityRequested(List<RoomOperaDto> rooms, Integer qty) {
    rooms.forEach(room -> {
      if (room.getQtyRequested() > 0) {
        room.setQtyRequested(room.getQtyRequested() - qty);
      }
    });
  }

  private LinkedList<Integer> mapRoomQuantity(List<List<String>> roomSubstitutionRuleResponseList) {
    LinkedList<Integer> countOfRoomType = new LinkedList<>();
    List<List<String>> roomSubstitutionRuleResponseListDistinctValues = roomSubstitutionRuleResponseList
        .stream()
        .distinct()
        .toList();
    for (int i = 0; i < roomSubstitutionRuleResponseListDistinctValues.size(); i++) {
      int frequencyOfRoomType = Collections.frequency(roomSubstitutionRuleResponseList,
          roomSubstitutionRuleResponseListDistinctValues.get(i));
      countOfRoomType.add(i, frequencyOfRoomType);
    }
    return countOfRoomType;
  }

  private HotelAvailabilityResultV2 mapToDomainResponseV2(HotelAvailabilityByIdsV2Request request,
      List<RoomSubstitutionRuleResponse> roomSubstitutionRuleResponses,
      Map<String, RoomTypesInfo> operaRoomTypes,
      HotelOperaDistrDto hotelOperaDto) {
    return HotelAvailabilityResultV2.builder()
        .hotelId(hotelOperaDto.getHotelCode())
        .roomStays(
            mapRoomStays(request, roomSubstitutionRuleResponses, operaRoomTypes.get(hotelOperaDto.getHotelCode()),
                hotelOperaDto.getRates()))
        .build();
  }

  private List<RoomStay> mapRoomStays(HotelAvailabilityByIdsV2Request request,
      List<RoomSubstitutionRuleResponse> roomSubstitutionRuleResponses, RoomTypesInfo roomTypesInfo,
      List<RatePlanOperaDistrDto> ratePlanOperaDistrDtos) {
    return List.of(
        RoomStay.builder()
            .roomClass(ROOM_CLASS_PUBLIC_RATES_ONLY)
            .roomTypes(request.getRooms().stream()
                .map(room -> mapRoomTypes(room, roomSubstitutionRuleResponses, roomTypesInfo, ratePlanOperaDistrDtos))
                .flatMap(List::stream)
                .toList())
            .build()
    );
  }

  private List<RoomTypeV2> mapRoomTypes(uk.co.whitbread.domain.model.availability.in.Room room,
      List<RoomSubstitutionRuleResponse> roomSubstitutionRuleResponses, RoomTypesInfo roomTypesInfo,
      List<RatePlanOperaDistrDto> ratePlanOperaDistrDtos) {

    var ruleResponse = roomSubstitutionRuleResponses.stream()
        .filter(roomSubstitutionRuleResponse -> {
          final RoomSubstitutionRequestDetails requestDetails = roomSubstitutionRuleResponse.getRequestDetails();
          return requestDetails.getRoomType().equals(room.getTag()) && requestDetails.getAdults()
              .equals(room.getAdults()) && requestDetails.getChildren().equals(room.getChildren());
        })
        .findFirst()
        .orElseThrow(() -> {
          var message = "Could not match request for room substitution";
          var exception = new NoAvailabilityException(ErrorCode.DIGITAL_NOT_MATCH_REQUEST_ROOM_SUBSTITUTION_EXCEPTION,
                  message);
          ExceptionLogger.log(log, exception);
          return exception;
        });

    var roomSubstitutions = ruleResponse.getSubstitutionList();

    var roomSubstitutionResult = roomSubstitutions.stream()
        .filter(roomSubstitution -> roomTypesInfo.getRoomType().stream()
            // .filter(roomTypeInfo -> STANDARD_ROOM_CLASS.equals(roomTypeInfo.getRoomClass()))
            .anyMatch(roomTypeInfo -> roomTypeInfo.getRoomType().equals(roomSubstitution.getType())))
        .filter(roomSubstitution -> ratePlanOperaDistrDtos.stream()
            .map(RatePlanOperaDistrDto::getRooms)
            .flatMap(List::stream)
            .anyMatch(operaRoom -> operaRoom.getRoomType().equals(roomSubstitution.getType())
                && operaRoom.getAvailableCosts().stream().allMatch(c -> c.getQtyAvailable() > 0)))
        .findFirst();
    if (roomSubstitutionResult.isEmpty()) {
      log.warn("Could not find room substitution .");
      return Collections.emptyList();
    }

    final var roomSubstitution = roomSubstitutionResult.get();

    return List.of(RoomTypeV2.builder()
        .tag(room.getTag())
        .roomType(roomSubstitution.getType())
        .roomRates(mapRoomRatesV2(roomSubstitution, ratePlanOperaDistrDtos))
        .build());

  }

  private List<RoomRateV2> mapRoomRatesV2(RoomSubstitution roomSubstitution,
      List<RatePlanOperaDistrDto> ratePlanOperaDistrDtos) {

    return ratePlanOperaDistrDtos.stream()
        .filter(ratePlanOperaDistrDto -> ratePlanOperaDistrDto.getRooms().stream()
            .anyMatch(roomOperaDistrDto -> roomSubstitution.getType().equals(roomOperaDistrDto.getRoomType())))
        .map(r -> mapRoomRate(r, roomSubstitution.getType()))
        .toList();
  }

  private RoomRateV2 mapRoomRate(RatePlanOperaDistrDto ratePlanOperaDistrDto, String roomType) {
    var roomRateBuilder = RoomRateV2.builder();

    ratePlanOperaDistrDto.getRooms().stream()
        .filter(r -> r.getRoomType().equals(roomType))
        .findFirst()
        .ifPresent(room -> {
          List<PriceInfo> priceInfos = new ArrayList<>();

          room.getAvailableCosts().forEach(c -> {
            c.setQtyAvailable(c.getQtyAvailable() - 1);

            priceInfos.add(PriceInfo.builder()
                    .amountAfterTax(c.getAmount())
                    .stayDate(LocalDate.parse(c.getDate()))
                .build());
          });

          roomRateBuilder.ratePlanCode(ratePlanOperaDistrDto.getRatePlanCode())
              .currencyCode(ratePlanOperaDistrDto.getRooms().getFirst().getAvailableCosts().getFirst().getCurrency())
              .displaySet("")
              .roomRateInfo(RoomRateInfoV2.builder()
                  .priceInfo(priceInfos)
                  .packages(Collections.emptyList())
                  .build());
        });

    return roomRateBuilder.build();
  }
}

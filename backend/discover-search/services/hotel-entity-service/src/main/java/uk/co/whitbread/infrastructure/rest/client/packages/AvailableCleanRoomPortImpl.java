package uk.co.whitbread.infrastructure.rest.client.packages;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uk.co.whitbread.domain.model.basket.out.Basket;
import uk.co.whitbread.domain.model.basket.out.BasketItem;
import uk.co.whitbread.domain.model.packages.in.PackagesRequest;
import uk.co.whitbread.domain.ports.secondary.AvailableCleanRoomOutPort;
import uk.co.whitbread.domain.ports.secondary.BasketServiceOutPort;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.KioskPreferenceCollectionDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.KioskPreferenceDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.KioskRoomDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationByBasketRefResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationByIdDto;
import uk.co.whitbread.infrastructure.PreferenceProperties;
import uk.co.whitbread.infrastructure.rest.client.OhipClient;


@Slf4j
@Component
@RequiredArgsConstructor
public class AvailableCleanRoomPortImpl implements AvailableCleanRoomOutPort {

  private static final String COTR = "COTR";
  private static final String STAY_TYPE = "STAY";
  private final PreferenceProperties preferenceProperties;
  private final OhipClient ohipClient;
  private final BasketServiceOutPort basketServiceOutPort;


  /**
   * Determines the number of available clean rooms for the given packages request by looking up
   * the basket's reservations and checking room availability against guest preferences.
   *
   * <p>If no basket reference is present or no STAY-type reservation IDs are found, returns
   * {@code 0} immediately. Otherwise, delegates to {@link #getAvailableCleanRooms} and counts
   * the rooms marked as available.
   *
   * @param packagesRequest the incoming packages request containing hotel ID and basket reference
   * @return the count of available clean rooms matching guest preferences, or {@code 0} if none
   */
  @Override
  public long availableCleanRooms(PackagesRequest packagesRequest) {
    var basketReference = packagesRequest.getBasketReference();
    if (StringUtils.isBlank(basketReference)) {
      log.debug("No basketReference present on request — skipping clean room check.");
      return 0;
    }

    List<String> reservationIds = getReservationIds(basketReference);

    if (reservationIds.isEmpty()) {
      log.debug("No STAY reservation IDs found in basket '{}'", basketReference);
      return 0;
    }

    List<AvailableRoom> availability = getAvailableCleanRooms(packagesRequest.getHotelId(),
        reservationIds);

    long result = availability.stream()
        .map(AvailableRoom::available)
        .reduce(Long.MAX_VALUE, Math::min);

    log.debug("Clean room available for hotelId={}, basketReference={}: {}",
        packagesRequest.getHotelId(), basketReference, result);

    return result == Long.MAX_VALUE ? 0 : result;
  }

  /**
   * Retrieves the list of STAY-type reservation IDs associated with the given basket reference.
   *
   * <p>Fetches the basket via {@link BasketServiceOutPort}, filters items by type {@code "STAY"},
   * and extracts non-blank source IDs. Returns an empty list if the basket is null or contains
   * no matching items.
   *
   * @param basketReference the basket reference string used to look up the basket
   * @return a non-null list of reservation IDs of type STAY
   */
  private @NonNull List<String> getReservationIds(String basketReference) {
    return Optional.ofNullable(
            basketServiceOutPort.getBasket(basketReference))
        .map(Basket::getItems)
        .filter(items -> !CollectionUtils.isEmpty(items))
        .map(items -> items.stream()
            .filter(Objects::nonNull)
            .filter(item -> STAY_TYPE.equals(item.getType()))
            .map(BasketItem::getSourceId)
            .filter(StringUtils::isNotBlank)
            .toList())
        .orElse(Collections.emptyList());
  }


  /**
   * Evaluates clean-room availability for each reservation ID in the input list.
   *
   * <p>For every reservation, the method:
   * <ol>
   *   <li>Loads reservation details from OHIP and builds a reservationId -> roomType map.</li>
   *   <li>Resolves the reservation room type; if missing/blank, availability is {@code 0}.</li>
   *   <li>Fetches vacant rooms for that room type; if none are returned, availability is {@code 0}.</li>
   *   <li>Fetches guest preferences and maps them to room-condition codes.</li>
   *   <li>Counts matching rooms using {@link #getAvailableRoomsNumber(List, Set)}.</li>
   * </ol>
   *
   * <p>If reservation info is null/empty, the method returns one {@link AvailableRoom} per input
   * reservation ID with availability {@code 0}. The method never returns {@code null}.
   *
   * @param hotelId        hotel identifier used for reservation and vacant-room lookups
   * @param reservationIds reservation IDs to evaluate
   * @return list of availability results, one entry per reservation ID
   */
  private List<AvailableRoom> getAvailableCleanRooms(String hotelId, List<String> reservationIds) {
    var reservationInfo = ohipClient.getReservationInfo(hotelId, reservationIds, false, false,
        false);

    if (reservationInfo == null || CollectionUtils.isEmpty(
        reservationInfo.getReservationByIdList())) {
      log.debug("No reservation info returned for hotelId={}, reservationIds={}", hotelId,
          reservationIds);
      return reservationIds.stream()
          .map(id -> new AvailableRoom(id, 0))
          .toList();
    }

    Map<String, String> reservationRoomTypeMap = mapReservationRoomTypes(reservationInfo);

    Map<String, List<KioskRoomDto>> roomTypeVacantRoomsMap = reservationRoomTypeMap.values().stream()
        .filter(StringUtils::isNotBlank)
        .distinct()
        .collect(Collectors.toMap(
            roomType -> roomType,
            roomType -> getKioskRoomDtos(hotelId, roomType)
        ));

    return reservationIds.stream()
        .map(reservationId -> {
          String roomType = reservationRoomTypeMap.get(reservationId);
          if (StringUtils.isBlank(roomType)) {
            log.debug("No room type found for reservationId={}", reservationId);
            return new AvailableRoom(reservationId, 0);
          }

          List<KioskRoomDto> vacantRooms = roomTypeVacantRoomsMap.getOrDefault(roomType,
              Collections.emptyList());

          if (vacantRooms.isEmpty()) {
            log.debug("No vacant rooms found for hotelId={}, roomType={}", hotelId, roomType);
            return new AvailableRoom(reservationId, 0);
          }

          Set<String> guestPreferenceValues = getRoomPreferences(hotelId, reservationId);

          long count = getAvailableRoomsNumber(vacantRooms, guestPreferenceValues);

          return new AvailableRoom(reservationId, count);

        })
        .toList();
  }

  /**
   * Builds a map of reservation ID to room type from the reservation info response.
   *
   * <p>Only reservations with a non-null ID and a non-blank room type are included. When
   * duplicate reservation IDs are present, the first entry is kept.
   *
   * @param reservationInfo the reservation response containing the list of reservations
   * @return a map from reservation ID to room type string
   */
  private Map<String, String> mapReservationRoomTypes(
      ReservationByBasketRefResponseDto reservationInfo) {
    return reservationInfo.getReservationByIdList()
        .stream()
        .filter(r -> r.getReservationId() != null)
        .filter(
            r -> r.getRoomStay() != null && StringUtils.isNotBlank(r.getRoomStay().getRoomType()))
        .collect(Collectors.toMap(
            ReservationByIdDto::getReservationId,
            r -> r.getRoomStay().getRoomType(),
            (a, b) -> a
        ));
  }

  /**
   * Counts the number of vacant rooms that match the guest's room condition preferences.
   *
   * <p>If guest preferences are provided, counts only those rooms whose housekeeping condition
   * code is present in the {@code guestPreferenceValues} set. If no preferences are provided,
   * counts rooms with no housekeeping condition code set (neutral condition).
   *
   * @param vacantRooms          the list of candidate vacant rooms to evaluate
   * @param guestPreferenceValues the set of room condition codes matching guest preferences;
   *                              empty set means no specific preferences
   * @return the count of rooms matching the guest's preferences (or neutral condition if no
   *         preferences are set)
   */
  private long getAvailableRoomsNumber(List<KioskRoomDto> vacantRooms,
      Set<String> guestPreferenceValues) {
    if (!guestPreferenceValues.isEmpty()) {
      return vacantRooms.stream()
          .filter(Objects::nonNull)
          .filter(room -> room.getHousekeeping() != null)
          .filter(room -> room.getHousekeeping().getRoomCondition() != null)
          .filter(room -> room.getHousekeeping().getRoomCondition().getRoomCondition() != null)
          .filter(room ->
              (guestPreferenceValues.contains(
                  room.getHousekeeping().getRoomCondition().getRoomCondition().getCode()))
          )
          .count();
    }
    return vacantRooms.stream()
        .filter(Objects::nonNull)
        .filter(room -> room.getHousekeeping() != null)
        .filter(room -> room.getHousekeeping().getRoomCondition() == null)
        .count();
  }

  /**
   * Fetches and resolves the guest's room preferences for a given reservation into a set of
   * room condition codes used to match against vacant rooms.
   *
   * <p>If the guest has a {@code "COTR"} (cot required) preference, each remaining preference
   * code is combined with the {@code "COTR"} suffix before mapping to a condition code
   * (e.g. {@code "HIGHFLR"} → {@code "HIGHFLRCOTR"}). Otherwise, each preference code is
   * mapped directly to its condition code via {@link PreferenceProperties#getCondition()}.
   *
   * @param hotelId       the hotel identifier
   * @param reservationId the reservation identifier whose preferences are fetched
   * @return a non-null set of room condition codes representing the guest's preferences;
   *         empty if no preferences are recorded
   */
  private @NonNull Set<String> getRoomPreferences(
      String hotelId, String reservationId) {
    // Fetch guest preferences for this reservation
    var preferencesResponse = ohipClient.fetchReservationPreferences(hotelId, reservationId);

    // Gets the guest's preference collection (e.g. "cot required")
    List<KioskPreferenceCollectionDto> preferences = Optional.ofNullable(
            preferencesResponse)
        .map(p -> p.getKioskPreferenceCollection())
        .orElse(Collections.emptyList());

    //Special Handling for Cots
    List<KioskPreferenceDto> cotsRequired = preferences.stream()
        .filter(Objects::nonNull)
        .map(KioskPreferenceCollectionDto::getKioskPreference)
        .filter(Objects::nonNull)
        .flatMap(list -> list != null ? list.stream() : java.util.stream.Stream.empty())
        .filter(Objects::nonNull)
        .filter(pref -> pref.getPreferenceValue() != null)
        .filter(kioskPref -> kioskPref.getPreferenceValue().contains(COTR))
        .toList();
    Set<String> preferencesSet;
    //If NO cots required maps preference codes to room condition codes
    if (cotsRequired.isEmpty()) {
      preferencesSet = preferences.stream()
          .filter(Objects::nonNull)
          .filter(pref -> pref.getKioskPreference() != null)
          .flatMap(pref -> pref.getKioskPreference().stream())
          .filter(Objects::nonNull)
          .map(KioskPreferenceDto::getPreferenceValue)
          .filter(Objects::nonNull)
          .map(kioskPref -> preferenceProperties.getCondition().get(kioskPref))
          .collect(Collectors.toSet());
    } else {
      //Removes COTR from preference list
      //Combines each remaining preference with "COTR" suffix
      //Example: "SS" becomes "SSCOTR" to find rooms that are both high floor and have cot capability
      log.info("The Cots are requested.....");

      preferencesSet = preferences.stream()
          .filter(Objects::nonNull)
          .filter(pref -> pref.getKioskPreference() != null)
          .flatMap(pref -> pref.getKioskPreference().stream())
          .filter(Objects::nonNull)
          .filter(prefValue -> prefValue.getPreferenceValue() != null)
          .map(KioskPreferenceDto::getPreferenceValue)
          .filter(prefValue -> !COTR.equals(prefValue)) // Removes COTR from preference list
          .map(prefValue -> preferenceProperties.getCondition()
              .get(prefValue.endsWith(COTR) ? prefValue : String.join("", prefValue, COTR)))
          .filter(Objects::nonNull)
          .collect(Collectors.toSet());
    }
    return preferencesSet;

  }

  /**
   * Fetches the list of vacant (unoccupied and clean) rooms for the given hotel and room type
   * from the OHIP kiosk API.
   *
   * <p>Returns an empty list if the response is null or contains no room details.
   *
   * @param hotelId  the hotel identifier
   * @param roomType the room type code to query vacant rooms for
   * @return a non-null list of {@link KioskRoomDto} representing vacant rooms
   */
  private @NonNull List<KioskRoomDto> getKioskRoomDtos(String hotelId, String roomType) {
    var vacantRoomsResponse = ohipClient.getVacantRooms(hotelId, roomType);
    return Optional.ofNullable(vacantRoomsResponse)
        .map(r -> r.getHotelRoomsDetails())
        .filter(Objects::nonNull)
        .map(details -> details.getRoom())
        .orElse(Collections.emptyList());
  }
}

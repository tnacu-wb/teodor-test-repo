package uk.co.whitbread.avail.business.events.infrastructure.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.co.whitbread.avail.business.events.infrastructure.entity.HotelEntity;
import uk.co.whitbread.avail.business.events.infrastructure.entity.ProcessedEventEntity;
import uk.co.whitbread.avail.business.events.infrastructure.entity.RatePlanEntity;
import uk.co.whitbread.avail.business.events.infrastructure.entity.RoomEntity;
import uk.co.whitbread.avail.business.events.infrastructure.model.BusinessEventType;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.EventHeader;

@Repository
@Slf4j
@RequiredArgsConstructor
public class HotelEntityRepository {

  public static final String SUCCESSFULLY_UPDATED
      = "Successfully updated event: {} of type: {} to DB.";

  public static final String EVENT_DISCARDED = "Event: {} for hotel: {} of type: {} "
      + "generated at timestamp: {} not updated in DB. This event will be discarded";

  public static final String PROCESSED_EVENT_ENTITY_PERSISTED = "processedEventEntityPersisted: {}";
  private final HotelEntityJpaRepository hotelEntityJpaRepository;
  private final ProcessedEventJpaRepository processedEventJpaRepository;

  @Transactional
  public void updateSummaryTotals(
      final HotelEntity hotelEntityFromService,
      final ProcessedEventEntity processedEventEntity) {
    boolean isDbUpdated = performSaveOrUpdate(hotelEntityFromService,
        BusinessEventType.SUMMARY_TOTAL);
    logEventUpdateStatus(isDbUpdated, hotelEntityFromService.getEventHeader());
    ProcessedEventEntity processedEventEntityPersisted =
        processedEventJpaRepository.save(processedEventEntity);
    log.debug(PROCESSED_EVENT_ENTITY_PERSISTED, processedEventEntityPersisted);
  }

  @Transactional
  public void updateDailyRates(
      final List<HotelEntity> hotelEntitiesFromService,
      final ProcessedEventEntity processedEventEntity) {
    performSaveOrUpdateForEntityList(
        hotelEntitiesFromService, BusinessEventType.RATE_APPLY_DAILY_RATES);
    ProcessedEventEntity processedEventEntityPersisted =
        processedEventJpaRepository.save(processedEventEntity);
    log.debug(PROCESSED_EVENT_ENTITY_PERSISTED, processedEventEntityPersisted);
  }

  @Transactional
  public void updateRateRestrictions(
      final List<HotelEntity> hotelEntitiesFromService,
      final ProcessedEventEntity processedEventEntity) {
    performSaveOrUpdateForEntityList(
        hotelEntitiesFromService, BusinessEventType.RATE_RATE_RESTRICTIONS);
    ProcessedEventEntity processedEventEntityPersisted =
        processedEventJpaRepository.save(processedEventEntity);
    log.debug(PROCESSED_EVENT_ENTITY_PERSISTED, processedEventEntityPersisted);
  }

  private void performSaveOrUpdateForEntityList(
      final List<HotelEntity> hotelEntitiesFromService, final String eventType) {
    boolean isDbUpdated = false;
    for (HotelEntity hotelEntity : hotelEntitiesFromService) {
      log.trace("hotelEntity: {}", hotelEntity);
      isDbUpdated = performSaveOrUpdate(hotelEntity, eventType);
    }
    final EventHeader eventHeader = hotelEntitiesFromService.stream()
        .findAny().get().getEventHeader();
    logEventUpdateStatus(isDbUpdated, eventHeader);
  }

  private void logEventUpdateStatus(final boolean isDbUpdated,
      final EventHeader eventHeader) {
    if (!isDbUpdated) {
      log.error(EVENT_DISCARDED,
          eventHeader.getMetadata(), eventHeader.getHotelId(),
          eventHeader.getEventName(), eventHeader.getTimestamp());
    } else {
      log.trace(SUCCESSFULLY_UPDATED, eventHeader.getMetadata(),
          eventHeader.getEventName());
    }
  }

  private boolean performSaveOrUpdate(
      final HotelEntity hotelEntityFromService,
      final String eventType) {
    boolean updateResult = false;
    final Optional<HotelEntity> hotelEntityOptional =
        hotelEntityJpaRepository.findById(hotelEntityFromService.getId());
    if (hotelEntityOptional.isEmpty()) {
      final EventHeader eventHeader = hotelEntityFromService.getEventHeader();
      log.error("Cannot find entity with id : {} "
          + "for hotel-code : {} in DB "
              + "for event : {} of type : {}",
          hotelEntityFromService.getId(), hotelEntityFromService.getHotelCode(),
          eventHeader.getMetadata(),
          eventHeader.getEventName());
    } else {
      final HotelEntity hotelEntityFromDb = hotelEntityOptional.get();
      final HotelEntity updatedHotelEntity =
          performUpdateToExistingEntity(hotelEntityFromService, hotelEntityFromDb, eventType);
      log.trace("{} updated successfully.", updatedHotelEntity);
      updateResult = true;
    }
    return updateResult;
  }

  private HotelEntity performUpdateToExistingEntity(
      final HotelEntity hotelEntityFromService,
      final HotelEntity hotelEntityFromDb,
      final String eventType) {
    switch (eventType) {
      case BusinessEventType.SUMMARY_TOTAL:
        log.debug("updating SUMMARY TOTALS Business Event ");
        updateSummaryTotalForExistingRecord(hotelEntityFromService, hotelEntityFromDb);
        break;
      case BusinessEventType.RATE_APPLY_DAILY_RATES:
        log.debug("updating APPLY DAILY RATES Business Event ");
        applyDailyRatesForExistingRecord(hotelEntityFromService, hotelEntityFromDb);
        break;
      case BusinessEventType.RATE_RATE_RESTRICTIONS:
        log.debug("updating RATE RESTRICTIONS Business Event ");
        applyRateRestrictionsForExistingRecord(hotelEntityFromService, hotelEntityFromDb);
        break;
      default:
        log.error("Invalid event type:{}", eventType);
    }
    hotelEntityFromDb.setTimeUpdated(Instant.now());
    return hotelEntityJpaRepository.save(hotelEntityFromDb);
  }

  private void updateSummaryTotalForExistingRecord(
      final HotelEntity hotelEntityFromService,
      final HotelEntity hotelEntityFromDb) {
    final List<String> roomIdsFromDb = getListOfRoomIds(hotelEntityFromDb);
    for (final RoomEntity roomEntityFromService : hotelEntityFromService.getRooms()) {
      //check if this is existing  room
      final boolean isExistingRoom = roomIdsFromDb.contains(roomEntityFromService.getId());
      if (isExistingRoom) {
        //update quantity to the existing room
        updateQuantityToExistingRoomOfExistingHotel(hotelEntityFromDb, roomEntityFromService);
      } else {
        log.error("Cannot find Room-Entity with id : {} "
                + "for hotel-code : {} in DB ",
            roomEntityFromService.getId(),
            hotelEntityFromDb.getHotelCode());
      }
    }
  }

  private void applyDailyRatesForExistingRecord(
      final HotelEntity hotelEntityFromService,
      final HotelEntity hotelEntityFromDb) {
    final List<String> rateIdsFromDb = getListOfRateIds(hotelEntityFromDb);
    for (final RatePlanEntity rateFromService : hotelEntityFromService.getRates()) {
      //check if this is existing  Rate
      final boolean isExistingRate = rateIdsFromDb.contains(rateFromService.getId());
      if (isExistingRate) {
        //update amount to the existing room
        applyDailyRatesToExistingRateOfExistingHotel(hotelEntityFromDb, rateFromService);
      }
    }
  }

  public Optional<HotelEntity> findById(final String id) {
    return hotelEntityJpaRepository.findById(id);
  }

  private void applyRateRestrictionsForExistingRecord(
      final HotelEntity hotelEntityFromService,
      final HotelEntity hotelEntityFromDb) {
    final List<String> rateIdsFromDb = getListOfRateIds(hotelEntityFromDb);
    final List<RatePlanEntity> ratesFromService = hotelEntityFromService.getRates();
    if (CollectionUtils.isNotEmpty(ratesFromService)) {
      for (final RatePlanEntity rateFromService : ratesFromService) {
        //check if this is existing  Rate
        final boolean isExistingRate = rateIdsFromDb.contains(rateFromService.getId());
        if (isExistingRate) {
          log.trace("Rate Entity with ID : {} found in DB, rate restriction event will be updated.",
                  hotelEntityFromService.getId());
          //update max, min nights
          updateRateRestrictionsToExistingRateOfExistingHotel(hotelEntityFromDb, rateFromService);
        }
      }
    }
  }

  private void updateQuantityToExistingRoomOfExistingHotel(
      final HotelEntity existingHotelEntity,
      final RoomEntity roomEntityFromService) {
    final Optional<RoomEntity> existingRoomEntityOpt = existingHotelEntity
            .getRooms()
            .stream()
            .filter(roomEntity ->
                roomEntity.getId().equalsIgnoreCase(roomEntityFromService.getId()))
            .findFirst();
    if (existingRoomEntityOpt.isPresent()) {
      RoomEntity existingRoomEntity = existingRoomEntityOpt.get();
      existingRoomEntity.setQuantity(roomEntityFromService.getQuantity());
    }
  }

  private void applyDailyRatesToExistingRateOfExistingHotel(
      final HotelEntity hotelEntityFromDb,
      final RatePlanEntity ratePlanEntityFromService) {

    hotelEntityFromDb.getRates().stream().filter(ratePlanEntity -> ratePlanEntity.getId()
        .equalsIgnoreCase(ratePlanEntityFromService.getId()))
        .collect(Collectors.toList())
        .forEach(ratePlanEntity -> {
          ratePlanEntity.setAmount(ratePlanEntityFromService.getAmount());
          ratePlanEntity.setPremiumAmount(ratePlanEntityFromService.getPremiumAmount());
        });
  }

  private void updateRateRestrictionsToExistingRateOfExistingHotel(
      final HotelEntity hotelEntityFromDb,
      final RatePlanEntity ratePlanEntityFromService) {

    hotelEntityFromDb.getRates().stream().filter(ratePlanEntity -> ratePlanEntity.getId()
        .equalsIgnoreCase(ratePlanEntityFromService.getId()))
        .collect(Collectors.toList())
        .forEach(ratePlanEntity -> {
          ratePlanEntity.setMinNights(ratePlanEntityFromService.getMinNights());
          ratePlanEntity.setMaxNights(ratePlanEntityFromService.getMaxNights());
        });
  }


  private List<String> getListOfRoomIds(final HotelEntity existingHotelEntity) {
    return existingHotelEntity.getRooms().stream().map(RoomEntity::getId)
        .collect(Collectors.toList());
  }

  private List<String> getListOfRateIds(final HotelEntity existingHotelEntity) {
    return existingHotelEntity.getRates().stream().map(RatePlanEntity::getId)
        .collect(Collectors.toList());
  }
}

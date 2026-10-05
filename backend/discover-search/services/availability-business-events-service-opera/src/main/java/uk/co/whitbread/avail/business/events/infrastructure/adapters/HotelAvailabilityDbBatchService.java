package uk.co.whitbread.avail.business.events.infrastructure.adapters;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.avail.business.events.domain.model.feature.FeatureFlag;
import uk.co.whitbread.avail.business.events.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.avail.business.events.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.avail.business.events.domain.ports.secondary.HotelAvailabilityDbBatchPort;
import uk.co.whitbread.avail.business.events.domain.ports.secondary.OcdAdapterOutPort;
import uk.co.whitbread.avail.business.events.infrastructure.entity.HotelEntity;
import uk.co.whitbread.avail.business.events.infrastructure.entity.ProcessedEventEntity;
import uk.co.whitbread.avail.business.events.infrastructure.mapper.ApplyDailyRatesMapper;
import uk.co.whitbread.avail.business.events.infrastructure.mapper.ProcessedEventMapper;
import uk.co.whitbread.avail.business.events.infrastructure.mapper.RateRestrictionsMapper;
import uk.co.whitbread.avail.business.events.infrastructure.mapper.SummaryTotalMapper;
import uk.co.whitbread.avail.business.events.infrastructure.model.BusinessEventType;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.EventHeader;
import uk.co.whitbread.avail.business.events.infrastructure.repository.HotelEntityRepository;
import uk.co.whitbread.avail.business.events.infrastructure.repository.ProcessedEventJpaRepository;

@Slf4j
@AllArgsConstructor
public class HotelAvailabilityDbBatchService implements HotelAvailabilityDbBatchPort {
  public static final String HOTEL_ENTITY = "hotelEntity  : {}";
  public static final String PROCESSED_EVENT_ENTITY = "processedEventEntity: {}";
  private static final int ADULTS_NO = 1;
  private final HotelEntityRepository hotelEntityRepository;
  private final ProcessedEventJpaRepository processedEventJpaRepository;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;
  private final ContentOutPort contentOutPort;
  private final OcdAdapterOutPort ocdAdapterOutPort;
  private final RateRestrictionsMapper rateRestrictionsMapper;

  @Override
  public void processDbBatchUpdate(final EventHeader eventHeader, final String appKey) {

    log.trace("eventHeader: {}", eventHeader);
    final String eventName = eventHeader.getEventName();
    log.trace("eventName: {}", eventName);
    final HotelEntity hotelEntity;
    final List<HotelEntity> hotelEntityList;
    final ProcessedEventEntity processedEventEntity;
    try {
      switch (eventName) {
        case BusinessEventType.SUMMARY_TOTAL:
          log.debug("updating summary totals business event ");
          hotelEntity = SummaryTotalMapper
              .convertSummaryTotalEventToHotelEntity(eventHeader);
          log.debug(HOTEL_ENTITY, hotelEntity);

          processedEventEntity =
              ProcessedEventMapper.convertToProcessedEventEntity(eventHeader, appKey);
          log.debug(PROCESSED_EVENT_ENTITY, processedEventEntity);

          hotelEntityRepository.updateSummaryTotals(hotelEntity, processedEventEntity);
          break;
        case BusinessEventType.RATE_APPLY_DAILY_RATES:
          log.debug("updating apply daily rates business event ");
          hotelEntityList = ApplyDailyRatesMapper.mapApplyDailyRatesEventToHotelEntity(eventHeader);
          log.debug(HOTEL_ENTITY, hotelEntityList);

          processedEventEntity =
              ProcessedEventMapper.convertToProcessedEventEntity(eventHeader, appKey);
          log.debug(PROCESSED_EVENT_ENTITY, processedEventEntity);

          applyCityTax(eventHeader, hotelEntityList);

          hotelEntityRepository.updateDailyRates(
              hotelEntityList, processedEventEntity);
          break;
        case BusinessEventType.RATE_RATE_RESTRICTIONS:
          log.debug("updating rate restrictions business event ");
          hotelEntityList = rateRestrictionsMapper.mapRateRestrictionsEventToHotelEntity(
              eventHeader);
          log.trace(HOTEL_ENTITY, hotelEntityList);
          if (!hotelEntityList.isEmpty()) {
            processedEventEntity =
                ProcessedEventMapper.convertToProcessedEventEntity(eventHeader, appKey);
            log.debug(PROCESSED_EVENT_ENTITY, processedEventEntity);

            hotelEntityRepository.updateRateRestrictions(hotelEntityList, processedEventEntity);
          }
          break;
        default:
          log.error("Received unsubscribed event:{}",
              eventHeader.getMetadata()); }
    } catch (final EntityNotFoundException e) {
      log.error("Cannot update received event : {} "
              + "as data not present in DB. This event will be discarded",
          eventHeader.getMetadata());
    }
  }

  private void applyCityTax(EventHeader eventHeader, List<HotelEntity> hotelEntityList) {

    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCityTaxUk())
        && !unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCityTaxUkFallback())) {

      var hotelsWithCityTax = contentOutPort.getHotelsWithCityTax();
      final String hotelCode = eventHeader.getHotelId();

      if (hotelsWithCityTax.contains(hotelCode)) {

        hotelEntityList.forEach(entity -> {

          List<CompletableFuture<Void>> futures = entity.getRates().stream()
              .map(rate -> CompletableFuture.runAsync(() -> {
                log.debug("Getting amount with city tax for rate: {}", rate);
                var amountAfterTax = ocdAdapterOutPort.getAmountAfterTax(entity.getHotelCode(),
                    String.valueOf(entity.getDate()),
                    String.valueOf(entity.getDate().plusDays(1)),
                    ADULTS_NO, rate.getRateCode(), rate.getRoom().getRoomType());

                rate.setAmountWithCityTax(amountAfterTax);
              }))
              .toList();

          CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        });
      }
    }
  }

  @Override
  public BigInteger getProcessedOffset(final String appKey) {
    Optional<ProcessedEventEntity> processedEventEntityOptional =
        processedEventJpaRepository.findById(appKey);

    final ProcessedEventEntity processedEventEntity =
        processedEventEntityOptional.isPresent()
            ? processedEventEntityOptional.get() : null;

    log.debug(PROCESSED_EVENT_ENTITY, processedEventEntity);

    return (processedEventEntity != null) ? processedEventEntity.getOffset() : BigInteger.ZERO;
  }

}

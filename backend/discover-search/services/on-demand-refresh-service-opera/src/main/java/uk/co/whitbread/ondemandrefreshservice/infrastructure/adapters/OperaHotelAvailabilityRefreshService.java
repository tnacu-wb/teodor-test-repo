package uk.co.whitbread.ondemandrefreshservice.infrastructure.adapters;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple4;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.*;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.HotelEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.mapper.HotelAvailabilitiesMapper;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.FeatureFlag;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.inventory.HotelInventory;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction.RateRestrictionResponse;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.DailyRates;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.LongStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperaHotelAvailabilityRefreshService implements OperaHotelAvailabilityRefreshOutPort {

    private final HotelInventoryOutPort hotelInventoryOutPort;
    private final DailyRatesOutPort dailyRatesOutPort;
    private final RateRestrictionOutPort rateRestrictionOutPort;
    private final HotelAvailabilitiesBatchOutPort dbBatchService;
    private final ContentEntityServiceOutPort contentOutPort;
    private final OcdAdapterOutPort ocdAdapterOutPort;
    private final UnleashWrapper<FeatureFlag> unleashWrapper;

    private static final int ADULTS_NO = 2;
    private static final String CHANNEL_ID = "PI";
    private static final String BRAND = "PI";
    private boolean isCityTaxUkEnabled;
    private boolean isCityTaxUkFallbackDisabled;

    @PostConstruct
    private void initFeatureFlags() {
        isCityTaxUkEnabled = unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCityTaxUk());
        isCityTaxUkFallbackDisabled = !unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCityTaxUkFallback());
    }


    @Override
    public void refreshHotelAvailabilities(final Set<String> hotelCodes, final LocalDate startDate,
                                           final LocalDate endDate) {
        log.info("Refresh hotel availabilities for hotels: {}", hotelCodes);
        log.info("Unleash Feature Flag Enabled: {}",
                isCityTaxUkEnabled);
        log.info("Unleash Fallback Feature Flag Enabled: {}",
                isCityTaxUkFallbackDisabled);

        Flux.fromIterable(hotelCodes)
                .flatMap(hotelCode -> fetchHotelData(hotelCode, startDate, endDate))
                .flatMap(tuple -> mapAndApplyCityTax(tuple, startDate, endDate))
                .collectList()
                .doOnNext(dbBatchService::persistHotelEntities)
                .doOnSuccess(
                        result -> log.info("Successfully refreshed hotel availabilities for hotels: {}", hotelCodes))
                .doOnError(error -> log.error("Error refreshing hotel availabilities for hotels: {}",
                        hotelCodes, error))
                .subscribe();
    }

    private Mono<Tuple4<HotelInventory, Map<String, DailyRates>, RateRestrictionResponse,
            String>> fetchHotelData(
            String hotelCode, LocalDate startDate, LocalDate endDate) {
        return Mono.zip(
                Mono.fromCallable(() -> hotelInventoryOutPort.getOperaHotelInventory(hotelCode, startDate, endDate)),
                Mono.fromCallable(() -> dailyRatesOutPort.getOperaDailyRates(hotelCode, startDate, endDate)),
                Mono.fromCallable(() -> rateRestrictionOutPort.getRateRestrictions(hotelCode, startDate, endDate)),
                Mono.just(hotelCode)
        );
    }

    private Flux<HotelEntity> mapAndApplyCityTax(
            Tuple4<HotelInventory, Map<String, DailyRates>, RateRestrictionResponse, String> tuple,
            LocalDate startDate, LocalDate endDate) {

        HotelInventory operaInventoryRsp = tuple.getT1();
        Map<String, DailyRates> operaDailyRatesRsp = tuple.getT2();
        RateRestrictionResponse operaLosRsp = tuple.getT3();
        String hotelCode = tuple.getT4();

        List<HotelEntity> hotelEntities = mapHotelEntities(hotelCode, startDate, endDate,
                operaInventoryRsp, operaDailyRatesRsp, operaLosRsp);

        if (isCityTaxUkEnabled && isCityTaxUkFallbackDisabled) {
            Mono<List<String>> hotelsWithCityTaxMono = contentOutPort.getHotelsWithCityTax(CHANNEL_ID, BRAND).cache();
            return hotelsWithCityTaxMono.flatMapMany(hotelsWithCityTax -> {
                if (hotelsWithCityTax.contains(hotelCode)) {
                    return applyCityTax(hotelEntities);
                } else {
                    return Flux.fromIterable(hotelEntities);
                }
            });
        }
        return Flux.fromIterable(hotelEntities);
    }

    private Flux<HotelEntity> applyCityTax(List<HotelEntity> hotelEntities) {
        return Flux.fromIterable(hotelEntities)
                .flatMap(hotelEntity ->
                        Flux.fromIterable(hotelEntity.getRates())
                                .flatMap(rate ->
                                        Mono.fromCallable(() -> ocdAdapterOutPort.getAmountAfterTax(
                                                        hotelEntity.getHotelCode(),
                                                        String.valueOf(hotelEntity.getDate()),
                                                        String.valueOf(hotelEntity.getDate().plusDays(1)),
                                                        ADULTS_NO,
                                                        rate.getRateCode(),
                                                        rate.getRoom().getRoomType()))
                                                .doOnNext(rate::setAmountWithCityTax)
                                                .thenReturn(rate) // Return the rate to ensure the stream continues
                                )
                                .collectList() // Wait for all rates to be processed
                                .thenReturn(hotelEntity) // Then return the mutated hotelEntity
                );
    }

    private static List<HotelEntity> mapHotelEntities(final String hotelCode, final LocalDate startDate,
                                                      final LocalDate endDate, final HotelInventory operaInventoryRsp
            , final Map<String, DailyRates> operaDailyRatesRsp,
                                                      final RateRestrictionResponse operaLosRsp) {
        log.debug("Executing mapHotelEntities()....");
        final long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate);

        log.info("Mapping to entities for date period:{}, start date:{}, end date:{}", numberOfDays, startDate,
                endDate);
        final List<HotelEntity> hotelEntitiesToPersist = new ArrayList<>();

        LongStream.rangeClosed(0, numberOfDays).forEach(i -> {
            final LocalDate availableDate = startDate.plusDays(i);
            final Optional<HotelEntity> hotelEntity = HotelAvailabilitiesMapper.mapToHotelAvailabilitiesEntities(
                    hotelCode, availableDate, operaInventoryRsp, operaDailyRatesRsp, operaLosRsp);
            hotelEntity.ifPresent(hotelEntitiesToPersist::add);
        });

        log.trace("Entities to persist: {}", hotelEntitiesToPersist);
        return hotelEntitiesToPersist;
    }
}

package uk.co.whitbread.avail.business.events.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.avail.business.events.infrastructure.entity.HotelEntity;
import uk.co.whitbread.avail.business.events.infrastructure.entity.ProcessedEventEntity;
import uk.co.whitbread.avail.business.events.infrastructure.entity.RatePlanEntity;
import uk.co.whitbread.avail.business.events.infrastructure.entity.RoomEntity;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.EventHeader;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.Metadata;

@ExtendWith(MockitoExtension.class)
class HotelEntityRepositoryTest {

  final String hotelCode = "LONSUT";
  final int year = LocalDate.now().getYear() + 1;
  final LocalDate localDate = LocalDate.of(year, 5, 18);
  final String hotelId = hotelCode + "_OPERA_" + localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
  final String pmsSource = "OPERA";
  final String appKey = "test_app_key";

  @Mock
  private HotelEntityJpaRepository hotelEntityJpaRepository;

  @Mock
  private ProcessedEventJpaRepository processedEventJpaRepository;

  @InjectMocks
  private HotelEntityRepository hotelEntityRepository;

  @Captor
  private ArgumentCaptor<HotelEntity> hotelEntityCaptor;

  @Test
  public void timeUpdatedShouldBeSetWhenUpdatingSummaryTotals() {
    final HotelEntity existingHotelEntity = buildExistingHotelEntity();
    final HotelEntity hotelEntityFromService = buildHotelEntityFromService();
    final ProcessedEventEntity processedEventEntity = buildProcessedEventEntity();

    when(hotelEntityJpaRepository.findById(hotelId)).thenReturn(Optional.of(existingHotelEntity));
    when(hotelEntityJpaRepository.save(any(HotelEntity.class))).thenReturn(existingHotelEntity);

    hotelEntityRepository.updateSummaryTotals(hotelEntityFromService, processedEventEntity);

    verify(hotelEntityJpaRepository, times(1)).save(hotelEntityCaptor.capture());

    final HotelEntity savedEntity = hotelEntityCaptor.getValue();
    assertNotNull(savedEntity.getTimeUpdated(), "timeUpdated should be set before save");
    assertTimeUpdatedIsRecent(savedEntity.getTimeUpdated());
  }

  @Test
  public void timeUpdatedShouldBeSetWhenUpdatingDailyRates() {
    final HotelEntity existingHotelEntity = buildExistingHotelEntityWithRates();
    final HotelEntity hotelEntityFromService = buildHotelEntityFromServiceWithRates();
    final ProcessedEventEntity processedEventEntity = buildProcessedEventEntity();

    when(hotelEntityJpaRepository.findById(hotelId)).thenReturn(Optional.of(existingHotelEntity));
    when(hotelEntityJpaRepository.save(any(HotelEntity.class))).thenReturn(existingHotelEntity);

    List<HotelEntity> hotelEntityList = new ArrayList<>();
    hotelEntityList.add(hotelEntityFromService);

    hotelEntityRepository.updateDailyRates(hotelEntityList, processedEventEntity);

    verify(hotelEntityJpaRepository, times(1)).save(hotelEntityCaptor.capture());

    final HotelEntity savedEntity = hotelEntityCaptor.getValue();
    assertNotNull(savedEntity.getTimeUpdated(), "timeUpdated should be set before save");
    assertTimeUpdatedIsRecent(savedEntity.getTimeUpdated());
  }

  @Test
  public void timeUpdatedShouldBeSetWhenUpdatingRateRestrictions() {
    final HotelEntity existingHotelEntity = buildExistingHotelEntityWithRates();
    final HotelEntity hotelEntityFromService = buildHotelEntityFromServiceWithRates();
    final ProcessedEventEntity processedEventEntity = buildProcessedEventEntity();

    when(hotelEntityJpaRepository.findById(hotelId)).thenReturn(Optional.of(existingHotelEntity));
    when(hotelEntityJpaRepository.save(any(HotelEntity.class))).thenReturn(existingHotelEntity);

    List<HotelEntity> hotelEntityList = new ArrayList<>();
    hotelEntityList.add(hotelEntityFromService);

    hotelEntityRepository.updateRateRestrictions(hotelEntityList, processedEventEntity);

    verify(hotelEntityJpaRepository, times(1)).save(hotelEntityCaptor.capture());

    final HotelEntity savedEntity = hotelEntityCaptor.getValue();
    assertNotNull(savedEntity.getTimeUpdated(), "timeUpdated should be set before save");
    assertTimeUpdatedIsRecent(savedEntity.getTimeUpdated());
  }

  @Test
  public void timeUpdatedShouldNotBeSetWhenHotelEntityNotFoundInDb() {
    final HotelEntity hotelEntityFromService = buildHotelEntityFromService();
    final ProcessedEventEntity processedEventEntity = buildProcessedEventEntity();

    when(hotelEntityJpaRepository.findById(hotelId)).thenReturn(Optional.empty());

    hotelEntityRepository.updateSummaryTotals(hotelEntityFromService, processedEventEntity);

    verify(hotelEntityJpaRepository, times(0)).save(hotelEntityCaptor.capture());
  }

  private void assertTimeUpdatedIsRecent(final Instant timeUpdated) {
    long secondsAgo = ChronoUnit.SECONDS.between(timeUpdated, Instant.now());
    assertTrue(secondsAgo < 5, "timeUpdated should be within the last 5 seconds");
  }

  private HotelEntity buildExistingHotelEntity() {
    final RoomEntity room = RoomEntity.builder()
        .id("TWIN_" + hotelId)
        .roomType("TWIN")
        .quantity(10)
        .build();

    return HotelEntity.builder()
        .id(hotelId)
        .hotelCode(hotelCode)
        .date(localDate)
        .pmsSource(pmsSource)
        .rates(new ArrayList<>())
        .rooms(new ArrayList<>(List.of(room)))
        .eventHeader(buildEventHeader())
        .build();
  }

  private HotelEntity buildExistingHotelEntityWithRates() {
    final RoomEntity room = RoomEntity.builder()
        .id("TWIN_" + hotelId)
        .roomType("TWIN")
        .quantity(10)
        .build();

    final RatePlanEntity rate = RatePlanEntity.builder()
        .id("A_" + hotelId + "_TWIN")
        .amount(BigDecimal.valueOf(100))
        .availability(true)
        .currency("G")
        .maxNights(0)
        .minNights(0)
        .premiumAmount(BigDecimal.ZERO)
        .rateClassification("A")
        .room(room)
        .build();

    return HotelEntity.builder()
        .id(hotelId)
        .hotelCode(hotelCode)
        .date(localDate)
        .pmsSource(pmsSource)
        .rates(new ArrayList<>(List.of(rate)))
        .rooms(new ArrayList<>(List.of(room)))
        .eventHeader(buildEventHeader())
        .build();
  }

  private HotelEntity buildHotelEntityFromService() {
    final RoomEntity room = RoomEntity.builder()
        .id("TWIN_" + hotelId)
        .roomType("TWIN")
        .quantity(16)
        .build();

    return HotelEntity.builder()
        .id(hotelId)
        .hotelCode(hotelCode)
        .date(localDate)
        .pmsSource(pmsSource)
        .rates(new ArrayList<>())
        .rooms(new ArrayList<>(List.of(room)))
        .eventHeader(buildEventHeader())
        .build();
  }

  private HotelEntity buildHotelEntityFromServiceWithRates() {
    final RoomEntity room = RoomEntity.builder()
        .id("TWIN_" + hotelId)
        .roomType("TWIN")
        .quantity(10)
        .build();

    final RatePlanEntity rate = RatePlanEntity.builder()
        .id("A_" + hotelId + "_TWIN")
        .amount(BigDecimal.valueOf(200))
        .availability(true)
        .currency("G")
        .maxNights(3)
        .minNights(1)
        .premiumAmount(BigDecimal.TEN)
        .rateClassification("A")
        .room(room)
        .build();

    return HotelEntity.builder()
        .id(hotelId)
        .hotelCode(hotelCode)
        .date(localDate)
        .pmsSource(pmsSource)
        .rates(new ArrayList<>(List.of(rate)))
        .rooms(new ArrayList<>(List.of(room)))
        .eventHeader(buildEventHeader())
        .build();
  }

  private EventHeader buildEventHeader() {
    return EventHeader.builder()
        .hotelId(hotelCode)
        .eventName("SUMMARY_TOTAL")
        .metadata(Metadata.builder().offset("123").uniqueEventId("Uid123").build())
        .timestamp("30-SEP-22 12.38.45.583113 PM")
        .build();
  }

  private ProcessedEventEntity buildProcessedEventEntity() {
    return ProcessedEventEntity.builder()
        .appKey(appKey)
        .receivedOn(LocalDateTime.now())
        .offset(BigInteger.valueOf(123))
        .build();
  }
}

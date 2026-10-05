package uk.co.whitbread.avail.business.events.infrastructure.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.getunleash.Unleash;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import uk.co.whitbread.avail.business.events.domain.model.feature.FeatureFlag;
import uk.co.whitbread.avail.business.events.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.avail.business.events.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.avail.business.events.domain.ports.secondary.HotelAvailabilityDbBatchPort;
import uk.co.whitbread.avail.business.events.domain.ports.secondary.OcdAdapterOutPort;
import uk.co.whitbread.avail.business.events.infrastructure.entity.HotelEntity;
import uk.co.whitbread.avail.business.events.infrastructure.entity.ProcessedEventEntity;
import uk.co.whitbread.avail.business.events.infrastructure.entity.RatePlanEntity;
import uk.co.whitbread.avail.business.events.infrastructure.entity.RoomEntity;
import uk.co.whitbread.avail.business.events.infrastructure.mapper.ApplyDailyRatesMapper;
import uk.co.whitbread.avail.business.events.infrastructure.mapper.ProcessedEventMapper;
import uk.co.whitbread.avail.business.events.infrastructure.mapper.RateRestrictionsMapper;
import uk.co.whitbread.avail.business.events.infrastructure.mapper.SummaryTotalMapper;
import uk.co.whitbread.avail.business.events.infrastructure.model.BusinessEventType;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.EventHeader;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.Metadata;
import uk.co.whitbread.avail.business.events.infrastructure.repository.HotelEntityRepository;
import uk.co.whitbread.avail.business.events.infrastructure.repository.ProcessedEventJpaRepository;

@DirtiesContext
@ExtendWith(MockitoExtension.class)
class HotelAvailabilityDbBatchServiceTest {

  final String hotelCode = "LONSUT";
  final int year = LocalDate.now().getYear() + 1;
  final LocalDate localDate1 = LocalDate.of(year, 05, 18);
  final String hotelId = hotelCode + "_" + localDate1.format(DateTimeFormatter.ISO_LOCAL_DATE);
  final String pmsSource = "Opera";
  final String TEST_APP_KEY = "test_app_key";

  @Mock
  private HotelEntityRepository hotelEntityRepository;

  @Mock
  private ProcessedEventJpaRepository processedEventJpaRepository;

  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @Mock
  private ContentOutPort contentOutPort;

  @Mock
  private OcdAdapterOutPort ocdAdapterOutPort;

  @Mock
  private RateRestrictionsMapper rateRestrictionsMapper;

  @Captor
  private ArgumentCaptor<HotelEntity> hotelEntityCaptor;

  @Captor
  private ArgumentCaptor<ProcessedEventEntity> processedEventEntityArgumentCaptor;

  private HotelAvailabilityDbBatchPort hotelAvailabilityDbBatchService;


  @BeforeEach
  public void setup() {

    var unleash = Mockito.mock(Unleash.class);
    var featureFlag = new FeatureFlag();
    unleashWrapper = Mockito.spy(new UnleashWrapper<>(unleash, featureFlag));

    hotelAvailabilityDbBatchService =
        new HotelAvailabilityDbBatchService(
            hotelEntityRepository, processedEventJpaRepository, unleashWrapper, contentOutPort,
            ocdAdapterOutPort,rateRestrictionsMapper);
  }


  @Test
  public void summaryTotalProcessDbBatchUpdateTest() {

    final HotelEntity hotelEntityFromMapper =
        buildHotelEntity(hotelId, hotelCode, localDate1);

    final ProcessedEventEntity processedEventEntityFromMapper =
        buildProcessedEventEntity();

    final EventHeader eventHeader = buildBusinessEventHeader(BusinessEventType.SUMMARY_TOTAL);

    try (MockedStatic<SummaryTotalMapper> summaryTotalMapper = mockStatic(SummaryTotalMapper.class);
        MockedStatic<ProcessedEventMapper> processedEventMapper = mockStatic(ProcessedEventMapper.class)) {
      summaryTotalMapper.when(() -> SummaryTotalMapper.convertSummaryTotalEventToHotelEntity(eventHeader))
          .thenReturn(hotelEntityFromMapper);
      processedEventMapper.when(
              () -> ProcessedEventMapper.convertToProcessedEventEntity(eventHeader, TEST_APP_KEY))
          .thenReturn(processedEventEntityFromMapper);

      hotelAvailabilityDbBatchService.processDbBatchUpdate(eventHeader, TEST_APP_KEY);

      verify(hotelEntityRepository, times(1))
          .updateSummaryTotals(
              hotelEntityCaptor.capture(),
              processedEventEntityArgumentCaptor.capture());

      final HotelEntity hotelEntityBeingPassedToRepo = hotelEntityCaptor.getValue();

      final ProcessedEventEntity processedEventEntityBeingPassedToRepo =
          processedEventEntityArgumentCaptor.getValue();

      assertEquals(hotelEntityFromMapper, hotelEntityBeingPassedToRepo);

      assertEquals(processedEventEntityFromMapper, processedEventEntityBeingPassedToRepo);
    }
  }

  private ProcessedEventEntity buildProcessedEventEntity() {

    return ProcessedEventEntity.builder()
        .appKey(TEST_APP_KEY)
        .receivedOn(LocalDateTime.now())
        .offset(BigInteger.valueOf(123))
        .build();
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void applyDailyRatesEventUpdateTest(boolean isCityTaxUkEnabled) {

    List<HotelEntity> hotelEntityListFromMapper = new ArrayList<>();

    final HotelEntity hotelEntityFromMapper =
        buildHotelEntity(hotelId, hotelCode, localDate1);

    final ProcessedEventEntity processedEventEntityFromMapper =
        buildProcessedEventEntity();

    hotelEntityListFromMapper.add(hotelEntityFromMapper);

    final EventHeader eventHeader =
        buildBusinessEventHeader(BusinessEventType.RATE_APPLY_DAILY_RATES);

    try (MockedStatic<ApplyDailyRatesMapper> applyDailyRatesMapper = mockStatic(ApplyDailyRatesMapper.class);
        MockedStatic<ProcessedEventMapper> processedEventMapper = mockStatic(ProcessedEventMapper.class)) {
      applyDailyRatesMapper.when(() -> ApplyDailyRatesMapper.mapApplyDailyRatesEventToHotelEntity(eventHeader))
          .thenReturn(hotelEntityListFromMapper);

      processedEventMapper.when(() -> ProcessedEventMapper.convertToProcessedEventEntity(eventHeader, TEST_APP_KEY))
          .thenReturn(processedEventEntityFromMapper);

      var featureFlag = new FeatureFlag();
      mockFeatureFlags(featureFlag);

      if (isCityTaxUkEnabled) {
        doReturn(true).when(unleashWrapper).isEnabled(featureFlag.getCityTaxUk());
        when(contentOutPort.getHotelsWithCityTax())
            .thenReturn(List.of(hotelCode));
        when(ocdAdapterOutPort.getAmountAfterTax(any(), any(), any(), any(), any(), any()))
            .thenReturn(BigDecimal.TEN);
      }

      hotelAvailabilityDbBatchService.processDbBatchUpdate(eventHeader, TEST_APP_KEY);

      verify(hotelEntityRepository, times(0))
          .updateSummaryTotals(
              hotelEntityCaptor.capture(),
              processedEventEntityArgumentCaptor.capture());

      verify(hotelEntityRepository, times(1))
          .updateDailyRates(hotelEntityListFromMapper, processedEventEntityFromMapper);

      if (isCityTaxUkEnabled) {
        verify(contentOutPort).getHotelsWithCityTax();
        verify(ocdAdapterOutPort, times(2))
            .getAmountAfterTax(any(), any(), any(), any(), any(), any());
      } else {
        verifyNoInteractions(contentOutPort, ocdAdapterOutPort);
      }
    }
  }

  private void mockFeatureFlags(FeatureFlag featureFlag) {

    var cityTaxUkFeature = new FeatureFlag.Feature();
    cityTaxUkFeature.setKey("cityTaxUk");
    cityTaxUkFeature.setFallback(false);
    featureFlag.setCityTaxUk(cityTaxUkFeature);

    var cityTaxUkFallbackFeature = new FeatureFlag.Feature();
    cityTaxUkFallbackFeature.setKey("cityTaxUkFallback");
    cityTaxUkFallbackFeature.setFallback(false);
    featureFlag.setCityTaxUkFallback(cityTaxUkFallbackFeature);

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
  }

  @Test
  public void rateRestrictionsEventUpdateTest() {

    List<HotelEntity> hotelEntityListFromMapper = new ArrayList<>();

    final HotelEntity hotelEntityFromMapper =
        buildHotelEntity(hotelId, hotelCode, localDate1);

    final ProcessedEventEntity processedEventEntityFromMapper = buildProcessedEventEntity();

    hotelEntityListFromMapper.add(hotelEntityFromMapper);

    final EventHeader eventHeader =
        buildBusinessEventHeader(BusinessEventType.RATE_RATE_RESTRICTIONS);

    when(rateRestrictionsMapper.mapRateRestrictionsEventToHotelEntity(eventHeader))
        .thenReturn(hotelEntityListFromMapper);

    try (MockedStatic<ProcessedEventMapper> processedEventMapper = mockStatic(ProcessedEventMapper.class)) {
      processedEventMapper.when(() -> ProcessedEventMapper.convertToProcessedEventEntity(eventHeader, TEST_APP_KEY))
          .thenReturn(processedEventEntityFromMapper);

      hotelAvailabilityDbBatchService.processDbBatchUpdate(eventHeader, TEST_APP_KEY);

      verify(hotelEntityRepository, times(0))
          .updateSummaryTotals(
              hotelEntityCaptor.capture(),
              processedEventEntityArgumentCaptor.capture());

      verify(hotelEntityRepository, times(1))
          .updateRateRestrictions(hotelEntityListFromMapper, processedEventEntityFromMapper);
    }
  }

  @Test
  public void noMethodShouldBeInvokedForInvalidEventType() {

    final EventHeader eventHeader = buildBusinessEventHeader("INVALID EVENT");

    hotelAvailabilityDbBatchService.processDbBatchUpdate(eventHeader, TEST_APP_KEY);

    verify(hotelEntityRepository, times(0))
        .updateSummaryTotals(
            hotelEntityCaptor.capture(),
            processedEventEntityArgumentCaptor.capture());
  }

  @Test
  public void getProcessedOffsetNotNullTest() {

    ProcessedEventEntity processedEventEntity = buildProcessedEventEntity();

    Optional<ProcessedEventEntity> processedEventEntityOptional =
        Optional.of(processedEventEntity);

    when(processedEventJpaRepository.findById(TEST_APP_KEY))
        .thenReturn(processedEventEntityOptional);

    BigInteger offset = hotelAvailabilityDbBatchService.getProcessedOffset(TEST_APP_KEY);

    assertEquals(processedEventEntity.getOffset(), offset);

  }

  @Test
  public void getProcessedOffsetNullTest() {

    ProcessedEventEntity processedEventEntity = null;

    Optional<ProcessedEventEntity> processedEventEntityOptional =
        Optional.ofNullable(processedEventEntity);

    when(processedEventJpaRepository.findById(TEST_APP_KEY))
        .thenReturn(processedEventEntityOptional);

    BigInteger offset = hotelAvailabilityDbBatchService.getProcessedOffset(TEST_APP_KEY);

    assertEquals(BigInteger.ZERO, offset);

  }

  private Metadata buildMetaData () {
    return Metadata.builder()
        .offset("123")
        .uniqueEventId("Uid1233456")
        .build();
  }

  private EventHeader buildBusinessEventHeader(final String eventName) {
    String receivedOn = "30-SEP-22 12.38.45.583113 PM";
    return EventHeader.builder()
        .hotelId(hotelCode)
        .eventName(eventName)
        .metadata(buildMetaData())
        .timestamp(receivedOn)
        .build();
  }

  private HotelEntity buildHotelEntity(
      final String hotelId,
      final String hotelCode,
      final LocalDate date) {

    var rate1 = new RatePlanEntity();
    rate1.setRateCode("RATE1");
    var room1 = new RoomEntity();
    room1.setRoomType("STD");
    rate1.setRoom(room1);

    var rate2 = new RatePlanEntity();
    rate2.setRateCode("RATE2");
    var room2 = new RoomEntity();
    room2.setRoomType("STD");
    rate2.setRoom(room2);

    return HotelEntity.builder()
        .id(hotelId)
        .hotelCode(hotelCode)
        .date(date)
        .pmsSource(pmsSource)
        .rates(List.of(rate1, rate2))
        .build();

  }
}

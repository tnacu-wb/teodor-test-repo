package uk.co.whitbread.ondemandrefreshservice.domain.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.adapters.HotelMigrationStatusRefreshService;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.scheduler.HotelAvailabilityOnDemandJobScheduler;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.HotelMigrationStatusEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.OnDemandProcessResponse;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.config.SchedulerProperties;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.repository.read.HotelMigrationStatusJpaRepository;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.repository.write.HotelMigrationStatusJpaRepositoryWriter;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.validator.DateFormatValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.validator.HotelIdValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ProcessRefreshHotelAvailabilityTest {

  @InjectMocks
  private ProcessRefreshHotelAvailability processRefreshHotelAvailability;

  @Mock
  private HotelAvailabilityOnDemandJobScheduler hotelAvailabilityOnDemandJobScheduler;

  @Mock
  private SchedulerProperties schedulerProperties;

  @Mock
  private DateFormatValidator dateFormatValidator;

  @Mock
  private HotelIdValidator hotelIdValidator;

  @Mock
  private HotelMigrationStatusJpaRepository hmsJpaRepositoryReader;

  @Mock
  private HotelMigrationStatusJpaRepositoryWriter hmsJpaRepositoryWriter;

  @Mock
  private HotelMigrationStatusRefreshService hmsRefreshOutPort;

  private final Set<String> hotelIds = new HashSet<>();
  private final Set<HotelMigrationStatusEntity> hotelMigrationStatusEntities = new HashSet<>();


  @BeforeEach
  void init(){
    ReflectionTestUtils.setField(processRefreshHotelAvailability, "dateFormatValidator", dateFormatValidator);
    ReflectionTestUtils.setField(dateFormatValidator, "validationProperties", schedulerProperties);

    ReflectionTestUtils.setField(processRefreshHotelAvailability, "hmsJpaRepositoryReader", hmsJpaRepositoryReader);
    ReflectionTestUtils.setField(processRefreshHotelAvailability, "hmsJpaRepositoryWriter", hmsJpaRepositoryWriter);
    ReflectionTestUtils.setField(processRefreshHotelAvailability, "hmsRefreshOutPort", hmsRefreshOutPort);
    ReflectionTestUtils.setField(schedulerProperties, "maxDays", 90);
    ReflectionTestUtils.setField(schedulerProperties, "maxRange", 365);
    ReflectionTestUtils.setField(schedulerProperties, "interval", 30);

    ReflectionTestUtils.setField(processRefreshHotelAvailability, "schedulerProperties", schedulerProperties);
  }

  @Test
  void testRefreshOnDemandWithStaleDates() {
    hotelIds.add("DUBHAI");
    HotelMigrationStatusEntity hotelMigrationStatusEntity = new HotelMigrationStatusEntity();
    hotelMigrationStatusEntity.setHotelCode("DUBHAI");
    hotelMigrationStatusEntity.setPmsSource("OPERA");
    hotelMigrationStatusEntities.add(hotelMigrationStatusEntity);
    String startDate  = LocalDate.now().toString();
    String endDate = LocalDate.now().plusDays(30).toString();
    Mockito.doReturn(hotelMigrationStatusEntities).when(hmsJpaRepositoryReader).findByPmsSource(Mockito.anyString());
    Mockito.doCallRealMethod().when(dateFormatValidator).validateInputDates(Mockito.any(), Mockito.any());
    ReflectionTestUtils.setField(schedulerProperties, "maxDays", 90);
    ReflectionTestUtils.setField(schedulerProperties, "interval", 30);
    Mockito.when(schedulerProperties.getMaxRange()).thenReturn(365);
    Mockito.when(schedulerProperties.getInterval()).thenReturn(30);
    OnDemandProcessResponse onDemandProcessResponse = processRefreshHotelAvailability
            .refreshOnDemandWithDates(hotelIds, startDate, endDate);
    assertEquals(HttpStatus.ACCEPTED, onDemandProcessResponse.getStatus());

  }
  @Test
  void testRefreshOnDemandWithDatesRanges() {
    hotelIds.add("LONDON");
    hotelIds.add("DUBHAI");
    HotelMigrationStatusEntity hotelMigrationStatusEntity = new HotelMigrationStatusEntity();
    hotelMigrationStatusEntity.setHotelCode("LONDON");
    hotelMigrationStatusEntity.setPmsSource("BART");
    hotelMigrationStatusEntity.setHotelCode("DUBHAI");
    hotelMigrationStatusEntity.setPmsSource("OPERA");
    hotelMigrationStatusEntities.add(hotelMigrationStatusEntity);
    String startDate  = LocalDate.now().toString();
    String endDate = LocalDate.now().plusDays(90).toString();
    Mockito.doReturn(hotelMigrationStatusEntities).when(hmsJpaRepositoryReader).findByPmsSource(Mockito.anyString());
    Mockito.doCallRealMethod().when(dateFormatValidator).validateInputDates(Mockito.any(), Mockito.any());
    ReflectionTestUtils.setField(schedulerProperties, "maxDays", 90);
    Mockito.when(schedulerProperties.getMaxRange()).thenReturn(365);
    ReflectionTestUtils.setField(schedulerProperties, "interval", 30);
    Mockito.when(schedulerProperties.getInterval()).thenReturn(30);
    OnDemandProcessResponse onDemandProcessResponse = processRefreshHotelAvailability
            .refreshOnDemandWithDates(hotelIds, startDate, endDate);
    assertEquals(HttpStatus.PARTIAL_CONTENT, onDemandProcessResponse.getStatus());


  }
  @Test
  void testRefreshOnDemandWithGreaterThan365Dates() {
    hotelIds.add("TKTINT");
    hotelIds.add("DUBHAI");
    HotelMigrationStatusEntity hotelMigrationStatusEntity = new HotelMigrationStatusEntity();
    hotelMigrationStatusEntity.setHotelCode("TKTINT");
    hotelMigrationStatusEntity.setPmsSource("OPERA");
    hotelMigrationStatusEntity.setHotelCode("DUBHAI");
    hotelMigrationStatusEntity.setPmsSource("OPERA");
    hotelMigrationStatusEntities.add(hotelMigrationStatusEntity);
    String startDate  = LocalDate.now().toString();
    String endDate = LocalDate.now().plusDays(390).toString();
    ReflectionTestUtils.setField(schedulerProperties, "interval", 30);
    Mockito.doCallRealMethod().when(dateFormatValidator).validateInputDates(Mockito.any(), Mockito.any());
    ReflectionTestUtils.setField(schedulerProperties, "maxDays", 90);
    Mockito.when(schedulerProperties.getMaxRange()).thenReturn(365);
    OnDemandProcessResponse onDemandProcessResponse = processRefreshHotelAvailability
            .refreshOnDemandWithDates(hotelIds, startDate, endDate);
    assertEquals(onDemandProcessResponse.getStatus(), (HttpStatus.BAD_REQUEST));
  }

  @Test
  void testRefreshOnDemandWithoutDates() {
    processRefreshHotelAvailability.refreshOnDemandWithoutDates();
    assertTrue(true);
  }

  @Test
  void testRefreshOnDemandWithHotelNotInDBOrBartHotel() {
    hotelIds.add("LONDON");
    hotelIds.add("DUBHAI");
    HotelMigrationStatusEntity hotelMigrationStatusEntity = new HotelMigrationStatusEntity();
    hotelMigrationStatusEntity.setHotelCode("LONDON");
    hotelMigrationStatusEntity.setPmsSource("OPERA");
    hotelMigrationStatusEntities.add(hotelMigrationStatusEntity);
    String startDate  = LocalDate.now().toString();
    String endDate = LocalDate.now().plusDays(90).toString();
    Mockito.doReturn(hotelMigrationStatusEntities).when(hmsJpaRepositoryReader).findByPmsSource(Mockito.anyString());
    Mockito.doCallRealMethod().when(dateFormatValidator).validateInputDates(Mockito.any(), Mockito.any());
    ReflectionTestUtils.setField(schedulerProperties, "maxDays", 90);
    Mockito.when(schedulerProperties.getMaxRange()).thenReturn(365);
    ReflectionTestUtils.setField(schedulerProperties, "interval", 30);
    Mockito.when(schedulerProperties.getInterval()).thenReturn(30);
    HotelMigrationStatusEntity hotelMigrationStatusEntity2 = new HotelMigrationStatusEntity();
    hotelMigrationStatusEntity2.setHotelCode("DUBHAI");
    hotelMigrationStatusEntity2.setPmsSource("OPERA");
    hotelMigrationStatusEntity2.setUpdatedOn(LocalDateTime.now());
    Mockito.when(hmsRefreshOutPort.getHotelMigrationStatus(Mockito.anyString())).thenReturn(hotelMigrationStatusEntity2);
    OnDemandProcessResponse onDemandProcessResponse = processRefreshHotelAvailability
        .refreshOnDemandWithDates(hotelIds, startDate, endDate);
    assertEquals(HttpStatus.ACCEPTED, onDemandProcessResponse.getStatus());
  }
}

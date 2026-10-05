package uk.co.whitbread.ondemandrefreshservice.infrastructure.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.config.OperaConstants.MIGRATION_PMS_SOURCE_OPERA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.HotelMigrationStatusEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.migrationstatus.OperaHotelDetails;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.migrationstatus.OperaHotelDetailsList;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.repository.read.HotelMigrationStatusJpaRepository;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.repository.write.HotelMigrationStatusJpaRepositoryWriter;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.migration.opera.HotelMigrationStatusClient;

@ExtendWith(MockitoExtension.class)
class HotelMigrationStatusRefreshServiceTest {

  @Mock
  private HotelMigrationStatusJpaRepository hotelMigrationStatusJpaRepository;

  @Mock
  private HotelMigrationStatusClient hotelMigrationStatusClient;

  @Mock
  private HotelMigrationStatusJpaRepositoryWriter hotelMigrationStatusJpaRepositoryWriter;

  private HotelMigrationStatusRefreshService hotelMigrationStatusRefreshService;

  @BeforeEach
  void setup() {
    hotelMigrationStatusRefreshService =
        new HotelMigrationStatusRefreshService(hotelMigrationStatusJpaRepository, hotelMigrationStatusClient,
            hotelMigrationStatusJpaRepositoryWriter);
  }

  @Test
  void shouldNotInvokeElseBlockIfRepoIsReturningEmptyList() {

    Mockito.when(hotelMigrationStatusJpaRepository.getOperaHotelCodes())
        .thenReturn(Collections.emptySet());

    hotelMigrationStatusRefreshService.refreshOperaHotelMigrationStatus();

    Mockito.verify(hotelMigrationStatusJpaRepositoryWriter, Mockito.never()).save(Mockito.any());
  }

  @Test
  void shouldInvokeElseBlockIfRepoIsReturningEmptyList() {

    final Set<String> operaHotelsExpected = new HashSet<>();
    operaHotelsExpected.add("operaHotel1");
    operaHotelsExpected.add("operaHotel2");
    Mockito.when(hotelMigrationStatusJpaRepository.getOperaHotelCodes())
        .thenReturn(operaHotelsExpected);

    List<OperaHotelDetails> hotelDetails1 = new ArrayList<>();
    final OperaHotelDetails operaHotelDetails11
        = buildOperaHotelDetails(
        "ONSALE", "TRUE", "TRUE", "operaHotel1");
    final OperaHotelDetails operaHotelDetails12
        = buildOperaHotelDetails(
        "PMS", "OPERA", "OPERA", "operaHotel1");
    hotelDetails1.add(operaHotelDetails11);
    hotelDetails1.add(operaHotelDetails12);
    OperaHotelDetailsList operaHotelDetailsList1 = OperaHotelDetailsList.builder()
        .hotelDetails(hotelDetails1)
        .build();

    Mockito.when(
            hotelMigrationStatusClient.getHotelMigrationStatus("operaHotel1"))
        .thenReturn(operaHotelDetailsList1);

    List<OperaHotelDetails> hotelDetails2 = new ArrayList<>();
    final OperaHotelDetails operaHotelDetails21
        = buildOperaHotelDetails(
        "ONSALE", "TRUE", "TRUE", "operaHotel2");
    final OperaHotelDetails operaHotelDetails22
        = buildOperaHotelDetails(
        "PMS", "OPERA", "OPERA", "operaHotel2");
    hotelDetails2.add(operaHotelDetails21);
    hotelDetails2.add(operaHotelDetails22);
    OperaHotelDetailsList operaHotelDetailsList2 = OperaHotelDetailsList.builder()
        .hotelDetails(hotelDetails2)
        .build();

    Mockito.when(
            hotelMigrationStatusClient.getHotelMigrationStatus("operaHotel2"))
        .thenReturn(operaHotelDetailsList2);

    Mockito.when(hotelMigrationStatusJpaRepositoryWriter.findById("operaHotel1"))
        .thenReturn(Optional.of(buiHotelMigrationStatusEntity("operaHotel1")));

    Mockito.when(hotelMigrationStatusJpaRepositoryWriter.findById("operaHotel2"))
        .thenReturn(Optional.of(buiHotelMigrationStatusEntity("operaHotel2")));

    hotelMigrationStatusRefreshService.refreshOperaHotelMigrationStatus();

    Mockito.verify(
            hotelMigrationStatusJpaRepositoryWriter, Mockito.times(1))
        .findById("operaHotel1");
    Mockito.verify(
            hotelMigrationStatusJpaRepositoryWriter, Mockito.times(1))
        .findById("operaHotel2");
    Mockito.verify(
            hotelMigrationStatusJpaRepositoryWriter, Mockito.times(operaHotelsExpected.size()))
        .save(Mockito.any(HotelMigrationStatusEntity.class));
  }

  @Test
  void getHotelMigrationStatusSuccess() {
    List<OperaHotelDetails> hotelDetails1 = new ArrayList<>();
    final OperaHotelDetails operaHotelDetails11
        = buildOperaHotelDetails(
        "ONSALE", "TRUE", "TRUE", "operaHotel1");
    final OperaHotelDetails operaHotelDetails12
        = buildOperaHotelDetails(
        "PMS", "OPERA", "OPERA", "operaHotel1");
    hotelDetails1.add(operaHotelDetails11);
    hotelDetails1.add(operaHotelDetails12);
    OperaHotelDetailsList operaHotelDetailsList1 = OperaHotelDetailsList.builder()
        .hotelDetails(hotelDetails1)
        .build();

    Mockito.when(
            hotelMigrationStatusClient.getHotelMigrationStatus("operaHotel1"))
        .thenReturn(operaHotelDetailsList1);

    HotelMigrationStatusEntity hotelResult = hotelMigrationStatusRefreshService.getHotelMigrationStatus("operaHotel1");
    assertEquals("operaHotel1", hotelResult.getHotelCode());
    assertEquals(MIGRATION_PMS_SOURCE_OPERA, hotelResult.getPmsSource());
    assertTrue(hotelResult.isOnSale());
  }

  @Test
  void getHotelMigrationStatusForNonOperaPms() {
    List<OperaHotelDetails> hotelDetails1 = new ArrayList<>();
    final OperaHotelDetails operaHotelDetails11
        = buildOperaHotelDetails(
        "ONSALE", "TRUE", "TRUE", "Hotel1");
    hotelDetails1.add(operaHotelDetails11);
    OperaHotelDetailsList operaHotelDetailsList1 = OperaHotelDetailsList.builder()
        .hotelDetails(hotelDetails1)
        .build();

    Mockito.when(
            hotelMigrationStatusClient.getHotelMigrationStatus("Hotel1"))
        .thenReturn(operaHotelDetailsList1);

    HotelMigrationStatusEntity hotelResult = hotelMigrationStatusRefreshService.getHotelMigrationStatus("Hotel1");
    assertEquals("Hotel1", hotelResult.getHotelCode());
    assertNotEquals(MIGRATION_PMS_SOURCE_OPERA, hotelResult.getPmsSource());
    assertTrue(hotelResult.isOnSale());
  }

  @Test
  void getHotelMigrationStatus_throwsException() {
    Mockito.when(
            hotelMigrationStatusClient.getHotelMigrationStatus("Hotel1"))
        .thenThrow(new RuntimeException());

    HotelMigrationStatusEntity hotelResult = hotelMigrationStatusRefreshService.getHotelMigrationStatus("Hotel1");
    assertNull( hotelResult);
  }

  private HotelMigrationStatusEntity buiHotelMigrationStatusEntity(final String hotelCode) {
    return HotelMigrationStatusEntity.builder()
        .pmsSource("OPERA")
        .onSale(true)
        .hotelCode(hotelCode)
        .build();
  }

  private OperaHotelDetails buildOperaHotelDetails(
      final String category, final String code, final String desc, final String hotelCode) {
    return OperaHotelDetails.builder()
        .category(category)
        .code(code)
        .description(desc)
        .hotelId(hotelCode)
        .onSale(false)
        .build();
  }


}

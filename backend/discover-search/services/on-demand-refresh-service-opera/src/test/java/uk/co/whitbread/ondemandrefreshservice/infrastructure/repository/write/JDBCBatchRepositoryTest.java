package uk.co.whitbread.ondemandrefreshservice.infrastructure.repository.write;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.HotelEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.RatePlanEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.RoomEntity;

@ExtendWith(MockitoExtension.class)
class JDBCBatchRepositoryTest {

  private static final String HOTEL_CODE = "TKINPT";
  private static final String PMS_SOURCE = "OPERA";
  private static final LocalDate AVAIL_DATE = LocalDate.of(2026, 5, 18);
  private static final String HOTEL_ID = HOTEL_CODE + "_" + PMS_SOURCE + "_" + AVAIL_DATE;

  @Mock
  private JdbcTemplate jdbcTemplate;

  @Mock
  private Connection connection;

  @Mock
  private PreparedStatement preparedStatement;

  @InjectMocks
  private JDBCBatchRepository jdbcBatchRepository;

  @Captor
  private ArgumentCaptor<PreparedStatementCreator> preparedStatementCreatorCaptor;

  @Test
  public void persistHotelAvailabilitiesShouldIncludeTimeUpdateQuery() throws Exception {
    final HotelEntity hotelEntity = buildHotelEntityWithRoomsAndRates();

    when(jdbcTemplate.update(preparedStatementCreatorCaptor.capture())).thenReturn(1);

    jdbcBatchRepository.persistHotelAvailabilities(hotelEntity);

    when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);

    final PreparedStatementCreator creator = preparedStatementCreatorCaptor.getValue();
    creator.createPreparedStatement(connection);

    ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
    verify(connection).prepareStatement(sqlCaptor.capture());

    final String executedSql = sqlCaptor.getValue();
    assertTrue(executedSql.contains("time_updated = CURRENT_TIMESTAMP"),
        "Batch SQL should include time_updated update with CURRENT_TIMESTAMP");
    assertTrue(executedSql.contains("where id = '" + HOTEL_ID + "'"),
        "Time update query should target the correct hotel ID");
  }

  @Test
  public void persistHotelAvailabilitiesShouldNotExecuteForNullEntity() throws Exception {
    jdbcBatchRepository.persistHotelAvailabilities(null);

    verify(jdbcTemplate, never()).update(any(PreparedStatementCreator.class));
  }

  @Test
  public void persistHotelAvailabilitiesShouldIncludeTimeUpdateQueryForHotelWithRoomsOnly() throws Exception {
    final HotelEntity hotelEntity = buildHotelEntityWithRoomsOnly();

    when(jdbcTemplate.update(preparedStatementCreatorCaptor.capture())).thenReturn(1);

    jdbcBatchRepository.persistHotelAvailabilities(hotelEntity);

    when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);

    final PreparedStatementCreator creator = preparedStatementCreatorCaptor.getValue();
    creator.createPreparedStatement(connection);

    ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
    verify(connection).prepareStatement(sqlCaptor.capture());

    final String executedSql = sqlCaptor.getValue();
    assertTrue(executedSql.contains("time_updated = CURRENT_TIMESTAMP"),
        "Batch SQL should include time_updated even when only rooms are present");
  }

  private HotelEntity buildHotelEntityWithRoomsAndRates() {
    final RatePlanEntity rate = RatePlanEntity.builder()
        .id("A_" + HOTEL_ID + "_TWIN")
        .amount(BigDecimal.valueOf(100))
        .availability(true)
        .currency("G")
        .minNights(0)
        .maxNights(0)
        .rateClassification("A")
        .rateCode("FLEX")
        .build();

    final RoomEntity room = RoomEntity.builder()
        .id("TWIN_" + HOTEL_ID)
        .roomType("TWIN")
        .quantity(10)
        .rates(new ArrayList<>(List.of(rate)))
        .build();

    return HotelEntity.builder()
        .id(HOTEL_ID)
        .hotelCode(HOTEL_CODE)
        .date(AVAIL_DATE)
        .pmsSource(PMS_SOURCE)
        .rooms(new ArrayList<>(List.of(room)))
        .rates(new ArrayList<>(List.of(rate)))
        .build();
  }

  private HotelEntity buildHotelEntityWithRoomsOnly() {
    final RoomEntity room = RoomEntity.builder()
        .id("TWIN_" + HOTEL_ID)
        .roomType("TWIN")
        .quantity(10)
        .rates(new ArrayList<>())
        .build();

    return HotelEntity.builder()
        .id(HOTEL_ID)
        .hotelCode(HOTEL_CODE)
        .date(AVAIL_DATE)
        .pmsSource(PMS_SOURCE)
        .rooms(new ArrayList<>(List.of(room)))
        .rates(new ArrayList<>())
        .build();
  }
}

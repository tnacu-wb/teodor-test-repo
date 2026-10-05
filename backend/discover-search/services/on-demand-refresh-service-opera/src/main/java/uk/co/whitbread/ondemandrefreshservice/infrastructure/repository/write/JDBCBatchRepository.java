package uk.co.whitbread.ondemandrefreshservice.infrastructure.repository.write;

import static uk.co.whitbread.ondemandrefreshservice.infrastructure.util.DBBatchUtil.insertConflictSafeHotel;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.util.DBBatchUtil.insertUpdateRateQuery;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.util.DBBatchUtil.insertUpdateRoomQuery;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.RoomEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.exceptions.OnDemandRefreshDbException;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.HotelEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.util.DBBatchUtil;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.util.StopWatchLog;

@RequiredArgsConstructor
@Repository
@Slf4j
public class JDBCBatchRepository {

  private final JdbcTemplate jdbcTemplate;

  @StopWatchLog
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void persistHotelAvailabilities(final HotelEntity hotelEntity) throws OnDemandRefreshDbException {
    if (null == hotelEntity) {
      log.info("hotelEntity is null, so no more further processing required..");
      return;
    }
    StringBuilder buildQuery = new StringBuilder();
    buildQuery.append(DBBatchUtil.BATCH_BEGIN);

    String roomQueries = null;
    if (null != hotelEntity.getRooms() && !hotelEntity.getRooms().isEmpty()) {
      roomQueries = hotelEntity.getRooms().stream()
          .map(room -> insertUpdateRoomQuery(room, hotelEntity.getId()))
          .collect(Collectors.joining(""));
    }
    else {
        log.trace("Empty room entities...skipping DB persist for room table");
    }

    String rateQueries = null;
    if (null != hotelEntity.getRooms() && !hotelEntity.getRooms().isEmpty()) {
      rateQueries = hotelEntity.getRooms().stream().filter(room -> isNotEmptyRateEntities(room))
          .map(room -> {
           return room.getRates().stream()
                .map(rate -> insertUpdateRateQuery(rate, hotelEntity.getId(), room.getId()))
                .collect(Collectors.joining(""));

          }).collect(Collectors.joining(""));
    }

    buildQuery.append(insertConflictSafeHotel(hotelEntity));

    if (StringUtils.isNotBlank(roomQueries)) {
      buildQuery.append(roomQueries);
    }

    if (StringUtils.isNotBlank(rateQueries)) {
      buildQuery.append(rateQueries);
    }

    buildQuery.append(DBBatchUtil.updateHotelTimeQuery(hotelEntity.getId()));

    buildQuery.append(DBBatchUtil.BATCH_END);

    try {
      log.trace("buildQuery - {}",buildQuery);
      jdbcTemplate.update(connection -> connection.prepareStatement(buildQuery.toString()));
      log.debug("Saved/Updated hotelAvailabilities for hotel : {}",
          hotelEntity.getId());
    } catch (final Exception e) {
      log.error("Error while trying to save hotelAvailabilities to DB for hotel -  {}", hotelEntity.getId(), e);
      throw new OnDemandRefreshDbException(e.getMessage(), e);
    }
  }

  private boolean isNotEmptyRateEntities(final RoomEntity room) {
    boolean isNotEmptyRateEntities = true;
    if(room.getRates().isEmpty()){
      log.trace("Empty daily rates entities for room type:{}...skipping DB persist for rate table");
      isNotEmptyRateEntities = false;
    }
    return isNotEmptyRateEntities;
  }

}

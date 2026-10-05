package uk.co.whitbread.ondemandrefreshservice.infrastructure.adapters;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.HotelAvailabilitiesBatchOutPort;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.config.JDBCBatchProperties;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.HotelEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.repository.write.JDBCBatchRepository;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.util.StopWatchLog;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelAvailabilityDBBatchService implements HotelAvailabilitiesBatchOutPort {
  private final JDBCBatchRepository jdbcBatchRepository;
  private final JDBCBatchProperties jdbcBatchProperties;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @StopWatchLog
  public void persistHotelEntities(final List<HotelEntity> hotelEntities) {
    try {
      log.debug("Executing persistHotelEntities()....");
      if(hotelEntities.isEmpty()){
        log.info("Hotel entities input list is empty, nothing to persist in DB.");
      }
      persistToDb(hotelEntities);
    }
    catch(final Exception e){
      log.error("Error while persisting hotel entities to DB.Received exception : {}",e);
      retryPersistMessagesToDB(hotelEntities);
    }
  }

  private void persistToDb(final List<HotelEntity> hotelEntities){
    hotelEntities.forEach(hotelEntity -> {
      log.trace("Persist hotel availability in DB for hotel:{}",
          hotelEntity.getHotelCode());
      jdbcBatchRepository.persistHotelAvailabilities(hotelEntity);
    });
  }

  private void retryPersistMessagesToDB(final List<HotelEntity> hotelEntities){
    int retryCount = 1;
    while(retryCount <= jdbcBatchProperties.getMaxRetryLimit()) {
      try {
        log.info("Retry persist to db attempt:{}", retryCount);
        persistToDb(hotelEntities);
        break;
      }
      catch(final Exception e){
        retryCount++;
      }
    }
    if(retryCount > jdbcBatchProperties.getMaxRetryLimit()){
      log.error("Retry attempts to persist to DB exhausted");
    }
  }
}

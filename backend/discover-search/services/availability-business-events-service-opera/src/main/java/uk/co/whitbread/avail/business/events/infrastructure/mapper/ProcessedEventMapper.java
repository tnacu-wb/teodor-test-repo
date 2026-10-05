package uk.co.whitbread.avail.business.events.infrastructure.mapper;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.avail.business.events.infrastructure.entity.ProcessedEventEntity;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.EventHeader;

@Slf4j
public class ProcessedEventMapper {

  private ProcessedEventMapper() {
  }


  public static ProcessedEventEntity convertToProcessedEventEntity(
      final EventHeader eventHeader, final String appKey) {

    final BigInteger offset = new BigInteger(eventHeader.getMetadata().getOffset());
    final String receivedOnString = eventHeader.getTimestamp();

    log.debug("receivedOnString : {}", receivedOnString);
    DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
        .append(DateTimeFormatter.ofPattern("dd-MMM-yy hh.mm.ss.SSSSSS a")).toFormatter();

    LocalDateTime receivedOnDate;

    try {
      receivedOnDate = LocalDateTime.parse(receivedOnString, formatter);
    } catch (DateTimeParseException e) {
      log.error("Error occurred while parsing event time stamp: {}", e.getMessage());
      receivedOnDate = LocalDateTime.now();
    }
    log.debug("receivedOnDate : {}", receivedOnDate);

    final ProcessedEventEntity processedEventEntity =
        ProcessedEventEntity.builder()
            .appKey(appKey)
            .offset(offset)
            .receivedOn(receivedOnDate)
            .build();

    log.debug("availabilityEventEntity : {}", processedEventEntity);

    return processedEventEntity;
  }

}

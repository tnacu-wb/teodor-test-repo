package uk.co.whitbread.avail.business.events.infrastructure.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.avail.business.events.infrastructure.entity.ProcessedEventEntity;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.EventHeader;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.Metadata;

public class ProcessedEventMapperTest {

  final String appKey = "test_app_key";
  final BigInteger offset = BigInteger.valueOf(123);
  String receivedOn = "30-SEP-22 12.38.45.583113 PM";

  @Test
  public void convertToProcessedEventEntityTest(){

    LocalDateTime date = covertDateToLocalDate(receivedOn);

    ProcessedEventEntity processedEventEntityExpected =
        buildProcessedEventEntity(appKey, offset, date);

    ProcessedEventEntity processedEventEntityActual =
    ProcessedEventMapper.convertToProcessedEventEntity(buildEventHeader(), appKey);

    assertEquals(processedEventEntityActual.getAppKey(), processedEventEntityExpected.getAppKey());
  }

  private EventHeader buildEventHeader() {

    final UUID uuid = UUID.randomUUID();
    String eventId = uuid.toString();

    Metadata metadata = Metadata.builder()
        .offset(String.valueOf(offset))
        .uniqueEventId(eventId)
        .build();

    return EventHeader.builder()
        .metadata(metadata)
        .timestamp(receivedOn)
        .build();

  }

  private ProcessedEventEntity buildProcessedEventEntity(
      final String appKey, final BigInteger offset, final LocalDateTime receivedOnDate) {

    return
        ProcessedEventEntity.builder()
            .appKey(appKey)
            .offset(offset)
            .receivedOn(receivedOnDate)
            .build();

  }

  private LocalDateTime covertDateToLocalDate(final String date) {
    DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
        .append(DateTimeFormatter.ofPattern("dd-MMM-yy hh.mm.ss.SSSSSS a")).toFormatter();

    LocalDateTime receivedOnDate;

    try {
      receivedOnDate = LocalDateTime.parse(date, formatter);
    } catch (DateTimeParseException e) {
      receivedOnDate = LocalDateTime.now();
    }
    return receivedOnDate;
  }



}

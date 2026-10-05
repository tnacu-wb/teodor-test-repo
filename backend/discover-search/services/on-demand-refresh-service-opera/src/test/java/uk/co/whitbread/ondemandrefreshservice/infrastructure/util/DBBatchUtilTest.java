package uk.co.whitbread.ondemandrefreshservice.infrastructure.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DBBatchUtilTest {

  private static final String HOTEL_ID = "TKINPT_OPERA_2026-05-18";

  @Test
  public void updateHotelTimeQueryShouldContainCurrentTimestamp() {
    final String query = DBBatchUtil.updateHotelTimeQuery(HOTEL_ID);

    assertTrue(query.contains("CURRENT_TIMESTAMP"),
        "Query should use CURRENT_TIMESTAMP for timezone-safe timestamp");
    assertTrue(query.contains(HOTEL_ID),
        "Query should contain the hotel ID");
    assertTrue(query.contains("update avail_cache.hotel_ac"),
        "Query should update the hotel_ac table");
    assertTrue(query.contains("time_updated"),
        "Query should update the time_updated column");
  }

  @Test
  public void updateHotelTimeQueryShouldNotContainTimezoneFunction() {
    final String query = DBBatchUtil.updateHotelTimeQuery(HOTEL_ID);

    assertTrue(!query.contains("timezone("),
        "Query should not use timezone() function with TIMESTAMPTZ column");
  }

  @Test
  public void updateHotelTimeQueryShouldProduceExpectedSql() {
    final String query = DBBatchUtil.updateHotelTimeQuery(HOTEL_ID);

    final String expected =
        "update avail_cache.hotel_ac set time_updated = CURRENT_TIMESTAMP where id = '"
            + HOTEL_ID + "'; ";
    assertEquals(expected, query);
  }
}

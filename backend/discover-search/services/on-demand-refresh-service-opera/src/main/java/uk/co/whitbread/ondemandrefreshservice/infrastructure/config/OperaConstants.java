package uk.co.whitbread.ondemandrefreshservice.infrastructure.config;

public class OperaConstants {

  private OperaConstants() {
    throw new IllegalStateException("OperaConstants class");
  }

  public static final String APP_KEY_HEADER = "x-app-key";
  public static final String HOTEL_ID_HEADER = "x-hotelid";
  public static final String ENTERPRISE_ID = "enterpriseId";
  public static final String REGISTRATION_ID = "ohip";
  public static final String MIGRATION_CATEGORY = "MIG";
  public static final String MIGRATION_STATUS_FALSE = "MIG_NO";
  public static final String MIGRATION_PMS_SOURCE_OPERA = "OPERA";
  public static final String MIGRATION_PMS_SOURCE_BART = "BART";
  public static final String MIGRATION_CATEGORY_PMS = "PMS";
  public static final String MIGRATION_CATEGORY_ONSALE = "ONSALE";
  public static final String MIGRATION_ONSALE_TRUE = "True";
  public static final String MIGRATION_ONSALE_FALSE = "False";
  public static final String LIMIT = "limit";
  public static final String START_DATE = "startDate";
  public static final String END_DATE = "endDate";
  public static final String DATE_RANGE_START = "dateRangeStart";
  public static final String DATE_RANGE_END = "dateRangeEnd";
  public static final String DAILY_INVENTORY = "dailyInventory";
  public static final String ROOM_COUNT_REQUESTED = "roomCountRequested";
  public static final String HOUSE_LEVEL = "houseLevel";

}
